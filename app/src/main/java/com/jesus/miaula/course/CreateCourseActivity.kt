package com.jesus.miaula.course

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jesus.miaula.R
import com.jesus.miaula.databinding.ActivityCreateCourseBinding
import java.util.Calendar

class CreateCourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCourseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Selección de fecha
        binding.etFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val fecha = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    binding.etFecha.setText(fecha)
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
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
            val profesor = binding.etProfesor.text.toString()
            val salon = binding.etSalon.text.toString()
            val capacidad = binding.etCapacidad.text.toString().toIntOrNull() ?: 0

            val curso = Course(
                nombre = name,
                clave = clave,
                profesorId = profesor,
                salon = salon,
                capacidad = capacidad,
                alumnos = listOf()
            )

            val adminUid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            Firebase.firestore.collection("users")
                .document(adminUid)
                .collection("cursos")
                .add(curso)
                .addOnSuccessListener {
                    Toast.makeText(this, "Curso guardado", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            setResult(RESULT_OK) // ← Notifica a AdminActivity que se guardó
            finish()
        }
    }
}
