package com.example.securenotes

import android.os.Bundle
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val fabNuevo = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabNuevo)

        fabNuevo.setOnClickListener { vistaBoton ->
            val contextoTema = android.view.ContextThemeWrapper(this, R.style.TemaMenuBoveda)

            val popupMenu = android.widget.PopupMenu(contextoTema, vistaBoton)

            popupMenu.menuInflater.inflate(R.menu.menu_opciones_nuevo, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { itemSeleccionado ->
                when (itemSeleccionado.itemId) {
                    R.id.opcion_nota -> {
                        val intent = Intent(this, NuevaNotaActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.opcion_pass -> {
                        val intent = Intent(this, NuevaContrasenaActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }
}