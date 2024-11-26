package com.cs407.grocerease.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cs407.grocerease.Blog
import com.cs407.grocerease.R
import com.cs407.grocerease.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        val addBlogButton = root.findViewById<Button>(R.id.addBlogButton)

        addBlogButton.setOnClickListener {
            Log.d("Blog Button", "Add Blog Button Clicked!")
            val db = FirebaseFirestore.getInstance()
            db.collection("blogs")
                .get()
                .addOnSuccessListener { result ->
                    Log.d("get db", "get db")
                    Log.d("result", result.size().toString())
                    // Iterate through the documents in the collection
                    for (document in result) {
                        val blog = document.toObject(Blog::class.java)
                        Log.d("Blog Fetch", "Blog: $blog")
                        // Optionally, show a Toast or update the UI
                        Toast.makeText(
                            requireContext(),
                            "Fetched blog: ${blog.description}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Blog Fetch", "Error fetching blogs", exception)
                    Toast.makeText(requireContext(), "Failed to fetch blogs", Toast.LENGTH_SHORT)
                        .show()
                }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}