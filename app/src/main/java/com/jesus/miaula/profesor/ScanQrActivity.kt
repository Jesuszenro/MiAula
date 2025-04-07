package com.jesus.miaula.profesor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator

class ScanQrActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkCameraPermission() // <- Aquí validas el permiso
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
        } else {
            startScanning()
        }
    }

    private fun startScanning() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Escanea el QR del alumno")
        integrator.setBeepEnabled(true)
        integrator.setOrientationLocked(true)
        integrator.setCameraId(0)
        integrator.initiateScan()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScanning()
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val content = result.contents
                val parts = content.split("|")
                if (parts.size == 4) {
                    val uidAlumno = parts[0]
                    val nombreAlumno = parts[1]
                    val claveCurso = parts[2]
                    val fecha = parts[3]

                    registerAttendace(claveCurso, uidAlumno, nombreAlumno, fecha)
                } else {
                    Toast.makeText(this, "QR inválido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
            }
            finish()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    private fun registerAttendace(claveCurso: String, uidAlumno: String, nombreAlumno: String, fecha: String) {
        Firebase.firestore.collection("cursos")
            .whereEqualTo("clave", claveCurso)
            .get()
            .addOnSuccessListener { result ->
                val cursoDoc = result.documents.firstOrNull()
                if (cursoDoc != null) {
                    val ref = cursoDoc.reference
                    val asistenciaRef = ref.collection("asistencia").document(fecha)
                    val data = hashMapOf <String, Any>(
                        uidAlumno to true
                    )
                    asistenciaRef.set(data, SetOptions.merge()) // ← solo añade o modifica el campo del alumno
                        .addOnSuccessListener {
                            Toast.makeText(this, "Asistencia registrada", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al registrar asistencia", Toast.LENGTH_SHORT).show()
                        }

                } else {
                    Toast.makeText(this, "Curso no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
    }
}


