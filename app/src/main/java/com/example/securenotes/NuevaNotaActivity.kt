package com.example.securenotes

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NuevaNotaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nueva_nota)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_nota)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Nueva Nota"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val etTitulo = findViewById<EditText>(R.id.et_titulo)
        val etContenido = findViewById<EditText>(R.id.et_contenido)
        val etComentario = findViewById<EditText>(R.id.et_comentario)
        val etContrasenaIndividual = findViewById<EditText>(R.id.et_contrasena_individual)

        val btnCrear = findViewById<Button>(R.id.btn_crear)

        btnCrear.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val contenido = etContenido.text.toString().trim()
            val comentario = etComentario.text.toString().trim()
            val contrasenaIndividual = etContrasenaIndividual.text.toString().trim()

            if (titulo.isEmpty() || contenido.isEmpty()) {
                Toast.makeText(this, "Por favor llena el título y la nota", Toast.LENGTH_SHORT).show()
            } else {
                guardarNotaEnBD(titulo, contenido, comentario, contrasenaIndividual)
            }
        }
    }

    private fun guardarNotaEnBD(titulo: String, contenido: String, comentario: String, contrasenaIndividual: String) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase

        val valores = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTA_TITULO, titulo)
            put(DatabaseHelper.COLUMN_NOTA_CONTENIDO, contenido)
            put(DatabaseHelper.COLUMN_NOTA_COMENTARIOS, comentario.takeIf { it.isNotEmpty() })
            put(DatabaseHelper.COLUMN_NOTA_INDIVIDUAL, contrasenaIndividual.takeIf { it.isNotEmpty() })
        }

        val resultadoId = db.insert(DatabaseHelper.TABLE_NOTAS, null, valores)

        if (resultadoId != -1L) {
            Toast.makeText(this, "Nota guardada (ID: $resultadoId)", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar la nota", Toast.LENGTH_SHORT).show()
        }

        db.close()
    }
}