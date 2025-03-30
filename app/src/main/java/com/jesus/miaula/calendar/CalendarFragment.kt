package com.jesus.miaula.calendar

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
import com.jesus.miaula.course.Course
import com.jesus.miaula.course.CourseAdapter
import com.jesus.miaula.course.OnCourseClickListener
import com.jesus.miaula.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.Locale

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
    }
    private fun configCalendar() {
        val days = generateDaysWeek()

        dayAdapter = DayAdapter(days) { selectedDay ->
            // Aquí puedes cargar materias del alumno o profesor, según el tipo
            loadCourses(selectedDay)
        }
        binding.rvDias.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = dayAdapter
        }
    }
    private fun generateDaysWeek(): List<DayWeek>{
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
    private fun loadCourses(selectedDay: DayWeek){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Log.d("DEBUG", "UID actual: $uid")

        Firebase.firestore.collection("cursos")
            .whereEqualTo("profesorId", uid)
            .whereEqualTo("fecha", selectedDay.fecha)
            .get()
            .addOnSuccessListener { result ->
                val courses = result.map { it.toObject(Course::class.java) }
                Toast.makeText(requireContext(), "Cursos encontrados: ${courses.size}", Toast.LENGTH_SHORT).show()
                val adapter = CourseAdapter(
                    listCourses = courses,
                    mapaProfesores = emptyMap(), // no se muestra en modo profesor
                    listener = object : OnCourseClickListener {
                        override fun onCourseClick(course: Course) {
                            Toast.makeText(requireContext(), "Curso: ${course.nombre}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    isProfesorMode = true
                )
                binding.rvCursosDelDia.layoutManager = LinearLayoutManager(requireContext())
                binding.rvCursosDelDia.adapter = adapter
            }
            .addOnFailureListener{
                Toast.makeText(requireContext(), "Error al cargar cursos", Toast.LENGTH_SHORT).show()
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}