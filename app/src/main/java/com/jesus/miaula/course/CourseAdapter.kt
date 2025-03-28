package com.jesus.miaula.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jesus.miaula.R

class CourseAdapter(
    private val listCourses: List<Course>,
    private val mapaProfesores: Map<String, String>,
    private val listener: OnCourseClickListener
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre = itemView.findViewById<TextView>(R.id.tvNombreCurso)
        val docente = itemView.findViewById<TextView>(R.id.tvDocente)
        val inscritos = itemView.findViewById<TextView>(R.id.tvInscritos)
        val salon = itemView.findViewById<TextView>(R.id.tvSalon)
        val fechaHora = itemView.findViewById<TextView>(R.id.tvFechaHora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_curso, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        Firebase.firestore.collection("cursos")
            .get()
            .addOnSuccessListener { result ->
                val curso = result.documents[position].toObject(Course::class.java)
            }
        val course = listCourses[position]
        val nombreProfesor = mapaProfesores[course.profesorId] ?: "Sin asignar"
        holder.nombre.text = course.nombre
        holder.docente.text = "Docente: $nombreProfesor"
        holder.inscritos.text = "Alumnos inscritos: ${course.alumnos.size}"
        holder.salon.text = "Salón: ${course.salon}"
        holder.fechaHora.text = "Fecha y hora: ${course.fecha} ${course.hora}"

        // Click sobre el ítem
        holder.itemView.setOnClickListener {
            listener.onCourseClick(course)
        }
    }

    override fun getItemCount(): Int = listCourses.size
}
