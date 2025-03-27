package com.jesus.miaula.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jesus.miaula.databinding.FragmentCalendarBinding

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
            val date = java.text.SimpleDateFormat("dd/MM/yyyy").format(calendar.time)
            days.add(DayWeek(name, number, date))
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }
    private fun loadCourses(selectedDay: DayWeek){
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}