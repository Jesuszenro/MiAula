package com.jesus.miaula.alumno

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jesus.miaula.R
import com.jesus.miaula.course.GradeCourse
import com.jesus.miaula.databinding.FragmentViewGradesBinding

class ViewGradesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ViewGradesAdapter
    private val listaCalificaciones = mutableListOf<GradeCourse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_view_grades, container, false)
        recyclerView = view.findViewById(R.id.rvCalificaciones)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ViewGradesAdapter(listaCalificaciones)
        recyclerView.adapter = adapter
        return view
    }

    override fun onResume() {
        super.onResume()
        cargarCalificaciones()
    }

    private fun cargarCalificaciones() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = Firebase.firestore

        db.collection("cursos")
            .get()
            .addOnSuccessListener { result ->
                listaCalificaciones.clear()
                Log.d("CALIF", "Total cursos encontrados: ${result.size()}")

                val cursosFiltrados = result.filter { doc ->
                    val alumnosList = doc.get("alumnos") as? List<*> ?: return@filter false
                    val contiene = alumnosList.contains(uid)
                    Log.d("CALIF", "Curso ${doc.getString("nombre")} contiene alumno? $contiene")
                    contiene
                }

                if (cursosFiltrados.isEmpty()) {
                    Log.d("CALIF", "Ningún curso contiene al alumno $uid")
                    adapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                var cursosProcesados = 0

                for (doc in cursosFiltrados) {
                    val nombreCurso = doc.getString("nombre") ?: "Sin nombre"
                    val profesorId = doc.getString("profesorId") ?: ""
                    val fecha = doc.getString("fecha") ?: ""
                    val calificacionesMap = doc.get("calificaciones") as? Map<*, *>
                    val calificacion = calificacionesMap?.get(uid)?.toString()?.toDoubleOrNull() ?: 0.0

                    Log.d("CALIF", "Procesando curso: $nombreCurso, profesorId: $profesorId, calif: $calificacion")

                    db.collection("users").document(profesorId).get()
                        .addOnSuccessListener { profDoc ->
                            val nombreProfesor = profDoc.getString("nombre") ?: "Sin asignar"
                            val calificacionCurso = GradeCourse(nombreCurso, nombreProfesor, fecha, calificacion)
                            listaCalificaciones.add(calificacionCurso)
                            Log.d("CALIF", "Profesor: $nombreProfesor agregado a lista")

                            cursosProcesados++
                            if (cursosProcesados == cursosFiltrados.size) {
                                adapter.notifyDataSetChanged()
                                Log.d("CALIF", "Adapter actualizado con ${listaCalificaciones.size} cursos")
                            }
                        }
                        .addOnFailureListener {
                            Log.e("CALIF", "Error al obtener profesor con ID $profesorId", it)
                            cursosProcesados++
                            if (cursosProcesados == cursosFiltrados.size) {
                                adapter.notifyDataSetChanged()
                            }
                        }
                }
            }
    }
}



