package com.example.securenotes

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NuevaContrasenaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nueva_contrasena)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_contrasena)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Nueva Contraseña"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val etTitulo = findViewById<EditText>(R.id.et_titulo)
        val etUsuario = findViewById<EditText>(R.id.et_contenido)
        val etContrasena = findViewById<EditText>(R.id.et_contrasena)
        val etComentario = findViewById<EditText>(R.id.et_comentario)
        val etContrasenaIndividual = findViewById<EditText>(R.id.et_contrasena_individual)

        val btnCrear = findViewById<Button>(R.id.btn_crear)

        btnCrear.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            val comentario = etComentario.text.toString().trim()
            val contrasenaIndividual = etContrasenaIndividual.text.toString().trim()

            if (titulo.isEmpty() || usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor llena el título, el usuario y la contraseña", Toast.LENGTH_SHORT).show()
            } else {
                guardarContrasenaEnBD(titulo, usuario, contrasena, comentario, contrasenaIndividual)
            }
        }
    }

    private fun guardarContrasenaEnBD(titulo: String, usuario: String, contrasena: String, comentario: String, contrasenaIndividual: String) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase

        val valores = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PASS_TITULO, titulo)
            put(DatabaseHelper.COLUMN_PASS_USER_CUENTA, usuario)
            put(DatabaseHelper.COLUMN_PASS_VALOR_CUENTA, contrasena)
            put(DatabaseHelper.COLUMN_PASS_COMENTARIOS, comentario.takeIf { it.isNotEmpty() })
            put(DatabaseHelper.COLUMN_PASS_INDIVIDUAL, contrasenaIndividual.takeIf { it.isNotEmpty() })
        }

        val resultadoId = db.insert(DatabaseHelper.TABLE_CONTRASENIAS, null, valores)

        if (resultadoId != -1L) {
            Toast.makeText(this, "Contraseña guardada (ID: $resultadoId)", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar la contraseña", Toast.LENGTH_SHORT).show()
        }

        db.close()
    }
}