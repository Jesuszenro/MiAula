package com.jesus.miaula.alumno

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.jesus.miaula.R
import com.jesus.miaula.calendar.CalendarFragment
import com.jesus.miaula.databinding.ActivityAlumnoBinding

class AlumnoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlumnoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CalendarFragment())
            .commit()
        binding.bottomNav.selectedItemId = R.id.nav_calendario
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_calendario -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, CalendarFragment())
                        .commit()
                    true
                }
                // Otras opciones del menú...
                R.id.nav_tareas -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ViewGradesFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}