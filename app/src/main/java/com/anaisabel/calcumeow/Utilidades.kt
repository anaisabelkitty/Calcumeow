package com.anaisabel.calcumeow

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.text.style.SubscriptSpan
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

// ---------- Idioma ----------

/** Cambio de idioma de la app entre español e inglés. AppCompat vuelve a crear la pantalla actual. */
object Idioma {
    /** Cambia a inglés si el idioma actual es otro, y a español si ya está en inglés. */
    fun alternar(idiomaActual: Locale) {
        val nuevo = if (idiomaActual.language == "en") "es" else "en"
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(nuevo))
    }
}

// ---------- Formato de números ----------

/** Formato de números para mostrar: separador de miles con coma y dos decimales, sin depender del idioma del teléfono. */
object Formato {
    /** Resultado con dos decimales, por ejemplo 1,020.00. */
    fun numero(valor: Double): String {
        // Un valor casi cero se muestra como 0.00 para evitar "-0.00"
        val v = if (abs(valor) < 0.005) 0.0 else valor
        return String.format(Locale.US, "%,.2f", v)
    }

    /** Para los límites de los mensajes de error: 100 se muestra como "100", no como "100.00". */
    fun limite(valor: Double): String =
        if (valor == floor(valor) && abs(valor) < 1e15) String.format(Locale.US, "%,d", valor.toLong())
        else String.format(Locale.US, "%,.2f", valor)
}

// ---------- Subíndices en las etiquetas (x₁, y₂, ...) ----------

/** Subíndice más pequeño y un poco más abajo que el de Android, para que no se separe de la letra. */
private class SubindiceSpan : MetricAffectingSpan() {
    override fun updateDrawState(paint: TextPaint) = aplicar(paint)
    override fun updateMeasureState(paint: TextPaint) = aplicar(paint)

    private fun aplicar(paint: TextPaint) {
        val tamanoBase = paint.textSize
        paint.textSize = tamanoBase * ESCALA
        paint.baselineShift += (tamanoBase * DESPLAZAMIENTO).toInt()
    }

    private companion object {
        const val ESCALA = 0.65f
        const val DESPLAZAMIENTO = 0.12f
    }
}

/** Cambia el <sub> de strings.xml por SubindiceSpan y conserva el resto del formato. Se usa con getText, no con getString. */
fun CharSequence.conSubindicesAjustados(): CharSequence {
    if (this !is Spanned) return this
    // Se copia el texto y, en cada subíndice original, se pone el nuestro en el mismo lugar
    val resultado = SpannableStringBuilder(this)
    for (subindice in resultado.getSpans(0, resultado.length, SubscriptSpan::class.java)) {
        val inicio = resultado.getSpanStart(subindice)
        val fin = resultado.getSpanEnd(subindice)
        resultado.removeSpan(subindice)
        resultado.setSpan(SubindiceSpan(), inicio, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return resultado
}

// ---------- Márgenes de pantalla completa ----------

/**
 * Ajusta los márgenes de las pantallas con barra superior para que no queden debajo de las barras
 * del sistema ni del teclado (la app se dibuja de borde a borde).
 */
fun ajustarBordes(raiz: View, barra: View, contenido: View?, pie: View? = null) {
    // Márgenes originales del layout: a ellos se les suma el espacio de las barras, sin perderlos
    val barraBase = Triple(barra.paddingLeft, barra.paddingTop, barra.paddingRight)
    val pieBase = pie?.let { Triple(it.paddingLeft, it.paddingRight, it.paddingBottom) }

    ViewCompat.setOnApplyWindowInsetsListener(raiz) { _, insets ->
        // Espacio que ocupan las barras, la cámara frontal y el teclado (abajo se usa el que sea mayor)
        val sistema = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        val abajo = max(sistema.bottom, insets.getInsets(WindowInsetsCompat.Type.ime()).bottom)

        // La barra superior baja lo que mide la barra de estado
        barra.updatePadding(
            left = barraBase.first + sistema.left,
            top = barraBase.second + sistema.top,
            right = barraBase.third + sistema.right
        )
        // Con pie (menú), el margen de abajo lo lleva el pie; sin pie (cálculo), lo lleva el contenido
        if (pie != null && pieBase != null) {
            contenido?.updatePadding(left = sistema.left, right = sistema.right)
            pie.updatePadding(
                left = pieBase.first + sistema.left,
                right = pieBase.second + sistema.right,
                bottom = pieBase.third + abajo
            )
        } else {
            contenido?.updatePadding(left = sistema.left, right = sistema.right, bottom = abajo)
        }
        WindowInsetsCompat.CONSUMED
    }
}
