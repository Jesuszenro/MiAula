package com.jesus.miaula

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jesus.miaula.course.CreateCourseActivity
import com.jesus.miaula.course.DisplayCourseFragment

class AdminActivity : AppCompatActivity() {

    private lateinit var createCourseLauncher: ActivityResultLauncher<Intent>
    private lateinit var fragment: DisplayCourseFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)

        // Mostrar el fragmento
        fragment = DisplayCourseFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()

        createCourseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK){
                fragment.updateResource()
            }
        }
        // Botón flotante para crear curso
        val fab = findViewById<FloatingActionButton>(R.id.fab_central)
        fab.setOnClickListener {
            val intent = Intent(this, CreateCourseActivity::class.java)
            createCourseLauncher.launch(intent)
        }
        // Opcional: cargar menú en BottomAppBar si lo usas
        // val bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        // bottomAppBar.replaceMenu(R.menu.bottom_nav_admin)
    }
}
