package com.jesus.miaula.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jesus.miaula.R

data class Usuario(
    val uid: String,
    val nombre: String,
    var rol: String
)

class UserRolAdapter(private val usuarios: List<Usuario>) :
    RecyclerView.Adapter<UserRolAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreUsuario)
        val spinnerRol: Spinner = itemView.findViewById(R.id.spinnerRol)
        val rolesVisibles = listOf("Alumno", "Profesor", "Administrador")
        val rolesDb = listOf("alumno", "profesor", "administrador")

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_rol, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.tvNombre.text = usuario.nombre

        val rolesVisibles = listOf("Alumno", "Profesor", "Administrador")

        val spinnerAdapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, rolesVisibles)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinnerRol.adapter = spinnerAdapter

        // ⚠ Evitar disparar onItemSelected durante setSelection
        holder.spinnerRol.setOnItemSelectedListener(null)

        val index = rolesVisibles.indexOf(usuario.rol.lowercase())
        if (index >= 0) {
            holder.spinnerRol.setSelection(index, false)
        }

        holder.spinnerRol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val nuevoRol = rolesVisibles[pos].lowercase()
                if (nuevoRol != usuario.rol.lowercase()) {
                    Firebase.firestore.collection("users").document(usuario.uid)
                        .update("role", nuevoRol)
                        .addOnSuccessListener {
                            usuario.rol = nuevoRol
                            val realPos = holder.adapterPosition
                            if (realPos != RecyclerView.NO_POSITION) {
                                notifyItemChanged(realPos)
                            }
                            Toast.makeText(holder.itemView.context, "Rol actualizado", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }





    override fun getItemCount() = usuarios.size
}
