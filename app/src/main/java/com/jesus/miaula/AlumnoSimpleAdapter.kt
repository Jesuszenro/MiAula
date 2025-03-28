package com.jesus.miaula

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlumnoSimpleAdapter(private val alumnos: List<String>) :
    RecyclerView.Adapter<AlumnoSimpleAdapter.AlumnoViewHolder>() {

    class AlumnoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre = view.findViewById<TextView>(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return AlumnoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        holder.nombre.text = alumnos[position]
    }

    override fun getItemCount(): Int = alumnos.size
}
