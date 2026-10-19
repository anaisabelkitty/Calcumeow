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
 * Propina y cuenta dividida.
 * total = cuenta * (1 + propina / 100)
 * por persona = total / personas
 */
object PropinaCalculo : Calculo {
    // ---------- Ids de los campos (también los usa la pantalla para sus textos) ----------
    const val CUENTA = "cuenta"
    const val PROPINA = "propina"
    const val PERSONAS = "personas"

    // ---------- Configuración del formulario ----------
    // Fila 0 = la cuenta; fila 1 = propina y personas. La propina puede ser 0 pero no negativa.
    override val id = "propina"

    override val campos = listOf(
        Campo(CUENTA, Regla.MayorQueCeroHasta(1_000_000.0), fila = 0),
        Campo(PROPINA, Regla.EntreInclusive(0.0, 100.0), fila = 1),
        Campo(PERSONAS, Regla.EnteroDe1Hasta(100), fila = 1)
    )

    // ---------- Validación ----------
    // Solo se revisa cada campo por separado; no hay relación entre ellos.
    override fun validar(textos: Map<String, String>) =
        ResultadoValidacion(validarCampos(textos))

    // ---------- Cálculo ----------
    override fun calcular(textos: Map<String, String>): List<Resultado> {
        // Datos ya validados, leídos como números
        val cuenta = textos.numero(CUENTA)
        val propina = textos.numero(PROPINA)
        val personas = textos.numero(PERSONAS)

        // Primero se suma la propina a la cuenta y después se reparte entre las personas
        val total = cuenta * (1 + propina / 100)
        val porPersona = total / personas

        return listOf(
            Resultado(ClaveResultado.TOTAL_CON_PROPINA, total),
            Resultado(ClaveResultado.PAGO_POR_PERSONA, porPersona)
        )
    }
}
