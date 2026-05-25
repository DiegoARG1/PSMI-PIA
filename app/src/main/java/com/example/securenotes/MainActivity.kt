package com.example.securenotes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)

        btnEntrar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa tu usuario y contraseña maestra", Toast.LENGTH_SHORT).show()
            }
            else if (usuario == "admin" && contrasena == "1234") {
                val intent = Intent(this, PrincipalActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, "Credenciales incorrectas. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}