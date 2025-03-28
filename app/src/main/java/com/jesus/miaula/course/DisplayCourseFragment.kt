package com.jesus.miaula.course

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jesus.miaula.AdminActivity
import com.jesus.miaula.R

class DisplayCourseFragment : Fragment(), OnCourseClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CourseAdapter
    private val courseList = mutableListOf<Course>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_course, container, false)
        recyclerView = view.findViewById(R.id.rvCourses)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Cargar cursos globales
        updateResource()

        return view
    }

    override fun onCourseClick(course: Course) {
        val intent = Intent(requireContext(), CourseContentActivity::class.java)
        intent.putExtra("curso", course)
        (activity as? AdminActivity)?.editCourseLauncher?.launch(intent)
    }

    fun updateResource() {
        Firebase.firestore.collection("users")
            .whereEqualTo("role", "Profesor")
            .get()
            .addOnSuccessListener { profesoresresult ->
                val mapaProfesores = profesoresresult.associate {
                    it.id to (it.getString("nombre") ?: "Sin nombre")
                }

                Firebase.firestore.collection("cursos") //Se toman los docuemntos cursos de Firebase
                    .get()
                    .addOnSuccessListener { cursosresult ->
                        courseList.clear()  // limpiar la lista antes de agregar nuevos elementos
                        courseList.addAll(cursosresult.map { it.toObject(Course::class.java) })   // agregar los cursos a la lista convirtiendolos en objeto Course

                        adapter = CourseAdapter(
                            courseList,
                            mapaProfesores,
                            this@DisplayCourseFragment
                        )   // crear el adaptador con la lista de cursos y el listener
                        recyclerView.adapter =
                            adapter  // establecer el adaptador en el RecyclerView
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Error al cargar cursos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
    }
}

