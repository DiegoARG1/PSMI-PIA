package com.example.securenotes

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class DetalleActivity : AppCompatActivity() {

    private var idDato: Int = -1
    private var tipo: String? = null
    private var passCorrecta: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_detalle)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        idDato = intent.getIntExtra("ID", -1)
        tipo = intent.getStringExtra("TIPO")

        obtenerDatosDeBD()
    }

    private fun obtenerDatosDeBD() {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        if (tipo == "NOTA") {
            val cursor = db.query(DatabaseHelper.TABLE_NOTAS, null, "${DatabaseHelper.COLUMN_NOTA_ID} = ?", arrayOf(idDato.toString()), null, null, null)
            if (cursor.moveToFirst()) {
                val titulo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTA_TITULO))
                val contenido = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTA_CONTENIDO))
                val comentario = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTA_COMENTARIOS))
                passCorrecta = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTA_INDIVIDUAL))

                supportActionBar?.title = titulo
                configurarUI("Nota:", contenido, null, comentario)
            }
            cursor.close()
        } else {
            val cursor = db.query(DatabaseHelper.TABLE_CONTRASENIAS, null, "${DatabaseHelper.COLUMN_PASS_ID} = ?", arrayOf(idDato.toString()), null, null, null)
            if (cursor.moveToFirst()) {
                val titulo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS_TITULO))
                val usuario = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS_USER_CUENTA))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS_VALOR_CUENTA))
                val comentario = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS_COMENTARIOS))
                passCorrecta = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS_INDIVIDUAL))

                supportActionBar?.title = titulo
                configurarUI("Usuario:", usuario, password, comentario)
            }
            cursor.close()
        }
        db.close()
    }

    private fun configurarUI(etiqueta1: String, valor1: String, valor2: String?, comentario: String?) {
        val layoutContenido = findViewById<LinearLayout>(R.id.layout_contenido_detalle)
        val layoutBloqueo = findViewById<LinearLayout>(R.id.layout_bloqueo)
        
        findViewById<TextView>(R.id.tv_etiqueta_1).text = etiqueta1
        findViewById<TextView>(R.id.tv_valor_1).text = valor1
        findViewById<TextView>(R.id.tv_valor_comentario).text = if (comentario.isNullOrEmpty()) "Sin comentarios" else comentario

        if (valor2 != null) {
            findViewById<LinearLayout>(R.id.layout_campo_2).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tv_valor_2).text = valor2
        } else {
            findViewById<LinearLayout>(R.id.layout_campo_2).visibility = View.GONE
        }

        // Configurar botones de copiar
        findViewById<ImageButton>(R.id.btn_copiar_1).setOnClickListener { copiarAlPortapapeles(valor1) }
        findViewById<ImageButton>(R.id.btn_copiar_2).setOnClickListener { valor2?.let { copiarAlPortapapeles(it) } }

        // Manejar bloqueo
        if (passCorrecta.isNullOrEmpty()) {
            layoutBloqueo.visibility = View.GONE
            layoutContenido.visibility = View.VISIBLE
        } else {
            layoutBloqueo.visibility = View.VISIBLE
            layoutContenido.visibility = View.GONE
            
            findViewById<Button>(R.id.btn_desbloquear).setOnClickListener {
                val passIngresada = findViewById<EditText>(R.id.et_pass_individual).text.toString()
                if (passIngresada == passCorrecta) {
                    Toast.makeText(this, "Contenido desbloqueado", Toast.LENGTH_SHORT).show()
                    layoutBloqueo.visibility = View.GONE
                    layoutContenido.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this, "Contraseña individual incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun copiarAlPortapapeles(texto: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText("Copiado", texto)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }
}
