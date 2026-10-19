package com.anaisabel.calcumeow.calculos

import com.anaisabel.calcumeow.modelo.Calculo
import com.anaisabel.calcumeow.modelo.Campo
import com.anaisabel.calcumeow.modelo.ClaveResultado
import com.anaisabel.calcumeow.modelo.ErrorGeneral
import com.anaisabel.calcumeow.modelo.Regla
import com.anaisabel.calcumeow.modelo.Resultado
import com.anaisabel.calcumeow.modelo.ResultadoValidacion
import com.anaisabel.calcumeow.modelo.numero
import com.anaisabel.calcumeow.modelo.validarCampos
import kotlin.math.abs

/**
 * Sistema de ecuaciones lineales 2x2 (regla de Cramer).
 *   a1 x + b1 y = c1
 *   a2 x + b2 y = c2
 * D = a1 b2 - a2 b1
 * x = (c1 b2 - c2 b1) / D
 * y = (a1 c2 - a2 c1) / D
 * Relación entre campos: D no puede ser 0, porque entonces no hay una única solución.
 */
object SistemaCalculo : Calculo {
    // ---------- Ids de los campos (también los usa la pantalla para sus textos) ----------
    const val A1 = "a1"
    const val B1 = "b1"
    const val C1 = "c1"
    const val A2 = "a2"
    const val B2 = "b2"
    const val C2 = "c2"

    // ---------- Configuración del formulario ----------
    // Fila 0 = primera ecuación (a1, b1, c1); fila 1 = segunda (a2, b2, c2).
    // Un determinante menor que DETERMINANTE_MINIMO se considera cero.
    private const val DETERMINANTE_MINIMO = 1e-12
    private val rangoCoeficiente = Regla.EntreInclusive(-1_000_000.0, 1_000_000.0)

    override val id = "sistema"

    override val campos = listOf(
        Campo(A1, rangoCoeficiente, fila = 0),
        Campo(B1, rangoCoeficiente, fila = 0),
        Campo(C1, rangoCoeficiente, fila = 0),
        Campo(A2, rangoCoeficiente, fila = 1),
        Campo(B2, rangoCoeficiente, fila = 1),
        Campo(C2, rangoCoeficiente, fila = 1)
    )

    // ---------- Validación ----------
    // En dos pasos: primero cada campo por separado y, solo si todos son válidos,
    // la relación entre ellos (que el determinante no sea 0).
    override fun validar(textos: Map<String, String>): ResultadoValidacion {
        val errores = validarCampos(textos)
        if (errores.isNotEmpty()) return ResultadoValidacion(errores)

        val determinante = determinante(textos)
        val general = if (abs(determinante) < DETERMINANTE_MINIMO) {
            ErrorGeneral.SISTEMA_SIN_SOLUCION_UNICA
        } else {
            null
        }
        return ResultadoValidacion(errores, general)
    }

    // ---------- Cálculo ----------
    override fun calcular(textos: Map<String, String>): List<Resultado> {
        // Coeficientes de las dos ecuaciones, ya validados
        val a1 = textos.numero(A1)
        val b1 = textos.numero(B1)
        val c1 = textos.numero(C1)
        val a2 = textos.numero(A2)
        val b2 = textos.numero(B2)
        val c2 = textos.numero(C2)
        val d = a1 * b2 - a2 * b1

        // Regla de Cramer: cada incógnita es un determinante entre D
        return listOf(
            Resultado(ClaveResultado.VALOR_X, (c1 * b2 - c2 * b1) / d),
            Resultado(ClaveResultado.VALOR_Y, (a1 * c2 - a2 * c1) / d)
        )
    }

    // ---------- Apoyo ----------
    // D = a1 b2 - a2 b1; se usa para validar antes de calcular.
    private fun determinante(textos: Map<String, String>): Double =
        textos.numero(A1) * textos.numero(B2) - textos.numero(A2) * textos.numero(B1)
}
