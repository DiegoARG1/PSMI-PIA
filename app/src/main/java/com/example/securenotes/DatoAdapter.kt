package com.example.securenotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DatoAdapter(
    private var lista: List<DatoBoveda>,
    private val onEliminarClick: (DatoBoveda) -> Unit,
    private val onEditarClick: (DatoBoveda) -> Unit
) : RecyclerView.Adapter<DatoAdapter.DatoViewHolder>() {

    class DatoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.tvTituloItem)
        val icono: ImageView = view.findViewById(R.id.imgIcono)
        val btnOpciones: ImageButton = view.findViewById(R.id.btnMasOpciones)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dato, parent, false)
        return DatoViewHolder(view)
    }

    override fun onBindViewHolder(holder: DatoViewHolder, position: Int) {
        val dato = lista[position]
        holder.titulo.text = dato.titulo

        if (dato.tipo == TipoDato.NOTA) {
            holder.icono.setImageResource(R.drawable.archivo)
        } else {
            holder.icono.setImageResource(R.drawable.candado)
        }

        holder.btnOpciones.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.inflate(R.menu.menu_item_opciones)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.opcion_editar -> {
                        onEditarClick(dato)
                        true
                    }
                    R.id.opcion_eliminar -> {
                        onEliminarClick(dato)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<DatoBoveda>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
