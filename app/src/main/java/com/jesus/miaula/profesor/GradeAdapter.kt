package com.jesus.miaula.profesor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.jesus.miaula.R
import com.jesus.miaula.alumno.AlumnoGrade

class GradeAdapter (
    private val alumnos: List<AlumnoGrade>
) : RecyclerView.Adapter<GradeAdapter.GradeViewHolder>(){

    class GradeViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvNombre = view.findViewById<TextView>(R.id.tvNombreAlumno)
        val etGrade = view.findViewById<EditText>(R.id.etCalificacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grade, parent, false)
        return GradeViewHolder(view)
    }
    override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
        val alumno = alumnos[position]
        holder.tvNombre.text = alumno.nombre
        holder.etGrade.setText(alumno.calificacion.toString())

        holder.etGrade.doOnTextChanged { text, _, _, _ ->
            alumno.calificacion = text.toString().toDoubleOrNull() ?: 0.0
        }
    }
    override fun getItemCount() = alumnos.size
}