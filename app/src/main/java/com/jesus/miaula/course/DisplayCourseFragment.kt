package com.jesus.miaula.course

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jesus.miaula.R

class DisplayCourseFragment : Fragment() {
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
            updateResource()
            return view
        }
    fun updateResource() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Firebase.firestore.collection("users")
            .document(uid)
            .collection("cursos")
            .get()
            .addOnSuccessListener { result ->
                val cursos = result.map { it.toObject(Course::class.java) }
                adapter = CourseAdapter(cursos)
                recyclerView.adapter = adapter
            }
    }
}