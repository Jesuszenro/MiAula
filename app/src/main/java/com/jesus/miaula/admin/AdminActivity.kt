package com.jesus.miaula.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jesus.miaula.R
import com.jesus.miaula.course.DisplayCourseFragment

class AdminActivity : AppCompatActivity() {

    private lateinit var createCourseLauncher: ActivityResultLauncher<Intent>
    lateinit var editCourseLauncher: ActivityResultLauncher<Intent>
    private lateinit var fragment: DisplayCourseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)

        // Inicializar fragmento
        fragment = DisplayCourseFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()

        // Launcher para crear curso
        createCourseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                fragment.updateResource()
            }
        }

        // Launcher para editar curso
        editCourseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                fragment.updateResource()
            }
        }

        // Botón flotante para crear curso
        val fab = findViewById<FloatingActionButton>(R.id.fab_central)
        fab.setOnClickListener {
            val intent = Intent(this, CreateCourseActivity::class.java)
            createCourseLauncher.launch(intent)
        }
    }
}

