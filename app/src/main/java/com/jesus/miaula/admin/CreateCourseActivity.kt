package com.jesus.miaula.admin

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jesus.miaula.course.Course
import com.jesus.miaula.databinding.ActivityCreateCourseBinding
import java.util.Calendar

class CreateCourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCourseBinding
    private val listaProfesores = mutableListOf<Pair<String, String>>() // UID - nombre
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cargarProfesores()
        val diasSemana =
            arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        val diasSeleccionados = mutableListOf<String>()

        binding.etFecha.setOnClickListener {
            val seleccionados = BooleanArray(diasSemana.size)
            AlertDialog.Builder(this)
                .setTitle("Selecciona los días")
                .setMultiChoiceItems(diasSemana, seleccionados) { _, index, isChecked ->
                    if (isChecked) diasSeleccionados.add(diasSemana[index])
                    else diasSeleccionados.remove(diasSemana[index])
                }
                .setPositiveButton("Aceptar") { _, _ ->
                    binding.etFecha.setText(diasSeleccionados.joinToString(", "))
                }
                .show()
        }

        // Selección de hora
        binding.etHora.setOnClickListener {
            val calendario = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val hora = String.format("%02d:%02d", hourOfDay, minute)
                    binding.etHora.setText(hora)
                },
                calendario.get(Calendar.HOUR_OF_DAY),
                calendario.get(Calendar.MINUTE),
                true
            )
            timePicker.show()
        }

        binding.btnRegistrar.setOnClickListener {
            val name = binding.etNombreCurso.text.toString()
            val clave = binding.etClaveCurso.text.toString()
            val profesorIndex = binding.spinnerProfesor.selectedItemPosition
            val profesor = if (profesorIndex != -1) listaProfesores[profesorIndex].first else ""
            val salon = binding.etSalon.text.toString()
            val capacidad = binding.etCapacidad.text.toString().toIntOrNull() ?: 0
            val hora = binding.etHora.text.toString()

            Firebase.firestore.collection("cursos")
                .whereEqualTo("clave", clave)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        Toast.makeText(this, "Ya existe un curso con esa clave", Toast.LENGTH_SHORT)
                            .show()
                        return@addOnSuccessListener // ← Detiene aquí si ya existe
                    }

                    val curso = Course(
                        nombre = name,
                        clave = clave,
                        profesorId = profesor,
                        salon = salon,
                        capacidad = capacidad,
                        dias = diasSeleccionados,
                        hora = hora,
                        alumnos = listOf()
                    )

                    Firebase.firestore.collection("cursos")
                        .add(curso)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Curso registrado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            setResult(RESULT_OK)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                }
        }
    }
        private fun cargarProfesores() {
        Firebase.firestore.collection("users")
            .whereEqualTo("role", "Profesor")
            .get()
            .addOnSuccessListener { result ->
                val nombres = result.map {
                    val uid = it.id
                    val nombre = it.getString("nombre") ?: ""
                    listaProfesores.add(uid to nombre)
                    nombre
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerProfesor.adapter = adapter
            }
    }
}
