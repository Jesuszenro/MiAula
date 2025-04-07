package com.jesus.miaula.admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jesus.miaula.R
import com.jesus.miaula.databinding.ActivityAdminUserRolBinding

class AdminUserRolActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminUserRolBinding
    private val listaUsuarios = mutableListOf<Usuario>()
    private lateinit var adapter: UserRolAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUserRolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UserRolAdapter(listaUsuarios)
        binding.rvUsuarios.layoutManager = LinearLayoutManager(this)
        binding.rvUsuarios.adapter = adapter

        cargarUsuarios()

    }

    private fun cargarUsuarios() {
        Firebase.firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                listaUsuarios.clear()
                for (doc in result.documents) {
                    val uid = doc.id
                    val nombre = doc.getString("nombre") ?: "Sin nombre"
                    val rol = doc.getString("role") ?: "alumno"
                    listaUsuarios.add(Usuario(uid, nombre, rol))
                }
                adapter.notifyDataSetChanged()
            }
    }
}