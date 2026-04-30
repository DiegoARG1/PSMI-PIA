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

        // 1. Configuramos la Toolbar puramente visual y para regresar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_nota)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Nueva Nota"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish() // La flechita te regresa a la Bóveda
        }

        // 2. Enlazamos los controles de la pantalla
        val etTitulo = findViewById<EditText>(R.id.et_titulo)
        val etContenido = findViewById<EditText>(R.id.et_usuario)
        val btnCrear = findViewById<Button>(R.id.btn_crear)
        val btnCancelar = findViewById<Button>(R.id.btn_cancelar)

        // 3. El botón cancelar original de Mauro
        btnCancelar.setOnClickListener {
            finish()
        }

        // 4. El botón crear original de Mauro
        btnCrear.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val contenido = etContenido.text.toString().trim()

            if (titulo.isEmpty() || contenido.isEmpty()) {
                Toast.makeText(this, "Por favor llena el título y la nota", Toast.LENGTH_SHORT).show()
            } else {
                guardarNotaEnBD(titulo, contenido)
            }
        }
    }

    private fun guardarNotaEnBD(titulo: String, contenido: String) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase

        val valores = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TITULO, titulo)
            put(DatabaseHelper.COLUMN_CONTENIDO, contenido)
        }

        val resultadoId = db.insert(DatabaseHelper.TABLE_NOTAS, null, valores)

        if (resultadoId != -1L) {
            Toast.makeText(this, "Nota guardada (ID: $resultadoId)", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        }

        // db.close()
    }
}