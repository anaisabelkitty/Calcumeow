package com.anaisabel.calcumeow.modelo

import kotlin.math.floor

// ---------- Reglas ----------
// Cada campo de un cálculo declara una de estas reglas; el Validador sabe aplicarlas.

/** Regla de validación numérica que se aplica a un campo. */
sealed interface Regla {
    /** Número mayor que 0 y menor o igual que [max]. */
    data class MayorQueCeroHasta(val max: Double) : Regla

    /** Número entre [min] y [max], ambos incluidos. Permite negativos si [min] es negativo. */
    data class EntreInclusive(val min: Double, val max: Double) : Regla

    /** Número entero entre 1 y [max]. */
    data class EnteroDe1Hasta(val max: Int) : Regla
}

// ---------- Validador ----------
// Lee el texto que escribió la persona y comprueba que cumpla la regla del campo.

/** Convierte textos en números y revisa que cumplan su [Regla]. No depende de Android. */
object Validador {

    /**
     * Convierte el texto escrito en un número.
     * Acepta coma o punto como separador decimal y rechaza NaN e infinitos.
     */
    fun leerNumero(texto: String): Double? {
        val limpio = texto.trim().replace(',', '.')
        val numero = limpio.toDoubleOrNull() ?: return null
        return if (numero.isFinite()) numero else null
    }

    /** Devuelve el error del campo o null si el texto cumple la regla. */
    fun validarCampo(texto: String, regla: Regla): ErrorCampo? {
        // Primero lo básico: que haya texto y que sea un número
        if (texto.isBlank()) return ErrorCampo.Obligatorio
        val valor = leerNumero(texto) ?: return ErrorCampo.NumeroInvalido

        // Después, el límite propio de cada tipo de regla
        return when (regla) {
            is Regla.MayorQueCeroHasta -> when {
                valor <= 0.0 -> ErrorCampo.MayorQueCero
                valor > regla.max -> ErrorCampo.MenorOIgualQue(regla.max)
                else -> null
            }

            is Regla.EntreInclusive -> when {
                valor > regla.max -> ErrorCampo.MenorOIgualQue(regla.max)
                valor < regla.min ->
                    if (regla.min == 0.0) ErrorCampo.NoNegativo else ErrorCampo.MayorOIgualQue(regla.min)

                else -> null
            }

            is Regla.EnteroDe1Hasta -> when {
                valor < 1.0 || valor != floor(valor) -> ErrorCampo.EnteroMayorQueCero
                valor > regla.max -> ErrorCampo.MenorOIgualQue(regla.max.toDouble())
                else -> null
            }
        }
    }
}
