package com.jesus.miaula.alumno

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class GenerateQr(private val context: Context) {

    fun isOnRange(courseHour: String, currentHour: String, margenMin: Int = 10): Boolean {
        val formato = SimpleDateFormat("HH:mm", Locale.getDefault())
        return try {
            val horaCursoDate = formato.parse(courseHour)
            val horaActualDate = formato.parse(currentHour)

            val diferencia = abs(horaCursoDate.time - horaActualDate.time)
            val margenMillis = margenMin * 60 * 1000
            diferencia <= margenMillis
        } catch (e: Exception) {
            Log.e("QRDEBUG", "Error al comparar horas: ${e.message}")
            false
        }
    }

    fun generarQrBitmap(data: String, size: Int = 512): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size)
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

        for (x in 0 until size) {
            for (y in 0 until size) {
                bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }

    fun generateQrForToday(ivQrCode: ImageView) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        db.collection("users").document(uid).get()
            .addOnSuccessListener { userDoc ->
                val nombre = userDoc.getString("nombre") ?: "Sin nombre"

                db.collection("cursos")
                    .whereArrayContains("alumnos", uid)
                    .get()
                    .addOnSuccessListener { result ->
                        val cursoActual = result.documents.firstOrNull { doc ->
                            val dias = doc.get("dias") as? List<*> ?: return@firstOrNull false
                            val diaActual = SimpleDateFormat("EEEE", Locale("es", "ES")).format(Date()).replaceFirstChar { it.uppercase() }
                            val horaCurso = doc.getString("hora") ?: ""
                            uid in (doc["alumnos"] as? List<*> ?: emptyList<Any>()) &&
                                    dias.contains(diaActual) &&
                                    isOnRange(horaCurso, currentTime)
                        }

                        if (cursoActual != null) {
                            val clave = cursoActual.getString("clave") ?: return@addOnSuccessListener
                            val cursoId = cursoActual.id

                            // Subcolección asistencia
                            val asistenciaRef = db.collection("cursos").document(cursoId)
                                .collection("asistencia").document(currentDate)

                            asistenciaRef.get().addOnSuccessListener { docSnap ->
                                if (!docSnap.exists()) {
                                    asistenciaRef.set(mapOf(uid to false)) // Primer alumno marca presencia
                                } else {
                                    asistenciaRef.update(uid, false)
                                }
                            }

                            // Generar QR con clave + uid + fecha
                            val qrData = "$uid|$nombre|$clave|$currentDate"
                            val bmp = generarQrBitmap(qrData)
                            ivQrCode.setImageBitmap(bmp)
                        } else {
                            Toast.makeText(context, "No tienes clases en este momento", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
    }
}
