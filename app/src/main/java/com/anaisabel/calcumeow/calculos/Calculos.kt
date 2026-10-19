package com.anaisabel.calcumeow.calculos

import com.anaisabel.calcumeow.modelo.Calculo

/** Lista de los cálculos disponibles, en el orden en que aparecen en el menú. */
object Calculos {

    // ---------- Catálogo ----------
    val todos: List<Calculo> = listOf(
        PropinaCalculo,
        DistanciaCalculo,
        SistemaCalculo,
        GasolinaCalculo
    )

    // ---------- Búsqueda ----------
    // La pantalla de cálculo recibe solo el id desde el menú y lo convierte aquí en el cálculo real.
    fun porId(id: String): Calculo? = todos.firstOrNull { it.id == id }
}
