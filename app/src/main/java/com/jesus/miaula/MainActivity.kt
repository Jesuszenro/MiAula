package com.jesus.miaula

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jesus.miaula.admin.AdminActivity
import com.jesus.miaula.alumno.AlumnoActivity
import com.jesus.miaula.loginRegistro.LoginActivity
import com.jesus.miaula.profesor.ProfesorActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("MiaulaPrefs", MODE_PRIVATE)
        val uid = prefs.getString("uid", null)
        val rol = prefs.getString("role", null)

        val intent = when {
            uid != null && rol != null -> {
                when (rol.lowercase()) {
                    "alumno" -> Intent(this, AlumnoActivity::class.java)
                    "profesor" -> Intent(this, ProfesorActivity::class.java)
                    "administrador" -> Intent(this, AdminActivity::class.java)
                    else -> Intent(this, LoginActivity::class.java)
                }
            }
            else -> Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}
