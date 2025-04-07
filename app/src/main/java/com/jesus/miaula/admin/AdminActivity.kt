package com.jesus.miaula.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jesus.miaula.R
import com.jesus.miaula.alumno.ViewGradesFragment
import com.jesus.miaula.course.DisplayCourseFragment
import com.jesus.miaula.databinding.ActivityAdminBinding
import com.jesus.miaula.loginRegistro.ProfileFragment

class AdminActivity : AppCompatActivity() {

    private lateinit var createCourseLauncher: ActivityResultLauncher<Intent>
    lateinit var editCourseLauncher: ActivityResultLauncher<Intent>
    private lateinit var fragment: DisplayCourseFragment
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ Inicializa correctamente ViewBinding
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // FAB para crear curso
        binding.fabCentral.setOnClickListener {
            val intent = Intent(this, CreateCourseActivity::class.java)
            createCourseLauncher.launch(intent)
        }

        // ✅ Menú inferior: cambio de actividad
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_manage_users -> {
                    startActivity(Intent(this, AdminUserRolActivity::class.java))
                    true
                }
                R.id.nav_perfilad -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, ProfileFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}


