package com.jesus.miaula.course

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jesus.miaula.admin.AlumnoCheckAdapter
import com.jesus.miaula.databinding.ActivityCourseContentBinding

class CourseContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseContentBinding
    private lateinit var curso: Course
    private lateinit var alumnoAdapter: AlumnoCheckAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        curso = intent.getSerializableExtra("curso") as? Course ?: return

        // Mostrar datos del curso
        binding.tvCourseName.text = curso.nombre
        Firebase.firestore.collection("users")
            .document(curso.profesorId)
            .get()
            .addOnSuccessListener { doc ->
                val nombreProfesor = doc.getString("nombre") ?: "Sin asignar"
                binding.tvDocente.text = "Profesor: $nombreProfesor"
            }
            .addOnFailureListener{
                binding.tvDocente.text = "Profesor: Sin asignar"
            }
        binding.tvSalon.text = "Salón: ${curso.salon}"

        // Configurar RecyclerView
        binding.rvAlumnosSeleccionables.layoutManager = LinearLayoutManager(this)

        cargarAlumnos()

        // Botón aceptar cambios
        binding.btnAceptar.setOnClickListener {
            guardarCambios()
        }
        updateAlumnos()
        binding.btnDelCourse.setOnClickListener {
            deleteCourse()

        }
    }


    private fun cargarAlumnos() {
        Firebase.firestore.collection("users")
            .whereEqualTo("role", "Alumno")
            .get()
            .addOnSuccessListener { result ->
                val alumnos = result.map { it.id to (it.getString("nombre") ?: "Sin nombre") }
                alumnoAdapter = AlumnoCheckAdapter(alumnos)
                binding.rvAlumnosSeleccionables.adapter = alumnoAdapter
            }
    }

    private fun guardarCambios() {
        val alumnosSeleccionados = alumnoAdapter.seleccionados.toList()

        Firebase.firestore.collection("cursos")
            .whereEqualTo("clave", curso.clave)
            .get()
            .addOnSuccessListener { result ->
                val doc = result.documents.firstOrNull()
                doc?.reference?.update("alumnos", alumnosSeleccionados)
                    ?.addOnSuccessListener {
                        Toast.makeText(this, "Alumnos actualizados", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK) // <- Esto notifica a AdminActivity
                        finish()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
    }
    private fun updateAlumnos() {
        if (curso.alumnos.isEmpty()) return

        Firebase.firestore.collection("users")
            .whereIn(FieldPath.documentId(), curso.alumnos)
            .get()
            .addOnSuccessListener { result ->
                val nombres = result.map { it.getString("nombre") ?: "Sin nombre" }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerAlumnosInscritos.adapter = adapter
            }
    }
    private fun deleteCourse() {
        AlertDialog.Builder(this)
            .setTitle("¿Estas seguro que quieres eliminar este curso?")
            .setMessage("Esta acción no se puede deshacer")
            .setPositiveButton("Aceptar") { _, _ ->
                Firebase.firestore.collection("cursos")
                    .whereEqualTo("clave", curso.clave)
                    .get()
                    .addOnSuccessListener { result ->
                        val doc = result.documents.firstOrNull()
                        doc?.reference?.delete()
                            ?.addOnSuccessListener {
                                Toast.makeText(this, "Curso eliminado", Toast.LENGTH_SHORT).show()
                                setResult(RESULT_OK) // <- Esto notifica a AdminActivity
                                finish()
                            }
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
