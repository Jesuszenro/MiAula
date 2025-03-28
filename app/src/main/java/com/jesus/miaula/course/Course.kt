package com.jesus.miaula.course

data class Course(
    val nombre: String = "",
    val clave: String = "",
    val profesorId: String = "Sin asignar",
    val salon: String = "",
    val capacidad: Int = 0,
    val fecha: String = "",
    val hora: String = "",
    val alumnos: List<String> = listOf()
)

