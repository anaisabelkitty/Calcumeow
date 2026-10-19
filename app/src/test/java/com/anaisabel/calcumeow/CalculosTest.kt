package com.anaisabel.calcumeow

import com.anaisabel.calcumeow.calculos.DistanciaCalculo
import com.anaisabel.calcumeow.calculos.GasolinaCalculo
import com.anaisabel.calcumeow.calculos.PropinaCalculo
import com.anaisabel.calcumeow.calculos.SistemaCalculo
import com.anaisabel.calcumeow.modelo.ClaveResultado
import com.anaisabel.calcumeow.modelo.ErrorCampo
import com.anaisabel.calcumeow.modelo.ErrorGeneral
import com.anaisabel.calcumeow.modelo.Resultado
import com.anaisabel.calcumeow.modelo.Validador
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pruebas de la lógica de los cuatro cálculos y de su validación (sin Android). */
class CalculosTest {

    /** Busca en la lista de resultados el valor de una clave (por ejemplo, el total con propina). */
    private fun valor(resultados: List<Resultado>, clave: ClaveResultado): Double =
        resultados.first { it.clave == clave }.valor

    // ---------- Lectura de números ----------
    // Se acepta coma o punto y espacios alrededor; se rechaza lo vacío, el texto y NaN/infinito.

    @Test
    fun leerNumero_aceptaPuntoComaYEspacios() {
        assertEquals(1.5, Validador.leerNumero("1,5")!!, 1e-12)
        assertEquals(7.0, Validador.leerNumero(" 7 ")!!, 1e-12)
        assertEquals(-2.25, Validador.leerNumero("-2.25")!!, 1e-12)
    }

    @Test
    fun leerNumero_rechazaTextoNoNumerico() {
        assertNull(Validador.leerNumero(""))
        assertNull(Validador.leerNumero("abc"))
        assertNull(Validador.leerNumero("-"))
        assertNull(Validador.leerNumero("NaN"))
        assertNull(Validador.leerNumero("Infinity"))
    }

    // ---------- Propina y cuenta dividida ----------
    // Resultado correcto con datos válidos, errores por campo, y los bordes (propina 0, negativos, cuenta 0).

    @Test
    fun propina_calculaTotalYPagoPorPersona() {
        val datos = mapOf("cuenta" to "850", "propina" to "20", "personas" to "4")
        assertTrue(PropinaCalculo.validar(datos).esValido)

        val r = PropinaCalculo.calcular(datos)
        assertEquals(1020.0, valor(r, ClaveResultado.TOTAL_CON_PROPINA), 1e-9)
        assertEquals(255.0, valor(r, ClaveResultado.PAGO_POR_PERSONA), 1e-9)
    }

    @Test
    fun propina_validaRangosYTipos() {
        val datos = mapOf("cuenta" to "", "propina" to "120", "personas" to "2.5")
        val errores = PropinaCalculo.validar(datos).errores

        assertEquals(ErrorCampo.Obligatorio, errores["cuenta"])
        assertEquals(ErrorCampo.MenorOIgualQue(100.0), errores["propina"])
        assertEquals(ErrorCampo.EnteroMayorQueCero, errores["personas"])
    }

    @Test
    fun propina_aceptaCeroPeroNoNegativos() {
        val conCero = mapOf("cuenta" to "100", "propina" to "0", "personas" to "1")
        assertTrue(PropinaCalculo.validar(conCero).esValido)

        val negativa = mapOf("cuenta" to "100", "propina" to "-5", "personas" to "1")
        assertEquals(ErrorCampo.NoNegativo, PropinaCalculo.validar(negativa).errores["propina"])

        val cuentaCero = mapOf("cuenta" to "0", "propina" to "10", "personas" to "1")
        assertEquals(ErrorCampo.MayorQueCero, PropinaCalculo.validar(cuentaCero).errores["cuenta"])
    }

    // ---------- Distancia entre dos puntos ----------
    // Puntos positivos, con coordenadas negativas (ambos dan 5, un triángulo 3-4-5) y campos incompletos.

    @Test
    fun distancia_calculaConPuntosPositivos() {
        val datos = mapOf("x1" to "1", "y1" to "2", "x2" to "4", "y2" to "6")
        assertTrue(DistanciaCalculo.validar(datos).esValido)
        assertEquals(5.0, valor(DistanciaCalculo.calcular(datos), ClaveResultado.DISTANCIA), 1e-9)
    }

    @Test
    fun distancia_aceptaCoordenadasNegativas() {
        val datos = mapOf("x1" to "-1", "y1" to "-1", "x2" to "2", "y2" to "3")
        assertTrue(DistanciaCalculo.validar(datos).esValido)
        assertEquals(5.0, valor(DistanciaCalculo.calcular(datos), ClaveResultado.DISTANCIA), 1e-9)
    }

    @Test
    fun distancia_rechazaTextoIncompleto() {
        val datos = mapOf("x1" to "1", "y1" to "", "x2" to "-", "y2" to "6")
        val errores = DistanciaCalculo.validar(datos).errores

        assertEquals(ErrorCampo.Obligatorio, errores["y1"])
        assertEquals(ErrorCampo.NumeroInvalido, errores["x2"])
        assertFalse(errores.containsKey("x1"))
    }

    // ---------- Sistema de ecuaciones 2x2 ----------
    // Solución única, determinante 0 (sin solución única) y que ese error general
    // no aparezca mientras haya campos inválidos.

    @Test
    fun sistema_resuelveSolucionUnica() {
        // 2x + y = 5  y  x - y = 1  ->  x = 2, y = 1
        val datos = mapOf(
            "a1" to "2", "b1" to "1", "c1" to "5",
            "a2" to "1", "b2" to "-1", "c2" to "1"
        )
        assertTrue(SistemaCalculo.validar(datos).esValido)

        val r = SistemaCalculo.calcular(datos)
        assertEquals(2.0, valor(r, ClaveResultado.VALOR_X), 1e-9)
        assertEquals(1.0, valor(r, ClaveResultado.VALOR_Y), 1e-9)
    }

    @Test
    fun sistema_rechazaDeterminanteCero() {
        val datos = mapOf(
            "a1" to "1", "b1" to "2", "c1" to "3",
            "a2" to "2", "b2" to "4", "c2" to "6"
        )
        val validacion = SistemaCalculo.validar(datos)

        assertTrue(validacion.errores.isEmpty())
        assertEquals(ErrorGeneral.SISTEMA_SIN_SOLUCION_UNICA, validacion.errorGeneral)
        assertFalse(validacion.esValido)
    }

    @Test
    fun sistema_noRevisaElDeterminanteSiHayCamposInvalidos() {
        val datos = mapOf(
            "a1" to "", "b1" to "2", "c1" to "3",
            "a2" to "2", "b2" to "4", "c2" to "6"
        )
        val validacion = SistemaCalculo.validar(datos)

        assertEquals(ErrorCampo.Obligatorio, validacion.errores["a1"])
        assertNull(validacion.errorGeneral)
    }

    // ---------- Gasolina del viaje ----------
    // Litros, costo y pago por persona con datos válidos, y que todos los campos exijan valores positivos dentro de su límite.

    @Test
    fun gasolina_calculaLitrosCostoYPagoPorPersona() {
        val datos = mapOf(
            "distancia" to "320", "rendimiento" to "14",
            "precio" to "24.5", "personas" to "4"
        )
        assertTrue(GasolinaCalculo.validar(datos).esValido)

        val r = GasolinaCalculo.calcular(datos)
        assertEquals(22.857142857, valor(r, ClaveResultado.LITROS), 1e-6)
        assertEquals(560.0, valor(r, ClaveResultado.COSTO_VIAJE), 1e-9)
        assertEquals(140.0, valor(r, ClaveResultado.COSTO_POR_PERSONA), 1e-9)
    }

    @Test
    fun gasolina_validaQueTodoSeaPositivoYConLimites() {
        val datos = mapOf(
            "distancia" to "0", "rendimiento" to "500",
            "precio" to "-3", "personas" to "0"
        )
        val errores = GasolinaCalculo.validar(datos).errores

        assertEquals(ErrorCampo.MayorQueCero, errores["distancia"])
        assertEquals(ErrorCampo.MenorOIgualQue(100.0), errores["rendimiento"])
        assertEquals(ErrorCampo.MayorQueCero, errores["precio"])
        assertEquals(ErrorCampo.EnteroMayorQueCero, errores["personas"])
    }
}
