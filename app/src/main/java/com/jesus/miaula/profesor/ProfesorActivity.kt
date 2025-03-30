package com.jesus.miaula.profesor

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.jesus.miaula.R
import com.jesus.miaula.calendar.CalendarFragment
import com.jesus.miaula.databinding.ActivityProfesorBinding

class ProfesorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfesorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfesorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
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
                // Otras opciones del menú...
                else -> false
            }
        }
    }
}