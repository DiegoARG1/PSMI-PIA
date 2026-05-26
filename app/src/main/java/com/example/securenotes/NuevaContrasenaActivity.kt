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

class NuevaContrasenaActivity : AppCompatActivity() {

    private var passId: Int = -1
    private var passCorrecta: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nueva_contrasena)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_contrasena)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val etTitulo = findViewById<EditText>(R.id.et_titulo)
        val etUsuario = findViewById<EditText>(R.id.et_contenido)
        val etContrasena = findViewById<EditText>(R.id.et_contrasena)
        val etComentario = findViewById<EditText>(R.id.et_comentario)
        val etContrasenaIndividual = findViewById<EditText>(R.id.et_contrasena_individual)
        val btnCrear = findViewById<Button>(R.id.btn_crear)
        val btnInfo = findViewById<android.widget.ImageButton>(R.id.btn_info)

        // Detectar si es edición
        passId = intent.getIntExtra("ID", -1)
        if (passId != -1) {
            supportActionBar?.title = "Editar Contraseña"
            btnCrear.text = "Guardar"
            cargarDatosExistentes(etTitulo, etUsuario, etContrasena, etComentario, etContrasenaIndividual)
        } else {
            supportActionBar?.title = "Nueva Contraseña"
        }

        btnInfo.setOnClickListener {
            mostrarDialogoInfo()
        }

        btnCrear.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            val comentario = etComentario.text.toString().trim()
            val contrasenaInd = etContrasenaIndividual.text.toString().trim()

            if (titulo.isEmpty() || usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor llena los campos obligatorios", Toast.LENGTH_SHORT).show()
            } else {
                if (passId == -1) {
                    guardarContrasenaEnBD(titulo, usuario, contrasena, comentario, contrasenaInd)
                } else {
                    actualizarContrasenaEnBD(titulo, usuario, contrasena, comentario, contrasenaInd)
                }
            }
        }
    }

    private fun cargarDatosExistentes(etT: EditText, etU: EditText, etP: EditText, etCom: EditText, etPassInd: EditText) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_CONTRASENIAS, null, "${DatabaseHelper.COLUMN_PASS_ID} = ?", arrayOf(passId.toString()), null, null, null)

        if (cursor.moveToFirst()) {
            etT.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS_TITULO)))
            etU.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS_USER_CUENTA)))
            etP.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS_VALOR_CUENTA)))
            etCom.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS_COMENTARIOS)))
            val passInd = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS_INDIVIDUAL))
            etPassInd.setText(passInd)
            passCorrecta = passInd

            // Bloqueo si hay contraseña individual
            if (!passInd.isNullOrEmpty()) {
                val layoutBloqueo = findViewById<LinearLayout>(R.id.layout_bloqueo_edit)
                layoutBloqueo.visibility = View.VISIBLE
                
                findViewById<Button>(R.id.btn_desbloquear_edit).setOnClickListener {
                    val input = findViewById<EditText>(R.id.et_pass_bloqueo_edit).text.toString()
                    if (input == passCorrecta) {
                        Toast.makeText(this, "Contenido desbloqueado", Toast.LENGTH_SHORT).show()
                        layoutBloqueo.visibility = View.GONE
                    } else {
                        Toast.makeText(this, "Contraseña individual incorrecta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        cursor.close()
        db.close()
    }

    private fun guardarContrasenaEnBD(titulo: String, usuario: String, contrasena: String, comentario: String, contrasenaInd: String) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PASS_TITULO, titulo)
            put(DatabaseHelper.COLUMN_PASS_USER_CUENTA, usuario)
            put(DatabaseHelper.COLUMN_PASS_VALOR_CUENTA, contrasena)
            put(DatabaseHelper.COLUMN_PASS_COMENTARIOS, comentario.takeIf { it.isNotEmpty() })
            put(DatabaseHelper.COLUMN_PASS_INDIVIDUAL, contrasenaInd.takeIf { it.isNotEmpty() })
        }
        if (db.insert(DatabaseHelper.TABLE_CONTRASENIAS, null, valores) != -1L) {
            Toast.makeText(this, "Contraseña guardada", Toast.LENGTH_SHORT).show()
            finish()
        }
        db.close()
    }

    private fun actualizarContrasenaEnBD(titulo: String, usuario: String, contrasena: String, comentario: String, contrasenaInd: String) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PASS_TITULO, titulo)
            put(DatabaseHelper.COLUMN_PASS_USER_CUENTA, usuario)
            put(DatabaseHelper.COLUMN_PASS_VALOR_CUENTA, contrasena)
            put(DatabaseHelper.COLUMN_PASS_COMENTARIOS, comentario.takeIf { it.isNotEmpty() })
            put(DatabaseHelper.COLUMN_PASS_INDIVIDUAL, contrasenaInd.takeIf { it.isNotEmpty() })
        }
        if (db.update(DatabaseHelper.TABLE_CONTRASENIAS, valores, "${DatabaseHelper.COLUMN_PASS_ID} = ?", arrayOf(passId.toString())) > 0) {
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
