package com.jesus.miaula.profesor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jesus.miaula.R
import com.jesus.miaula.alumno.AlumnoGrade
import com.jesus.miaula.databinding.ActivityAddGradeBinding

class AddGradeActivity : AppCompatActivity() {
    private lateinit var adapter: GradeAdapter
    private val alumnos = mutableListOf<AlumnoGrade>()
    private lateinit var binding: ActivityAddGradeBinding
    private var claveCurso: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGradeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recupera la clave del intent
        claveCurso = intent.getStringExtra("claveCurso") ?: ""
        if (claveCurso.isNotEmpty()) {
            loadCourseInfo(claveCurso)
            loadAlumnos(claveCurso)
        }

        adapter = GradeAdapter(alumnos)
        binding.rvCalificaciones.layoutManager = LinearLayoutManager(this)
        binding.rvCalificaciones.adapter = adapter

        binding.btnGuardarCalificaciones.setOnClickListener {
            val califMap = mutableMapOf<String, Double>()
            alumnos.forEach {
                califMap[it.uid] = it.calificacion
            }

            Firebase.firestore.collection("cursos")
                .whereEqualTo("clave", claveCurso)
                .get()
                .addOnSuccessListener { result ->
                    val cursoDoc = result.documents.firstOrNull()
                    cursoDoc?.reference?.update("calificaciones", califMap)
                    Toast.makeText(this, "Calificaciones guardadas", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    private fun loadCourseInfo(clave: String) {
        Firebase.firestore.collection("cursos")
            .whereEqualTo("clave", clave)
            .get()
            .addOnSuccessListener { result ->
                val curso = result.documents.firstOrNull()
                if (curso != null) {
                    binding.tvNombreCurso.text = curso.getString("nombre") ?: "Nombre de curso"
                    //binding.tvProfesor.text = "Profesor: ${curso.getString("profesorId") ?: "Sin asignar"}"
                    binding.tvSalon.text = "Salón: ${curso.getString("salon") ?: "-"}"
                    binding.tvFechaHora.text = "Fecha y hora: ${curso.getString("fecha") ?: "-"} ${curso.getString("hora") ?: ""}"
                }
            }
    }

    private fun loadAlumnos(clave: String) {
        Firebase.firestore.collection("cursos")
            .whereEqualTo("clave", clave)
            .get()
            .addOnSuccessListener { result ->
                val curso = result.documents.firstOrNull()
                val alumnosIds =
                    curso?.get("alumnos") as? List<String> ?: return@addOnSuccessListener
                val calificaciones =
                    curso.get("calificaciones") as? Map<String, Number> ?: emptyMap()

                var loadedCount = 0
                for (uid in alumnosIds) {
                    Firebase.firestore.collection("users").document(uid).get()
                        .addOnSuccessListener { userDoc ->
                            val nombre = userDoc.getString("nombre") ?: "Sin nombre"
                            val calif = calificaciones[uid]?.toDouble() ?: 0.0
                            alumnos.add(AlumnoGrade(uid, nombre, calif))

                            loadedCount++
                            if (loadedCount == alumnosIds.size) {
                                adapter.notifyDataSetChanged()
                            }
                        }
                }
            }
    }
}
