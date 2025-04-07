package com.jesus.miaula.profesor

data class AttendanceRegist (
    val fecha: String,
    val alumnos: List<String>,
    var expandido: Boolean = false
)