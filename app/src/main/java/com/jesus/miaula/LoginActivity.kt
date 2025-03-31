package com.jesus.miaula

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jesus.miaula.admin.AdminActivity
import com.jesus.miaula.alumno.AlumnoActivity
import com.jesus.miaula.databinding.ActivityLoginBinding
import com.jesus.miaula.profesor.ProfesorActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val uid = authResult.user?.uid ?: return@addOnSuccessListener
                    redirigirSegunRol(uid)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun redirigirSegunRol(uid: String) {
        Firebase.firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                when (doc.getString("role")) {
                    "Alumno" -> startActivity(Intent(this, AlumnoActivity::class.java))
                    "Profesor" -> startActivity(Intent(this, ProfesorActivity::class.java))
                    "Administrador" -> startActivity(Intent(this, AdminActivity::class.java))
                    else -> Toast.makeText(this, "Rol no reconocido", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
    }
}
