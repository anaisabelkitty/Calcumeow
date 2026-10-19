package com.anaisabel.calcumeow

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.anaisabel.calcumeow.databinding.ActivityWelcomeBinding

/** Primera pantalla: saludo del gatito y botón para entrar al menú. */
class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ---------- Pantalla y barras del sistema ----------
        // La app se dibuja de borde a borde; el fondo es claro, así que los íconos de la barra de estado van oscuros.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Se deja un margen igual al de las barras y la cámara frontal para que nada quede tapado
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val barras = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            WindowInsetsCompat.CONSUMED
        }

        // ---------- Botones ----------
        // El chip cambia el idioma (español/inglés); "Empezar" abre el menú de cálculos.
        binding.chipIdioma.setOnClickListener {
            Idioma.alternar(resources.configuration.locales[0])
        }

        binding.btnEmpezar.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }
    }
}
