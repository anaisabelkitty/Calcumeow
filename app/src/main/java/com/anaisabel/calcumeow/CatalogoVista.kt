package com.anaisabel.calcumeow

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.anaisabel.calcumeow.calculos.DistanciaCalculo
import com.anaisabel.calcumeow.calculos.GasolinaCalculo
import com.anaisabel.calcumeow.calculos.PropinaCalculo
import com.anaisabel.calcumeow.calculos.SistemaCalculo
import com.anaisabel.calcumeow.modelo.ClaveResultado

// ---------- Tipos de vista ----------
// Los cálculos solo saben de números; estas clases dicen cómo se ven en pantalla
// (textos, íconos, unidades). Todo apunta a recursos, así cambia solo con el idioma.

/** Textos y unidad de un campo del formulario (todo apunta a strings.xml). */
data class CampoVista(
    @param:StringRes val etiqueta: Int,
    @param:StringRes val ayuda: Int,
    @param:StringRes val unidad: Int? = null
)

/** Todo lo visual de un cálculo: textos, ícono, imagen de la fórmula y datos de cada campo. */
data class CalculoVista(
    val id: String,
    @param:StringRes val titulo: Int,
    @param:StringRes val nube: Int,
    @param:StringRes val leyenda: Int,
    @param:DrawableRes val icono: Int,
    @param:DrawableRes val formula: Int,
    @param:StringRes val descripcionFormula: Int,
    val campos: Map<String, CampoVista>
)

/** Cómo se muestra cada dato del resultado: su etiqueta y la unidad (si tiene) que va después del número. */
data class ResultadoVista(
    @param:StringRes val etiqueta: Int,
    @param:StringRes val unidad: Int? = null
)

/** Une cada cálculo con su vista, y cada clave de resultado con su etiqueta y unidad. */
object CatalogoVista {

    // ---------- Vistas de los cálculos ----------
    // Cada vista se enlaza con su cálculo por id. Los campos usan las mismas constantes
    // que el cálculo (por ejemplo PropinaCalculo.CUENTA).
    private val vistas: List<CalculoVista> = listOf(
        // Propina y cuenta dividida
        CalculoVista(
            id = PropinaCalculo.id,
            titulo = R.string.calc_propina_titulo,
            nube = R.string.calc_propina_nube,
            leyenda = R.string.calc_propina_leyenda,
            icono = R.drawable.ic_recibo,
            formula = R.drawable.formula_propina,
            descripcionFormula = R.string.cd_formula_propina,
            campos = mapOf(
                PropinaCalculo.CUENTA to CampoVista(R.string.campo_cuenta, R.string.ayuda_cuenta, R.string.unidad_dinero),
                PropinaCalculo.PROPINA to CampoVista(R.string.campo_propina, R.string.ayuda_propina, R.string.unidad_porcentaje),
                PropinaCalculo.PERSONAS to CampoVista(R.string.campo_personas, R.string.ayuda_personas, R.string.unidad_personas)
            )
        ),
        // Distancia entre dos puntos
        CalculoVista(
            id = DistanciaCalculo.id,
            titulo = R.string.calc_distancia_titulo,
            nube = R.string.calc_distancia_nube,
            leyenda = R.string.calc_distancia_leyenda,
            icono = R.drawable.ic_regla,
            formula = R.drawable.formula_distancia,
            descripcionFormula = R.string.cd_formula_distancia,
            campos = mapOf(
                DistanciaCalculo.X1 to CampoVista(R.string.campo_x1, R.string.ayuda_x1, R.string.unidad_plano),
                DistanciaCalculo.Y1 to CampoVista(R.string.campo_y1, R.string.ayuda_y1, R.string.unidad_plano),
                DistanciaCalculo.X2 to CampoVista(R.string.campo_x2, R.string.ayuda_x2, R.string.unidad_plano),
                DistanciaCalculo.Y2 to CampoVista(R.string.campo_y2, R.string.ayuda_y2, R.string.unidad_plano)
            )
        ),
        // Sistema de ecuaciones 2x2 (los coeficientes no llevan unidad)
        CalculoVista(
            id = SistemaCalculo.id,
            titulo = R.string.calc_sistema_titulo,
            nube = R.string.calc_sistema_nube,
            leyenda = R.string.calc_sistema_leyenda,
            icono = R.drawable.ic_funcion,
            formula = R.drawable.formula_sistema,
            descripcionFormula = R.string.cd_formula_sistema,
            campos = mapOf(
                SistemaCalculo.A1 to CampoVista(R.string.campo_a1, R.string.ayuda_coef_x),
                SistemaCalculo.B1 to CampoVista(R.string.campo_b1, R.string.ayuda_coef_y),
                SistemaCalculo.C1 to CampoVista(R.string.campo_c1, R.string.ayuda_constante),
                SistemaCalculo.A2 to CampoVista(R.string.campo_a2, R.string.ayuda_coef_x),
                SistemaCalculo.B2 to CampoVista(R.string.campo_b2, R.string.ayuda_coef_y),
                SistemaCalculo.C2 to CampoVista(R.string.campo_c2, R.string.ayuda_constante)
            )
        ),
        // Gasolina del viaje
        CalculoVista(
            id = GasolinaCalculo.id,
            titulo = R.string.calc_gasolina_titulo,
            nube = R.string.calc_gasolina_nube,
            leyenda = R.string.calc_gasolina_leyenda,
            icono = R.drawable.ic_gasolina,
            formula = R.drawable.formula_gasolina,
            descripcionFormula = R.string.cd_formula_gasolina,
            campos = mapOf(
                GasolinaCalculo.DISTANCIA to CampoVista(R.string.campo_distancia, R.string.ayuda_distancia, R.string.unidad_km),
                GasolinaCalculo.RENDIMIENTO to CampoVista(R.string.campo_rendimiento, R.string.ayuda_rendimiento, R.string.unidad_km_litro),
                GasolinaCalculo.PRECIO to CampoVista(R.string.campo_precio, R.string.ayuda_precio, R.string.unidad_dinero_litro),
                GasolinaCalculo.PERSONAS to CampoVista(R.string.campo_personas, R.string.ayuda_personas, R.string.unidad_personas)
            )
        )
    )

    // ---------- Búsquedas ----------

    /** La vista del cálculo con ese id. */
    fun de(idCalculo: String): CalculoVista = vistas.first { it.id == idCalculo }

    /**
     * Etiqueta y unidad de un dato del resultado. El pago por persona de propina y de gasolina
     * comparten texto; x e y no tienen unidad.
     */
    fun resultado(clave: ClaveResultado): ResultadoVista = when (clave) {
        ClaveResultado.TOTAL_CON_PROPINA -> ResultadoVista(R.string.res_total_con_propina, R.string.unidad_dinero)
        ClaveResultado.PAGO_POR_PERSONA,
        ClaveResultado.COSTO_POR_PERSONA -> ResultadoVista(R.string.res_pago_por_persona, R.string.unidad_dinero)
        ClaveResultado.DISTANCIA -> ResultadoVista(R.string.res_distancia, R.string.unidad_plano)
        ClaveResultado.VALOR_X -> ResultadoVista(R.string.res_valor_x)
        ClaveResultado.VALOR_Y -> ResultadoVista(R.string.res_valor_y)
        ClaveResultado.LITROS -> ResultadoVista(R.string.res_litros, R.string.unidad_litros)
        ClaveResultado.COSTO_VIAJE -> ResultadoVista(R.string.res_costo_viaje, R.string.unidad_dinero)
    }
}
