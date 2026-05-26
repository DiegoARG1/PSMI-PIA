package com.example.securenotes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PrincipalActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvVacio: TextView
    private lateinit var adapter: DatoAdapter
    private var listaDatos = mutableListOf<DatoBoveda>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewDatos)
        tvVacio = findViewById(R.id.tvVacio)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Configurar el adapter con las acciones de eliminar, editar y clic en el item
        adapter = DatoAdapter(
            listaDatos,
            onEliminarClick = { dato -> confirmarEliminacion(dato) },
            onEditarClick = { dato -> abrirEditor(dato) },
            onItemClick = { dato -> verDetalle(dato) }
        )
        recyclerView.adapter = adapter

        val fabNuevo = findViewById<FloatingActionButton>(R.id.fabNuevo)
        fabNuevo.setOnClickListener { vistaBoton ->
            val contextoTema = android.view.ContextThemeWrapper(this, R.style.TemaMenuBoveda)
            val popupMenu = android.widget.PopupMenu(contextoTema, vistaBoton)
            popupMenu.menuInflater.inflate(R.menu.menu_opciones_nuevo, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { itemSeleccionado ->
                when (itemSeleccionado.itemId) {
                    R.id.opcion_nota -> {
                        startActivity(Intent(this, NuevaNotaActivity::class.java))
                        true
                    }
                    R.id.opcion_pass -> {
                        startActivity(Intent(this, NuevaContrasenaActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        listaDatos.clear()
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        // Cargar Notas
        val cursorNotas = db.query(DatabaseHelper.TABLE_NOTAS, null, null, null, null, null, null)
        if (cursorNotas.moveToFirst()) {
            do {
                val id = cursorNotas.getInt(cursorNotas.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTA_ID))
                val titulo = cursorNotas.getString(cursorNotas.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTA_TITULO))
                listaDatos.add(DatoBoveda(id, titulo, TipoDato.NOTA))
            } while (cursorNotas.moveToNext())
        }
        cursorNotas.close()

        // Cargar Contraseñas
        val cursorPass = db.query(DatabaseHelper.TABLE_CONTRASENIAS, null, null, null, null, null, null)
        if (cursorPass.moveToFirst()) {
            do {
                val id = cursorPass.getInt(cursorPass.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS_ID))
                val titulo = cursorPass.getString(cursorPass.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS_TITULO))
                listaDatos.add(DatoBoveda(id, titulo, TipoDato.PASSWORD))
            } while (cursorPass.moveToNext())
        }
        cursorPass.close()

        db.close()

        if (listaDatos.isEmpty()) {
            tvVacio.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvVacio.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        adapter.actualizarLista(listaDatos)
    }

    private fun verDetalle(dato: DatoBoveda) {
        val intent = Intent(this, DetalleActivity::class.java).apply {
            putExtra("ID", dato.id)
            putExtra("TIPO", dato.tipo.name)
        }
        startActivity(intent)
    }

    private fun confirmarEliminacion(dato: DatoBoveda) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar ${if (dato.tipo == TipoDato.NOTA) "nota" else "contraseña"}")
            .setMessage("¿Estás seguro de que quieres eliminar \"${dato.titulo}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarDeBD(dato)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarDeBD(dato: DatoBoveda) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        
        val tabla = if (dato.tipo == TipoDato.NOTA) DatabaseHelper.TABLE_NOTAS else DatabaseHelper.TABLE_CONTRASENIAS
        val columnaId = if (dato.tipo == TipoDato.NOTA) DatabaseHelper.COLUMN_NOTA_ID else DatabaseHelper.COLUMN_PASS_ID
        
        val resultado = db.delete(tabla, "$columnaId = ?", arrayOf(dato.id.toString()))
        
        if (resultado > 0) {
            Toast.makeText(this, "Eliminado correctamente", Toast.LENGTH_SHORT).show()
            cargarDatos() // Recargar lista
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    private fun abrirEditor(dato: DatoBoveda) {
        // Por ahora solo un mensaje, luego puedes crear el Activity para editar
        Toast.makeText(this, "Editar: ${dato.titulo}", Toast.LENGTH_SHORT).show()
    }
}
