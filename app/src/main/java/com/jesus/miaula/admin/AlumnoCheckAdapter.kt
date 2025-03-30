package com.jesus.miaula.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.jesus.miaula.R

class AlumnoCheckAdapter(private val alumnos: List<Pair<String, String>>) :
    RecyclerView.Adapter<AlumnoCheckAdapter.ViewHolder>() {

    val seleccionados = mutableSetOf<String>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkAlumno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkbox_alumno, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (uid, nombre) = alumnos[position]
        holder.checkBox.text = nombre
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) seleccionados.add(uid)
            else seleccionados.remove(uid)
        }
    }

    override fun getItemCount(): Int = alumnos.size
}
