package com.anaisabel.calcumeow.calculos

import com.anaisabel.calcumeow.modelo.Calculo
import com.anaisabel.calcumeow.modelo.Campo
import com.anaisabel.calcumeow.modelo.ClaveResultado
import com.anaisabel.calcumeow.modelo.Regla
import com.anaisabel.calcumeow.modelo.Resultado
import com.anaisabel.calcumeow.modelo.ResultadoValidacion
import com.anaisabel.calcumeow.modelo.numero
import com.anaisabel.calcumeow.modelo.validarCampos

/**
 * Gasolina del viaje.
 * litros = distancia / rendimiento
 * costo = litros * precio por litro
 * por persona = costo / personas
 */
object GasolinaCalculo : Calculo {
    // ---------- Ids de los campos (también los usa la pantalla para sus textos) ----------
    const val DISTANCIA = "distancia"
    const val RENDIMIENTO = "rendimiento"
    const val PRECIO = "precio"
    const val PERSONAS = "personas"

    // ---------- Configuración del formulario ----------
    // Fila 0 = distancia y rendimiento; fila 1 = precio y personas.
    // Todos deben ser mayores que cero y tienen un máximo.
    override val id = "gasolina"

    override val campos = listOf(
        Campo(DISTANCIA, Regla.MayorQueCeroHasta(100_000.0), fila = 0),
        Campo(RENDIMIENTO, Regla.MayorQueCeroHasta(100.0), fila = 0),
        Campo(PRECIO, Regla.MayorQueCeroHasta(1_000.0), fila = 1),
        Campo(PERSONAS, Regla.EnteroDe1Hasta(100), fila = 1)
    )

    // ---------- Validación ----------
    // Solo se revisa cada campo por separado; no hay relación entre ellos.
    override fun validar(textos: Map<String, String>) =
        ResultadoValidacion(validarCampos(textos))

    // ---------- Cálculo ----------
    override fun calcular(textos: Map<String, String>): List<Resultado> {
        // Cada paso usa el anterior: litros -> costo del viaje -> costo por persona
        val litros = textos.numero(DISTANCIA) / textos.numero(RENDIMIENTO)
        val costo = litros * textos.numero(PRECIO)
        val porPersona = costo / textos.numero(PERSONAS)

        // Se muestran los tres datos, en este orden, en la tarjeta de resultado
        return listOf(
            Resultado(ClaveResultado.LITROS, litros),
            Resultado(ClaveResultado.COSTO_VIAJE, costo),
            Resultado(ClaveResultado.COSTO_POR_PERSONA, porPersona)
        )
    }
}
