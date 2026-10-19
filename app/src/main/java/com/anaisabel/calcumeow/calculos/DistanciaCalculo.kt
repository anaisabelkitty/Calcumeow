package com.anaisabel.calcumeow.calculos

import com.anaisabel.calcumeow.modelo.Calculo
import com.anaisabel.calcumeow.modelo.Campo
import com.anaisabel.calcumeow.modelo.ClaveResultado
import com.anaisabel.calcumeow.modelo.Regla
import com.anaisabel.calcumeow.modelo.Resultado
import com.anaisabel.calcumeow.modelo.ResultadoValidacion
import com.anaisabel.calcumeow.modelo.numero
import com.anaisabel.calcumeow.modelo.validarCampos
import kotlin.math.sqrt

/**
 * Distancia entre dos puntos del plano.
 * d = raíz((x2 - x1)^2 + (y2 - y1)^2)
 * Las coordenadas pueden ser negativas.
 */
object DistanciaCalculo : Calculo {
    // ---------- Ids de los campos (también los usa la pantalla para sus textos) ----------
    const val X1 = "x1"
    const val Y1 = "y1"
    const val X2 = "x2"
    const val Y2 = "y2"

    // ---------- Configuración del formulario ----------
    // Fila 0 = primer punto (x1, y1); fila 1 = segundo punto (x2, y2). Todas aceptan negativos.
    private val rangoCoordenada =Regla.EntreInclusive(-1_000_000.0, 1_000_000.0)

    override val id = "distancia"

    override val campos = listOf(
        Campo(X1, rangoCoordenada, fila = 0),
        Campo(Y1, rangoCoordenada, fila = 0),
        Campo(X2, rangoCoordenada, fila = 1),
        Campo(Y2, rangoCoordenada, fila = 1)
    )

    // ---------- Validación ----------
    // Solo se revisa cada campo por separado; no hay relación entre ellos.
    override fun validar(textos: Map<String, String>) =
        ResultadoValidacion(validarCampos(textos))

    // ---------- Cálculo ----------
    override fun calcular(textos: Map<String, String>): List<Resultado> {
        // Diferencia entre los puntos en cada eje
        val dx = textos.numero(X2) - textos.numero(X1)
        val dy = textos.numero(Y2) - textos.numero(Y1)

        // Teorema de Pitágoras: la distancia es la hipotenusa de dx y dy
        return listOf(Resultado(ClaveResultado.DISTANCIA, sqrt(dx * dx + dy * dy)))
    }
}
