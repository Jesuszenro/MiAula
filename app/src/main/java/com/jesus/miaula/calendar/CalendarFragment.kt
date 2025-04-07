package com.jesus.miaula.calendar

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.jesus.miaula.alumno.GenerateQr
import com.jesus.miaula.course.Course
import com.jesus.miaula.course.CourseAdapter
import com.jesus.miaula.course.CourseContentActivity
import com.jesus.miaula.course.OnCourseClickListener
import com.jesus.miaula.databinding.FragmentCalendarBinding
import com.jesus.miaula.profesor.AddGradeActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var dayAdapter: DayAdapter

    override fun onCreateView(
        inflater : LayoutInflater, container: ViewGroup?,
        savedInstance: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(requireContext(), "CalendarFragment cargado", Toast.LENGTH_SHORT).show()

        configCalendar()
        var qrVisible = false
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            Firebase.firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role") ?: ""
                    val nombre = document.getString("nombre") ?: "Usuario"
                    binding.tvBienvenido.text = "Bienvenido, $nombre"
                    if (role == "Alumno") {
                        binding.btnGenerarQr.visibility = View.VISIBLE
                        binding.ivQrCode.visibility = View.GONE
                        val qrHelper = GenerateQr(requireContext())
                        binding.btnGenerarQr.setOnClickListener {
                            if (qrVisible) {
                                // Ocultar el QR
                                binding.ivQrCode.setImageBitmap(null)
                                binding.ivQrCode.visibility = View.GONE
                                qrVisible = false
                            } else {
                                // Mostrar el QR si hay clase activa
                                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
                                qrHelper.generateQrForToday(binding.ivQrCode) // Ya genera el código y lo asigna
                                binding.ivQrCode.visibility = View.VISIBLE
                                qrVisible = true
                            }
                        }
                    }
                }
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val dayName = listOf("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom")[(dayOfWeek + 5) % 7]
            val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
            val today = DayWeek(dayName, dayNumber, currentDate)

            loadCourses(today)
            dayAdapter.setSelectedDay(today)
        }
    }

    private fun configCalendar() {
        val days = generateDaysWeek()

        dayAdapter = DayAdapter(days) { selectedDay ->
            loadCourses(selectedDay)
        }
        binding.rvDias.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = dayAdapter
        }
    }

    private fun generateDaysWeek(): List<DayWeek> {
        val weekDays = listOf("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom")
        val days = mutableListOf<DayWeek>()
        val calendar = java.util.Calendar.getInstance()

        for (i in 0..6) {
            val number = calendar.get(java.util.Calendar.DAY_OF_MONTH)
            val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
            val name = weekDays[(dayOfWeek + 5) % 7]
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            days.add(DayWeek(name, number, date))
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }

    private fun loadCourses(selectedDay: DayWeek) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val nombreDiaCompleto = mapOf(
            "Lun" to "Lunes",
            "Mar" to "Martes",
            "Mie" to "Miércoles",
            "Jue" to "Jueves",
            "Vie" to "Viernes",
            "Sab" to "Sábado",
            "Dom" to "Domingo"
        )

        val dayCapitalized = nombreDiaCompleto[selectedDay.nombre] ?: selectedDay.nombre

        val userDocRef = Firebase.firestore.collection("users").document(uid)
        userDocRef.get().addOnSuccessListener { document ->
            val role = document.getString("role") ?: ""

            val query = when (role) {
                "Profesor" -> {
                    Firebase.firestore.collection("cursos")
                        .whereEqualTo("profesorId", uid)
                        .whereArrayContains("dias", dayCapitalized)
                }
                "Alumno" -> {
                    // Solo podemos hacer un whereArrayContains aquí
                    Firebase.firestore.collection("cursos")
                        .whereArrayContains("alumnos", uid)
                }
                else -> null
            }

            query?.get()?.addOnSuccessListener { result ->
                // Para alumnos, filtra manualmente por día
                val cursosFiltrados = if (role == "Alumno") {
                    result.filter { doc ->
                        val dias = doc.get("dias") as? List<*> ?: emptyList<Any>()
                        dias.contains(dayCapitalized)
                    }
                } else {
                    result.toList()
                }

                val cursos = cursosFiltrados.map { it.toObject(Course::class.java) }

                val adapter = CourseAdapter(
                    listCourses = cursos,
                    mapaProfesores = emptyMap(),
                    listener = object : OnCourseClickListener {
                        override fun onCourseClick(course: Course) {
                            if (role == "Profesor") {
                                Log.d("CLICK", "Abriendo AddGradeActivity para ${course.clave}")
                                val intent = Intent(requireContext(), AddGradeActivity::class.java)
                                intent.putExtra("claveCurso", course.clave)
                                startActivity(intent)
                            } else if (role == "Alumno") {
                                Toast.makeText(requireContext(), "Curso: ${course.nombre}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    isProfesorMode = role == "Profesor"
                )

                binding.rvCursosDelDia.layoutManager = LinearLayoutManager(requireContext())
                binding.rvCursosDelDia.adapter = adapter
            }?.addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar cursos", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "No se pudo obtener el rol del usuario", Toast.LENGTH_SHORT).show()
        }
    }

    /*
    private fun generateQr(uid: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        Log.d("QRDEBUG", "Hora actual: $currentTime")
        Log.d("QRDEBUG", "Fecha actual: $currentDate")

        Firebase.firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { userDoc ->
                val userName = userDoc.getString("nombre") ?: "Sin nombre"

                Firebase.firestore.collection("cursos")
                    .whereArrayContains("alumnos", uid)
                    .whereEqualTo("fecha", currentDate)
                    .get()
                    .addOnSuccessListener { result ->
                        val cursoActual = result.documents.firstOrNull { doc ->
                            val horaCurso = doc.getString("hora") ?: ""
                            Log.d("QRDEBUG", "Evaluando curso con hora $horaCurso")
                            isOnRange(horaCurso, currentTime)
                        }

                        if (cursoActual != null) {
                            val clave = cursoActual.getString("clave") ?: return@addOnSuccessListener
                            val qrData = "$uid|$userName|$clave"

                            val writer = QRCodeWriter()
                            val bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512)
                            val bmp = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)

                            for (x in 0 until 512) {
                                for (y in 0 until 512) {
                                    bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                                }
                            }
                            binding.ivQrCode.setImageBitmap(bmp)
                        } else {
                            Toast.makeText(requireContext(), "No tienes clases en este momento", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
    }

    fun isOnRange(courseHour: String, currentHour: String, margenMin: Int = 10): Boolean {
        val formato = SimpleDateFormat("HH:mm", Locale.getDefault())
        return try {
            val horaCursoDate = formato.parse(courseHour)
            val horaActualDate = formato.parse(currentHour)

            val diferencia = abs(horaCursoDate.time - horaActualDate.time)
            val margenMillis = margenMin * 60 * 1000
            Log.d("QRDEBUG", "Diferencia entre horas: $diferencia ms (margen: $margenMillis ms)")
            diferencia <= margenMillis
        } catch (e: Exception) {
            Log.e("QRDEBUG", "Error parseando hora: ${e.message}")
            false
        }
    }*/
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
