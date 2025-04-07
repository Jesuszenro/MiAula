package com.jesus.miaula.profesor

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jesus.miaula.R

class AttendanceAdapter(private val lista: MutableList<AttendanceRegist>) : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    fun actualizarDatos(nuevaLista: List<AttendanceRegist>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val ivArrow: ImageView = itemView.findViewById(R.id.ivArrow)
        val layoutAlumnos: LinearLayout = itemView.findViewById(R.id.layoutAlumnos)
        val headerLayout: LinearLayout = itemView.findViewById(R.id.headerLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attendance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asistencia = lista[position]
        holder.tvFecha.text = asistencia.fecha

        holder.layoutAlumnos.removeAllViews()
        val titulo = TextView(holder.itemView.context)
        titulo.text = "Alumnos que asistieron"
        titulo.setTypeface(null, Typeface.BOLD)
        holder.layoutAlumnos.addView(titulo)

        asistencia.alumnos.forEach {
            val alumnoText = TextView(holder.itemView.context)
            alumnoText.text = it
            alumnoText.setPadding(8, 4, 8, 4)
            holder.layoutAlumnos.addView(alumnoText)
        }

        holder.layoutAlumnos.visibility = if (asistencia.expandido) View.VISIBLE else View.GONE
        holder.ivArrow.setImageResource(
            if (asistencia.expandido) R.drawable.arrow_drop_up else R.drawable.arrow_drop_down
        )

        holder.headerLayout.setOnClickListener {
            asistencia.expandido = !asistencia.expandido
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = lista.size
}


