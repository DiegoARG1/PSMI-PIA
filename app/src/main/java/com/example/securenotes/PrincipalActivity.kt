package com.example.securenotes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    // Botones de filtro
    private lateinit var btnTodo: Button
    private lateinit var btnPass: Button
    private lateinit var btnNotas: Button
    
    private var filtroActual = "TODO"

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
        
        // Inicializar botones de filtro
        btnTodo = findViewById(R.id.btnFiltroTodo)
        btnPass = findViewById(R.id.btnFiltroPass)
        btnNotas = findViewById(R.id.btnFiltroNotas)

        configurarFiltros()

        recyclerView.layoutManager = LinearLayoutManager(this)
        
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

    private fun configurarFiltros() {
        btnTodo.setOnClickListener {
            filtroActual = "TODO"
            actualizarEstiloFiltros()
            cargarDatos()
        }
        btnPass.setOnClickListener {
            filtroActual = "PASS"
            actualizarEstiloFiltros()
            cargarDatos()
        }
        btnNotas.setOnClickListener {
            filtroActual = "NOTAS"
            actualizarEstiloFiltros()
            cargarDatos()
        }
    }

    private fun actualizarEstiloFiltros() {
        val colorActivo = ContextCompat.getColorStateList(this, R.color.acento_celeste)
        val colorInactivo = ContextCompat.getColorStateList(this, R.color.superficie)

        btnTodo.backgroundTintList = if (filtroActual == "TODO") colorActivo else colorInactivo
        btnPass.backgroundTintList = if (filtroActual == "PASS") colorActivo else colorInactivo
        btnNotas.backgroundTintList = if (filtroActual == "NOTAS") colorActivo else colorInactivo
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        listaDatos.clear()
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        // Cargar Notas (si el filtro es TODO o NOTAS)
        if (filtroActual == "TODO" || filtroActual == "NOTAS") {
            val cursorNotas = db.query(DatabaseHelper.TABLE_NOTAS, null, null, null, null, null, null)
            if (cursorNotas.moveToFirst()) {
                do {
                    val id = cursorNotas.getInt(cursorNotas.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTA_ID))
                    val titulo = cursorNotas.getString(cursorNotas.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTA_TITULO))
                    listaDatos.add(DatoBoveda(id, titulo, TipoDato.NOTA))
                } while (cursorNotas.moveToNext())
            }
            cursorNotas.close()
        }

        // Cargar Contraseñas (si el filtro es TODO o PASS)
        if (filtroActual == "TODO" || filtroActual == "PASS") {
            val cursorPass = db.query(DatabaseHelper.TABLE_CONTRASENIAS, null, null, null, null, null, null)
            if (cursorPass.moveToFirst()) {
                do {
                    val id = cursorPass.getInt(cursorPass.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS_ID))
                    val titulo = cursorPass.getString(cursorPass.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASS_TITULO))
                    listaDatos.add(DatoBoveda(id, titulo, TipoDato.PASSWORD))
                } while (cursorPass.moveToNext())
            }
            cursorPass.close()
        }

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
            cargarDatos()
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    private fun abrirEditor(dato: DatoBoveda) {
        val intent = if (dato.tipo == TipoDato.NOTA) {
            Intent(this, NuevaNotaActivity::class.java)
        } else {
            Intent(this, NuevaContrasenaActivity::class.java)
        }
        intent.putExtra("ID", dato.id)
        startActivity(intent)
    }
}
