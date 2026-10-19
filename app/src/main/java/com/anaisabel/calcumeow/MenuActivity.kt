package com.anaisabel.calcumeow

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnLayout
import com.anaisabel.calcumeow.calculos.Calculos
import com.anaisabel.calcumeow.databinding.ActivityMenuBinding

/** Menú con los cuatro cálculos. La nube del gatito cambia según la tarjeta elegida. */
class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private var seleccion = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ---------- Pantalla y barras del sistema ----------
        // La barra superior es oscura, así que los íconos de la barra de estado van claros.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ajustarBordes(binding.main, binding.barra, binding.scroll, binding.pie)

        // ---------- Tarjetas de los cálculos ----------
        // Cada tarjeta se llena con el ícono y título de su cálculo; al tocarla se selecciona.
        // La selección se recupera cuando la pantalla se vuelve a crear (por ejemplo, al girarla).
        seleccion = savedInstanceState?.getInt(ESTADO_SELECCION) ?: 0

        val tarjetas = listOf(binding.tarjeta1, binding.tarjeta2, binding.tarjeta3, binding.tarjeta4)
        Calculos.todos.forEachIndexed { indice, calculo ->
            val vista = CatalogoVista.de(calculo.id)
            val tarjeta = tarjetas[indice]
            tarjeta.imgIcono.setImageResource(vista.icono)
            tarjeta.txtTitulo.setText(vista.titulo)
            tarjeta.root.setOnClickListener { seleccionar(indice) }
        }
        seleccionar(seleccion)

        // ---------- Idioma ----------
        binding.chipIdioma.setOnClickListener {
            Idioma.alternar(resources.configuration.locales[0])
        }

        // ---------- Ajuste del gatito y la nube ----------
        // Se recalcula su tamaño al dibujar la pantalla y cada vez que cambia el alto visible,
        // para que quepa antes de que aparezca el scroll.
        binding.scroll.doOnLayout { ajustarBloque() }
        binding.scroll.addOnLayoutChangeListener { _, _, arriba, _, abajo, _, arribaAntes, _, abajoAntes ->
            if (abajo - arriba != abajoAntes - arribaAntes) ajustarBloque()
        }

        // ---------- Botón "Abrir" ----------
        // Manda a CalculoActivity el id del cálculo seleccionado.
        binding.btnAbrir.setOnClickListener {
            val intent = Intent(this, CalculoActivity::class.java)
            val args = Bundle().apply {
                putString(CalculoActivity.EXTRA_CALCULO_ID, Calculos.todos[seleccion].id)
            }
            intent.putExtras(args)
            startActivity(intent)
        }
    }

    /**
     * Si el contenido no cabe en el alto visible, encoge el bloque del gatito y la nube
     * (hasta menu_bloque_escala_min); después aparece el scroll.
     */
    private fun ajustarBloque() {
        val bloque = binding.bloqueGatoNube
        val espacio = binding.espacioLibre
        val contenido = binding.scroll.getChildAt(0) as ViewGroup
        val parametros = bloque.layoutParams as ConstraintLayout.LayoutParams

        // Paso 1: alto que tendría el bloque a tamaño completo.
        // Es su ancho disponible por la relación alto/ancho del layout (por ejemplo "1000:760").
        val (ancho, alto) = parametros.dimensionRatio.split(":").map { it.toFloat() }
        val anchoCompleto = (espacio.width - espacio.paddingLeft - espacio.paddingRight).toFloat()
        val altoCompleto = anchoCompleto * alto / ancho

        // Paso 2: alto que ocupa todo lo demás del contenido, sin contar el espacio del bloque.
        var otros = contenido.paddingTop + contenido.paddingBottom + espacio.paddingTop + espacio.paddingBottom
        for (i in 0 until contenido.childCount) {
            val hijo = contenido.getChildAt(i)
            if (hijo === espacio || hijo.visibility == View.GONE) continue
            val margenes = hijo.layoutParams as ViewGroup.MarginLayoutParams
            otros += hijo.height + margenes.topMargin + margenes.bottomMargin
        }
        // Paso 3: del alto visible, lo que sobra es el espacio disponible para el bloque.
        // Su escala (entre el mínimo y 1) se aplica como porcentaje del ancho, así no se deforma.
        val visible = binding.scroll.height - binding.scroll.paddingTop - binding.scroll.paddingBottom
        val disponible = visible - otros

        val minimo = ResourcesCompat.getFloat(resources, R.dimen.menu_bloque_escala_min)
        val escala = (disponible / altoCompleto).coerceIn(minimo, 1f)
        if (parametros.matchConstraintPercentWidth != escala) {
            parametros.matchConstraintPercentWidth = escala
            bloque.layoutParams = parametros
        }

        // Paso 4: con el bloque reducido la nube es más angosta, así que se permiten más líneas
        // para que el texto nunca se corte.
        val lineasBase = resources.getInteger(R.integer.menu_nube_max_lineas)
        binding.txtNube.maxLines = if (escala < 1f) maxOf(lineasBase, LINEAS_NUBE_REDUCIDA) else lineasBase
    }

    // ---------- Selección y estado ----------

    /** Marca la tarjeta elegida y cambia el mensaje de la nube del gatito. */
    private fun seleccionar(indice: Int) {
        seleccion = indice
        // Solo la tarjeta elegida queda "seleccionada" (el estilo del layout la resalta)
        val tarjetas = listOf(binding.tarjeta1, binding.tarjeta2, binding.tarjeta3, binding.tarjeta4)
        tarjetas.forEachIndexed { i, tarjeta -> tarjeta.root.isSelected = i == indice }
        // La nube muestra el mensaje del cálculo elegido
        binding.txtNube.setText(CatalogoVista.de(Calculos.todos[indice].id).nube)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ESTADO_SELECCION, seleccion)
    }

    private companion object {
        const val ESTADO_SELECCION = "seleccion"
        const val LINEAS_NUBE_REDUCIDA = 3
    }
}
