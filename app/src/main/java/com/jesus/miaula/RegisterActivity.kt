package com.jesus.miaula

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.jesus.miaula.databinding.ActivityRegisterBinding
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val roles = listOf("Alumno", "Profesor", "Administrador")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, roles)
        binding.autoCompleteRol.setAdapter(adapter)
        Log.d("SPINNER_DEBUG", "Adapter count: ${adapter.count}")
        //Button behaviour
        binding.btnRegister.setOnClickListener {
            val nombre = binding.editTextText.text.toString()
            val id = binding.id.text.toString()
            val email = binding.email.text.toString()
            val password = binding.editTextNumber.text.toString()
            val role = binding.autoCompleteRol.text.toString()
            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            //Connect Firebase
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                //Save user data
                val userData = hashMapOf(
                    "nombre" to nombre,
                    "id" to id,
                    "email" to email,
                    "password" to password,
                    "role" to role
                )
                //Save user data in Firebase
                Firebase.firestore.collection("users").document(uid).set(userData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registro exitoso como $role", Toast.LENGTH_SHORT)
                            .show()
                        when (role) {
                            //Redirect to main activity
                            "Alumno" -> startActivity(Intent(this, MainActivity::class.java))
                            "Profesor" -> startActivity(Intent(this, MainActivity::class.java))
                            "Administrador" -> startActivity(Intent(this, MainActivity::class.java))
                        }
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al registrarse", Toast.LENGTH_SHORT).show()
                    }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al registrarse", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}