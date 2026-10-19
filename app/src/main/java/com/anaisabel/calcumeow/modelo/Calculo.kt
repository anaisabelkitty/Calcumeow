package com.anaisabel.calcumeow.modelo

// ---------- Campos del formulario ----------

/**
 * Un campo del formulario.
 * [fila] indica en cuál de los dos renglones se muestra (0 o 1); dentro de cada renglón
 * los campos se acomodan en el orden de la lista y se reparten el ancho por partes iguales.
 */
data class Campo(
    val id: String,
    val regla: Regla,
    val fila: Int
) {
    /** Los campos que solo aceptan enteros no necesitan teclado decimal. */
    val soloEntero: Boolean
        get() = regla is Regla.EnteroDe1Hasta

    /** Solo se muestra la tecla de signo menos cuando la regla admite valores negativos. */
    val permiteNegativo: Boolean
        get() = regla is Regla.EntreInclusive && regla.min < 0.0
}

// ---------- Resultados ----------
// Cada cálculo devuelve una lista de Resultado; la pantalla usa la clave para saber
// qué etiqueta y qué unidad mostrar (ver CatalogoVista).

/** Cada dato que se muestra en la tarjeta de resultado. */
enum class ClaveResultado {
    TOTAL_CON_PROPINA,
    PAGO_POR_PERSONA,
    DISTANCIA,
    VALOR_X,
    VALOR_Y,
    LITROS,
    COSTO_VIAJE,
    COSTO_POR_PERSONA
}

/** Un dato calculado: qué es ([clave]) y cuánto vale ([valor]). */
data class Resultado(val clave: ClaveResultado, val valor: Double)

// ---------- Contrato de un cálculo ----------
// CalculoActivity trabaja con esta interfaz, no con un cálculo en concreto.

/** Contrato que cumple cada uno de los cuatro cálculos. */
interface Calculo {
    /** Nombre único con el que el menú le pasa el cálculo a la pantalla. */
    val id: String

    /** Campos que pide el formulario, en el orden en que se muestran. */
    val campos: List<Campo>

    /** Valida todos los campos y las relaciones entre ellos. [textos] va por id de campo. */
    fun validar(textos: Map<String, String>): ResultadoValidacion

    /** Calcula el resultado. Solo se debe llamar cuando [validar] no encontró errores. */
    fun calcular(textos: Map<String, String>): List<Resultado>
}

// ---------- Funciones de apoyo para los cálculos ----------

/** Validación común: revisa cada campo con su regla. */
internal fun Calculo.validarCampos(textos: Map<String, String>): Map<String, ErrorCampo> {
    val errores = mutableMapOf<String, ErrorCampo>()
    for (campo in campos) {
        val error = Validador.validarCampo(textos[campo.id].orEmpty(), campo.regla)
        if (error != null) errores[campo.id] = error
    }
    return errores
}

/** Lee un valor que ya fue validado. */
internal fun Map<String, String>.numero(id: String): Double =
    requireNotNull(Validador.leerNumero(this[id].orEmpty())) { "El campo $id no es un número" }
