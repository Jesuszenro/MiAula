package com.jesus.miaula.calendar

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jesus.miaula.R
import com.jesus.miaula.databinding.ItemDayBinding

class DayAdapter (
    private val days: List<DayWeek>,
    private val onClick: (DayWeek) -> Unit
) : RecyclerView.Adapter<DayAdapter.DayViewHolder>(){

    private var select = 0

    inner class DayViewHolder(val binding: ItemDayBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.binding.tvDiaSemana.text = day.nombre
        holder.binding.tvNumeroDia.text = day.numero.toString()

        val isSelected = position == select

        // Estilo dinámico de texto
        holder.binding.tvDiaSemana.apply {
            setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
            setTextColor(if (isSelected) Color.parseColor("#003366") else Color.GRAY)
        }

        holder.binding.tvNumeroDia.apply {
            setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
            setTextColor(if (isSelected) Color.parseColor("#003366") else Color.GRAY)
        }

        holder.itemView.setOnClickListener {
            val realPosition = holder.adapterPosition
            if (realPosition != RecyclerView.NO_POSITION) {
                select = realPosition
                notifyDataSetChanged()
                onClick(days[realPosition])
            }
        }
    }
    override fun getItemCount(): Int = days.size
}