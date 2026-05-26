package com.example.securenotes

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class NuevaNotaActivity : AppCompatActivity() {

    private var notaId: Int = -1
    private var passCorrecta: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nueva_nota)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_nota)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val etTitulo = findViewById<EditText>(R.id.et_titulo)
        val etContenido = findViewById<EditText>(R.id.et_contenido)
        val etComentario = findViewById<EditText>(R.id.et_comentario)
        val etContrasenaIndividual = findViewById<EditText>(R.id.et_contrasena_individual)
        val btnCrear = findViewById<Button>(R.id.btn_crear)
        val btnInfo = findViewById<android.widget.ImageButton>(R.id.btn_info)

        // Detectar si es edición
        notaId = intent.getIntExtra("ID", -1)
        if (notaId != -1) {
            supportActionBar?.title = "Editar Nota"
            btnCrear.text = "Guardar"
            cargarDatosExistentes(etTitulo, etContenido, etComentario, etContrasenaIndividual)
        } else {
            supportActionBar?.title = "Nueva Nota"
        }

        btnInfo.setOnClickListener {
            mostrarDialogoInfo()
        }

        btnCrear.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val contenido = etContenido.text.toString().trim()
            val comentario = etComentario.text.toString().trim()
            val contrasenaInd = etContrasenaIndividual.text.toString().trim()

            if (titulo.isEmpty() || contenido.isEmpty()) {
                Toast.makeText(this, "Por favor llena el título y la nota", Toast.LENGTH_SHORT).show()
            } else {
                if (notaId == -1) {
                    guardarNotaEnBD(titulo, contenido, comentario, contrasenaInd)
                } else {
                    actualizarNotaEnBD(titulo, contenido, comentario, contrasenaInd)
                }
            }
        }
    }

    private fun cargarDatosExistentes(etT: EditText, etC: EditText, etCom: EditText, etPass: EditText) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_NOTAS, null, "${DatabaseHelper.COLUMN_NOTA_ID} = ?", arrayOf(notaId.toString()), null, null, null)

        if (cursor.moveToFirst()) {
            etT.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTA_TITULO)))
            etC.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTA_CONTENIDO)))
            etCom.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTA_COMENTARIOS)))
            val passInd = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTA_INDIVIDUAL))
            etPass.setText(passInd)
            passCorrecta = passInd

            // Manejar bloqueo si hay contraseña individual
            if (!passInd.isNullOrEmpty()) {
                val layoutBloqueo = findViewById<LinearLayout>(R.id.layout_bloqueo_edit)
                layoutBloqueo.visibility = View.VISIBLE
                
                findViewById<Button>(R.id.btn_desbloquear_edit).setOnClickListener {
                    val input = findViewById<EditText>(R.id.et_pass_bloqueo_edit).text.toString()
                    if (input == passCorrecta) {
                        Toast.makeText(this, "Contenido desbloqueado", Toast.LENGTH_SHORT).show()
                        layoutBloqueo.visibility = View.GONE
                    } else {
                        Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        cursor.close()
        db.close()
    }

    private fun guardarNotaEnBD(titulo: String, contenido: String, comentario: String, contrasenaInd: String) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTA_TITULO, titulo)
            put(DatabaseHelper.COLUMN_NOTA_CONTENIDO, contenido)
            put(DatabaseHelper.COLUMN_NOTA_COMENTARIOS, comentario.takeIf { it.isNotEmpty() })
            put(DatabaseHelper.COLUMN_NOTA_INDIVIDUAL, contrasenaInd.takeIf { it.isNotEmpty() })
        }
        if (db.insert(DatabaseHelper.TABLE_NOTAS, null, valores) != -1L) {
            Toast.makeText(this, "Nota guardada", Toast.LENGTH_SHORT).show()
            finish()
        }
        db.close()
    }

    private fun actualizarNotaEnBD(titulo: String, contenido: String, comentario: String, contrasenaInd: String) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTA_TITULO, titulo)
            put(DatabaseHelper.COLUMN_NOTA_CONTENIDO, contenido)
            put(DatabaseHelper.COLUMN_NOTA_COMENTARIOS, comentario.takeIf { it.isNotEmpty() })
            put(DatabaseHelper.COLUMN_NOTA_INDIVIDUAL, contrasenaInd.takeIf { it.isNotEmpty() })
        }
        if (db.update(DatabaseHelper.TABLE_NOTAS, valores, "${DatabaseHelper.COLUMN_NOTA_ID} = ?", arrayOf(notaId.toString())) > 0) {
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
            finish()
        }
        db.close()
    }

    private fun mostrarDialogoInfo() {
        AlertDialog.Builder(this)
            .setTitle("Contraseña individual")
            .setMessage("Si estableces una contraseña individual, se te pedirá cada vez que intentes ver o editar este contenido. Esto añade una capa extra de seguridad.")
            .setPositiveButton("Continuar", null)
            .show()
    }
}
