package com.cs407.grocerease.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
        val addBlog = root.findViewById<ImageView>(R.id.addBlog)

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
                        Log.d("blog user", blog.username)
                        Log.d("blog descrip", blog.description)
                        Log.d("blog url", blog.url)
                        Log.d("blog time", blog.timestamp.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Blog Fetch", "Error fetching blogs", exception)
                    Toast.makeText(requireContext(), "Failed to fetch blogs", Toast.LENGTH_SHORT)
                        .show()
                }
        }

        addBlog.setOnClickListener {
            addBlogPopup()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addBlogPopup() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.adding_blog_dialog, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.title_edit_text)
        val bodyEditText = dialogView.findViewById<EditText>(R.id.body_edit_text)
        val imageView = dialogView.findViewById<ImageView>(R.id.image_view)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancel_button)
        val uploadButton = dialogView.findViewById<Button>(R.id.upload_button)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)

        val dialog = builder.create()
        dialog.show()

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        uploadButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val body = bodyEditText.text.toString()

            // Here, you can add your logic to save the blog post to Firestore


            dialog.dismiss()
        }

    }
}