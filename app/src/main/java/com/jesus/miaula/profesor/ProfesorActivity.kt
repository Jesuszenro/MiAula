package com.jesus.miaula.profesor

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.jesus.miaula.R
import com.jesus.miaula.calendar.CalendarFragment
import com.jesus.miaula.databinding.ActivityProfesorBinding
import com.jesus.miaula.loginRegistro.ProfileFragment

class ProfesorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfesorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfesorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CalendarFragment())
            .commit()

        // Marca el ítem de calendario como seleccionado visualmente
        binding.bottomNav.selectedItemId = R.id.nav_calendario

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_calendario -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, CalendarFragment())
                        .commit()
                    true
                }
                R.id.nav_scan -> {
                    startActivity(Intent(this, ScanQrActivity::class.java))
                    true
                }
                R.id.nav_perfil -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ProfileFragment())
                        .commit()
                    true
                }
                // Otras opciones del menú...
                else -> false
            }
        }
    }
}