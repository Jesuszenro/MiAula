package com.jesus.miaula.alumno

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jesus.miaula.R
import com.jesus.miaula.course.GradeCourse

class ViewGradesAdapter(private val calificaciones: List<GradeCourse>) :
    RecyclerView.Adapter<ViewGradesAdapter.GradeViewHolder>() {

    inner class GradeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreCurso: TextView = view.findViewById(R.id.tvNombreCurso)
        val tvProfesor: TextView = view.findViewById(R.id.tvProfesor)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvCalificacion: TextView = view.findViewById(R.id.tvCalificacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course_graded, parent, false)
        return GradeViewHolder(view)
    }

    override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
        val item = calificaciones[position]
        holder.tvNombreCurso.text = item.nombreCurso
        holder.tvProfesor.text = "Profesor: ${item.profesor}"
        holder.tvFecha.text = "Fecha: ${item.fecha}"
        holder.tvCalificacion.text = item.calificacion.toString()
    }

    override fun getItemCount(): Int = calificaciones.size
}


