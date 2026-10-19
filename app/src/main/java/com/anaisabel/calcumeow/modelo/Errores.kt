package com.anaisabel.calcumeow.modelo

// ---------- Errores de un campo ----------

/**
 * Errores que puede tener un campo del formulario.
 * Esta clase no depende de Android: la pantalla se encarga de convertir cada error
 * en el texto que corresponde (strings.xml) según el idioma.
 */
sealed interface ErrorCampo {
    data object Obligatorio : ErrorCampo
    data object NumeroInvalido : ErrorCampo
    data object MayorQueCero : ErrorCampo
    data object NoNegativo : ErrorCampo
    data object EnteroMayorQueCero : ErrorCampo
    data class MenorOIgualQue(val limite: Double) : ErrorCampo
    data class MayorOIgualQue(val limite: Double) : ErrorCampo
}

// ---------- Error de varios campos a la vez ----------

/** Errores que dependen de la relación entre varios campos. */
enum class ErrorGeneral {
    SISTEMA_SIN_SOLUCION_UNICA
}

// ---------- Resultado de validar el formulario ----------
// [errores] va por id de campo; el formulario es válido solo si no hay ninguno de los dos tipos.

/** Resultado de validar todo el formulario de un cálculo. */
class ResultadoValidacion(
    val errores: Map<String, ErrorCampo>,
    val errorGeneral: ErrorGeneral? = null
) {
    val esValido: Boolean
        get() = errores.isEmpty() && errorGeneral == null
}
