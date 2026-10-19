package com.anaisabel.calcumeow

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.anaisabel.calcumeow.calculos.Calculos
import com.anaisabel.calcumeow.databinding.ActivityCalculoBinding
import com.anaisabel.calcumeow.databinding.ItemCampoBinding
import com.anaisabel.calcumeow.modelo.Calculo
import com.anaisabel.calcumeow.modelo.Campo
import com.anaisabel.calcumeow.modelo.ErrorCampo
import com.anaisabel.calcumeow.modelo.ErrorGeneral
import com.anaisabel.calcumeow.modelo.Resultado
import com.anaisabel.calcumeow.modelo.ResultadoValidacion

/**
 * Pantalla de un cálculo. El formulario es dinámico: según el cálculo elegido cambian los campos
 * visibles, sus etiquetas, unidades, ayudas y la imagen de la fórmula. Los campos que no se usan
 * se ocultan.
 */
class CalculoActivity : AppCompatActivity() {

    // ---------- Datos de la pantalla ----------
    // calculo = la lógica (campos, validar, calcular); vista = lo visual (textos, íconos, fórmula).
    private lateinit var binding: ActivityCalculoBinding
    private lateinit var calculo: Calculo
    private lateinit var vista: CalculoVista

    /** Los 6 espacios de campo que tiene el layout (3 por renglón). */
    private lateinit var espacios: List<ItemCampoBinding>

    /** Espacio que usa cada campo del cálculo actual, por id de campo. */
    private val espacioDe = mutableMapOf<String, ItemCampoBinding>()

    /** Espacios de campo que tienen un error. */
    private val espaciosConError = mutableSetOf<ItemCampoBinding>()

    private var ultimosResultados: List<Resultado> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ---------- Pantalla y barras del sistema ----------
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))

        // ---------- Cálculo elegido ----------
        // El menú manda solo el id; aquí se busca el cálculo y su vista. Si no existe, se cierra la pantalla.
        val idCalculo = intent.extras?.getString(EXTRA_CALCULO_ID)
        val encontrado = idCalculo?.let { Calculos.porId(it) }
        if (encontrado == null) {
            finish()
            return
        }
        calculo = encontrado
        vista = CatalogoVista.de(calculo.id)

        // ---------- Armado de la pantalla ----------
        // El layout trae 6 espacios de campo fijos; cada cálculo usa solo los que necesita.
        binding = ActivityCalculoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ajustarBordes(binding.main, binding.barra, binding.scroll)

        espacios = listOf(
            binding.campo1, binding.campo2, binding.campo3,
            binding.campo4, binding.campo5, binding.campo6
        )

        mostrarEncabezado()
        armarFormulario()
        restaurarEstado(savedInstanceState)

        // ---------- Botones ----------
        binding.btnVolver.setOnClickListener { finish() }
        binding.chipIdioma.setOnClickListener {
            Idioma.alternar(resources.configuration.locales[0])
        }
        binding.btnCalcular.setOnClickListener { calcular(interactivo = true) }
        binding.btnLimpiar.setOnClickListener { limpiar() }
        binding.btnCompartir.setOnClickListener { compartir() }
    }

    // ---------------------------------------------------------------- Formulario

    /** Pone el ícono, el título, la leyenda y la imagen de la fórmula del cálculo. */
    private fun mostrarEncabezado() {
        binding.imgIcono.setImageResource(vista.icono)
        binding.txtTitulo.setText(vista.titulo)
        binding.txtLeyenda.setText(vista.leyenda)
        binding.imgFormula.setImageResource(vista.formula)
        binding.imgFormula.contentDescription = getString(vista.descripcionFormula)
    }

    /** Oculta los 6 espacios y muestra solo los campos del cálculo, cada uno en el renglón que le toca. */
    private fun armarFormulario() {
        espacios.forEach { it.root.visibility = View.GONE }

        // Al agrupar por fila, el espacio de cada campo es: fila * 3 + su posición dentro del renglón
        val ultimo = calculo.campos.last()
        calculo.campos.groupBy { it.fila }.forEach { (fila, campos) ->
            campos.forEachIndexed { posicion, campo ->
                val espacio = espacios[fila * CAMPOS_POR_FILA + posicion]
                espacioDe[campo.id] = espacio
                prepararCampo(espacio, campo, esUltimo = campo == ultimo)
            }
        }
    }

    /** Configura un espacio de campo: textos, tipo de teclado y qué pasa al escribir. */
    private fun prepararCampo(espacio: ItemCampoBinding, campo: Campo, esUltimo: Boolean) {
        val datos = vista.campos.getValue(campo.id)

        // Textos: etiqueta, ayuda y unidad. Se usa getText (no getString) para conservar
        // la cursiva y el subíndice de strings.xml.
        espacio.root.visibility = View.VISIBLE
        espacio.lblCampo.text = getText(datos.etiqueta).conSubindicesAjustados()
        espacio.txtAyuda.setText(datos.ayuda)
        espacio.txtAyuda.tag = datos.ayuda // para volver a ponerla cuando se quita un error
        espacio.tilCampo.suffixText = datos.unidad?.let { getString(it) }

        // Teclado numérico: con punto decimal solo si el campo no es entero, y con signo menos solo si admite negativos
        var tipo = InputType.TYPE_CLASS_NUMBER
        if (!campo.soloEntero) tipo = tipo or InputType.TYPE_NUMBER_FLAG_DECIMAL
        if (campo.permiteNegativo) tipo = tipo or InputType.TYPE_NUMBER_FLAG_SIGNED
        espacio.edtCampo.inputType = tipo

        // Botón del teclado: en el último campo dice "Listo" y calcula; en los demás pasa al siguiente campo
        espacio.edtCampo.imeOptions = if (esUltimo) EditorInfo.IME_ACTION_DONE else EditorInfo.IME_ACTION_NEXT
        espacio.edtCampo.setOnEditorActionListener { _, accion, _ ->
            if (accion == EditorInfo.IME_ACTION_DONE) {
                calcular(interactivo = true)
                true
            } else {
                false
            }
        }

        // Al editar un dato, su error y el resultado anterior dejan de ser válidos, así que se quitan
        espacio.edtCampo.doAfterTextChanged {
            quitarError(espacio)
            ocultarResultado()
            binding.txtErrorGeneral.visibility = View.GONE
        }
    }

    /** Lo que hay escrito en cada campo del cálculo, por id de campo. */
    private fun leerTextos(): Map<String, String> =
        calculo.campos.associate { campo ->
            campo.id to espacioDe.getValue(campo.id).edtCampo.text?.toString().orEmpty()
        }

    // ---------------------------------------------------------------- Validación y cálculo

    /**
     * Valida los datos y, si todo está bien, muestra el resultado.
     * [interactivo] es true cuando la persona pulsó calcular (avisos, teclado y scroll);
     * es false al restaurar la pantalla, donde solo se vuelve a mostrar el resultado.
     */
    private fun calcular(interactivo: Boolean) {
        // Paso 1: validar y mostrar los errores que haya
        val textos = leerTextos()
        val validacion = calculo.validar(textos)
        mostrarErrores(validacion)

        // Paso 2: con errores no hay resultado; se avisa y se lleva el cursor al primer campo con error
        if (!validacion.esValido) {
            ocultarResultado()
            if (interactivo) {
                Toast.makeText(this, R.string.aviso_corregir, Toast.LENGTH_SHORT).show()
                enfocarPrimerError(validacion)
            }
            return
        }

        // Paso 3: datos válidos; se calcula y, si la persona lo pidió, se baja el teclado y se desplaza la
        // pantalla hasta la tarjeta de resultado
        if (interactivo) ocultarTeclado()
        mostrarResultados(calculo.calcular(textos))
        if (interactivo) {
            binding.scroll.post {
                val margen = (16 * resources.displayMetrics.density).toInt()
                binding.scroll.smoothScrollTo(0, binding.tarjetaResultado.top - margen)
            }
        }
    }

    /** Marca cada campo con error y muestra (u oculta) el error general que afecta a varios campos. */
    private fun mostrarErrores(validacion: ResultadoValidacion) {
        // Errores de cada campo: se marcan los que fallan y se limpian los que ya están bien
        for (campo in calculo.campos) {
            val espacio = espacioDe.getValue(campo.id)
            val error = validacion.errores[campo.id]
            if (error == null) quitarError(espacio) else mostrarError(espacio, mensajeDe(error))
        }
        // Error general (por ejemplo, sistema sin solución única): un solo mensaje bajo el formulario
        val general = validacion.errorGeneral
        binding.txtErrorGeneral.isVisible = general != null
        if (general != null) binding.txtErrorGeneral.setText(mensajeDe(general))
    }

    /** Marca el campo con error: borde rojo y el mensaje en el lugar de la ayuda (el error de Material queda desalineado). */
    private fun mostrarError(espacio: ItemCampoBinding, mensaje: String) {
        // Borde rojo, con y sin foco. Material solo usa un color único cuando el campo tiene foco,
        // por eso se arma una lista con estados aunque los dos colores sean iguales.
        val rojo = getColor(R.color.error)
        espacio.tilCampo.setBoxStrokeColorStateList(
            ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_focused), intArrayOf()),
                intArrayOf(rojo, rojo)
            )
        )
        // El mensaje de error reemplaza al texto de ayuda, en rojo y un poco más grande
        espacio.txtAyuda.apply {
            text = mensaje
            setTextColor(getColor(R.color.error))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, TAMANO_ERROR_SP)
        }
        espaciosConError.add(espacio)
    }

    /** Quita el error del campo y vuelve a mostrar su ayuda. */
    private fun quitarError(espacio: ItemCampoBinding) {
        // Si el campo no tenía error no hay nada que deshacer
        if (!espaciosConError.remove(espacio)) return
        // Se restauran el borde normal y el texto de ayuda con su color y tamaño originales
        espacio.tilCampo.setBoxStrokeColorStateList(
            AppCompatResources.getColorStateList(this, R.color.campo_borde)
        )
        espacio.txtAyuda.apply {
            setText(tag as Int)
            setTextColor(getColor(R.color.texto_secundario))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, TAMANO_AYUDA_SP)
        }
    }

    /** Lleva el cursor al primer campo (en orden del formulario) que tiene error. */
    private fun enfocarPrimerError(validacion: ResultadoValidacion) {
        val primero = calculo.campos.firstOrNull { it.id in validacion.errores } ?: return
        espacioDe.getValue(primero.id).edtCampo.requestFocus()
    }

    // Traducción de errores a texto: el modelo no sabe de idiomas, así que aquí cada error
    // se convierte en su mensaje de strings.xml (en el idioma activo).
    private fun mensajeDe(error: ErrorCampo): String = when (error) {
        ErrorCampo.Obligatorio -> getString(R.string.error_obligatorio)
        ErrorCampo.NumeroInvalido -> getString(R.string.error_numero)
        ErrorCampo.MayorQueCero -> getString(R.string.error_mayor_cero)
        ErrorCampo.NoNegativo -> getString(R.string.error_no_negativo)
        ErrorCampo.EnteroMayorQueCero -> getString(R.string.error_entero)
        is ErrorCampo.MenorOIgualQue -> getString(R.string.error_maximo, Formato.limite(error.limite))
        is ErrorCampo.MayorOIgualQue -> getString(R.string.error_minimo, Formato.limite(error.limite))
    }

    private fun mensajeDe(error: ErrorGeneral): Int = when (error) {
        ErrorGeneral.SISTEMA_SIN_SOLUCION_UNICA -> R.string.error_sistema
    }

    // ---------------------------------------------------------------- Resultado

    /** Llena la tarjeta de resultado: una fila por dato (máximo 3) y muestra el botón de compartir. */
    private fun mostrarResultados(resultados: List<Resultado>) {
        // Se guardan para poder compartirlos después
        ultimosResultados = resultados
        // Las filas que no se necesitan se ocultan; las demás muestran etiqueta, valor y unidad
        val filas = listOf(binding.filaRes1, binding.filaRes2, binding.filaRes3)
        filas.forEachIndexed { indice, fila ->
            val resultado = resultados.getOrNull(indice)
            fila.root.isVisible = resultado != null
            if (resultado != null) {
                val datos = CatalogoVista.resultado(resultado.clave)
                fila.txtEtiqueta.setText(datos.etiqueta)
                fila.txtValor.text = Formato.numero(resultado.valor)
                // La unidad va aparte, más pequeña; los resultados sin unidad (x, y) la ocultan
                fila.txtUnidad.isVisible = datos.unidad != null
                datos.unidad?.let { fila.txtUnidad.setText(it) }
            }
        }
        binding.tarjetaResultado.visibility = View.VISIBLE
        binding.btnCompartir.visibility = View.VISIBLE
    }

    /** Esconde la tarjeta de resultado y el botón de compartir (los datos ya no corresponden). */
    private fun ocultarResultado() {
        ultimosResultados = emptyList()
        binding.tarjetaResultado.visibility = View.GONE
        binding.btnCompartir.visibility = View.GONE
    }

    /** Valor con su unidad para el texto que se comparte: "1,020.00 $", "5.00 u" o solo el número. */
    private fun textoDeValor(resultado: Resultado): String {
        val datos = CatalogoVista.resultado(resultado.clave)
        val numero = Formato.numero(resultado.valor)
        val unidad = datos.unidad ?: return numero
        return getString(R.string.valor_con_unidad, numero, getString(unidad))
    }

    /** Comparte los resultados como texto plano: un encabezado con el nombre del cálculo y una línea por dato. */
    private fun compartir() {
        if (ultimosResultados.isEmpty()) return
        // Se arma el texto línea por línea
        val lineas = ultimosResultados.map { resultado ->
            getString(
                R.string.compartir_linea,
                getString(CatalogoVista.resultado(resultado.clave).etiqueta),
                textoDeValor(resultado)
            )
        }
        val texto = (listOf(getString(R.string.compartir_encabezado, getString(vista.titulo))) + lineas)
            .joinToString("\n")

        // Se abre el selector de apps del sistema para elegir con cuál enviarlo
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, texto)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.compartir_titulo)))
    }

    // ---------------------------------------------------------------- Limpiar y teclado

    /** Borra todos los datos, errores y el resultado, y deja el cursor en el primer campo. */
    private fun limpiar() {
        for (campo in calculo.campos) {
            val espacio = espacioDe.getValue(campo.id)
            espacio.edtCampo.text?.clear()
            quitarError(espacio)
        }
        ocultarResultado()
        binding.txtErrorGeneral.visibility = View.GONE
        espacioDe.getValue(calculo.campos.first().id).edtCampo.requestFocus()
    }

    /** Quita el foco y baja el teclado. */
    private fun ocultarTeclado() {
        currentFocus?.clearFocus()
        WindowCompat.getInsetsController(window, binding.main)
            .hide(WindowInsetsCompat.Type.ime())
    }

    // ---------------------------------------------------------------- Estado (giro de pantalla, cambio de idioma)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Si la pantalla se cerró antes de armarse (cálculo no encontrado), no hay nada que guardar
        if (!::binding.isInitialized) return
        // Se guardan los textos de los 6 campos y si había resultado. Los 6 campos comparten ids,
        // por eso el texto se guarda a mano en lugar de dejarlo al sistema.
        val textos = espacios.map { it.edtCampo.text?.toString().orEmpty() }
        outState.putStringArray(ESTADO_TEXTOS, textos.toTypedArray())
        outState.putBoolean(ESTADO_RESULTADO, binding.tarjetaResultado.isVisible)
    }

    /** Devuelve los textos escritos y, si había resultado, lo vuelve a calcular. */
    private fun restaurarEstado(estado: Bundle?) {
        val textos = estado?.getStringArray(ESTADO_TEXTOS) ?: return
        // Se vuelve a escribir el texto de cada campo
        espacios.forEachIndexed { indice, espacio ->
            val texto = textos.getOrNull(indice).orEmpty()
            if (texto.isNotEmpty()) espacio.edtCampo.setText(texto)
        }
        // El resultado se vuelve a calcular con los mismos datos, así sale en el idioma nuevo
        if (estado.getBoolean(ESTADO_RESULTADO)) calcular(interactivo = false)
    }

    // ---------- Constantes ----------
    companion object {
        const val EXTRA_CALCULO_ID = "calculo_id"
        private const val CAMPOS_POR_FILA = 3
        private const val TAMANO_AYUDA_SP = 13f
        private const val TAMANO_ERROR_SP = 14f
        private const val ESTADO_TEXTOS = "textos"
        private const val ESTADO_RESULTADO = "hay_resultado"
    }
}
