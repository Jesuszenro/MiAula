package com.jesus.miaula.profesor

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jesus.miaula.R
import com.jesus.miaula.alumno.AlumnoGrade
import com.jesus.miaula.databinding.ActivityAddGradeBinding

class AddGradeActivity : AppCompatActivity() {
    private lateinit var adapter: GradeAdapter
    private val alumnos = mutableListOf<AlumnoGrade>()
    private lateinit var asistenciaAdapter: AttendanceAdapter
    private val asistenciasPorFecha = mutableListOf<AttendanceRegist>()
    private lateinit var binding: ActivityAddGradeBinding
    private var claveCurso: String = ""
    private var asistenciaVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGradeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        claveCurso = intent.getStringExtra("claveCurso") ?: ""
        if (claveCurso.isNotEmpty()) {
            loadCourseInfo(claveCurso)
            loadAlumnos(claveCurso)
            loadAsistencias(claveCurso)
        }

        adapter = GradeAdapter(alumnos)
        binding.rvCalificaciones.layoutManager = LinearLayoutManager(this)
        binding.rvCalificaciones.adapter = adapter

        asistenciaAdapter = AttendanceAdapter(asistenciasPorFecha.toMutableList())
        binding.rvAttendance.layoutManager = LinearLayoutManager(this)
        binding.rvAttendance.adapter = asistenciaAdapter
        binding.rvAttendance.visibility = View.GONE

        // Flecha y toggle de asistencia
        binding.layoutToggleAsistencia.setOnClickListener {
            asistenciaVisible = !asistenciaVisible

            // Animación suave (opcional)
            val transition = android.transition.AutoTransition()
            android.transition.TransitionManager.beginDelayedTransition(binding.root, transition)

            binding.rvAttendance.visibility = if (asistenciaVisible) View.VISIBLE else View.GONE
            binding.ivArrowAsistencia.setImageResource(
                if (asistenciaVisible) R.drawable.arrow_drop_up else R.drawable.arrow_drop_down
            )
        }


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
                    binding.tvSalon.text = "Salón: ${curso.getString("salon") ?: "-"}"
                    binding.tvFechaHora.text = "Fecha y hora: ${curso.get("dias")} ${curso.getString("hora") ?: ""}"
                }
            }
    }

    private fun loadAlumnos(clave: String) {
        Firebase.firestore.collection("cursos")
            .whereEqualTo("clave", clave)
            .get()
            .addOnSuccessListener { result ->
                val curso = result.documents.firstOrNull()
                val alumnosIds = curso?.get("alumnos") as? List<String> ?: return@addOnSuccessListener
                val calificaciones = curso.get("calificaciones") as? Map<String, Number> ?: emptyMap()

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

    private fun loadAsistencias(clave: String) {
        Firebase.firestore.collection("cursos")
            .whereEqualTo("clave", clave)
            .get()
            .addOnSuccessListener { result ->
                val cursoDoc = result.documents.firstOrNull() ?: return@addOnSuccessListener
                val cursoRef = cursoDoc.reference

                cursoRef.collection("asistencia")
                    .get()
                    .addOnSuccessListener { fechas ->
                        asistenciasPorFecha.clear()
                        val tareasPendientes = mutableListOf<Task<QuerySnapshot>>()

                        for (fechaDoc in fechas) {
                            val fecha = fechaDoc.id
                            val mapaAsistencias = fechaDoc.data
                                .filterValues { it == true }
                                .mapKeys { it.key }

                            val uids = mapaAsistencias.keys.toList()
                            if (uids.isEmpty()) continue

                            val task = Firebase.firestore.collection("users")
                                .whereIn(FieldPath.documentId(), uids)
                                .get()
                                .addOnSuccessListener { usuarios ->
                                    val nombres = usuarios.map { it.getString("nombre") ?: "Sin nombre" }
                                    asistenciasPorFecha.add(AttendanceRegist(fecha, nombres))
                                }

                            tareasPendientes.add(task)
                        }
                        // Cuando todas las tareas terminen, se actualiza el adapter
                        Tasks.whenAllSuccess<QuerySnapshot>(tareasPendientes)
                            .addOnSuccessListener {
                                // Ordenar por fecha descendente si deseas
                                asistenciasPorFecha.sortByDescending { it.fecha }
                                asistenciaAdapter.actualizarDatos(asistenciasPorFecha)
                            }
                    }
            }
    }

}
