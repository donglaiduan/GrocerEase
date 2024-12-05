package com.cs407.grocerease.ui.home

import AccountActivity
import android.app.AlertDialog
import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.oAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    private val blogList = mutableListOf<Blog>()

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
        val addBlogButton = root.findViewById<ImageView>(R.id.addBlogButton)
        val editProfile = root.findViewById<ImageView>(R.id.editProfile)

        editProfile.setOnClickListener {
//            val intent = Intent(requireContext(), AccountActivity::class.java)
//            startActivity(intent)

            val inflater = LayoutInflater.from(requireContext())
            val editProfileView = inflater.inflate(R.layout.edit_profile, binding.root, false)

            // Clear the current content
            binding.root.removeAllViews()

            // Add the new view (edit profile layout)
            binding.root.addView(editProfileView)
        }

        addBlogButton.setOnClickListener {
            addBlogPopup()
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
                        Log.d("blog user", blog.username.toString())
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

        fetchBlogs()



        // Populate the view with blog data
        populateBlogView()

        Log.d("blog fetching", blogList.size.toString())



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
            val currentUser = FirebaseAuth.getInstance().currentUser

            val blog = Blog(
                username = currentUser?.displayName,
                title = title,
                description = body, url = "",
                timestamp = System.currentTimeMillis()
            )

            // should add this code to update display name for users
//            val profileUpdates = UserProfileChangeRequest.Builder()
//                .setDisplayName("Your Username")
//                .build()
//
//            FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
//                ?.addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Log.d("Update Profile", "User profile updated.")
//                    }
//                }

            val db = FirebaseFirestore.getInstance()
            db.collection("blogs").add(blog)


            dialog.dismiss()
        }

    }

    private fun fetchBlogs() {
        val db = FirebaseFirestore.getInstance()
        db.collection("blogs")
            .get()
            .addOnSuccessListener { result ->
                blogList.clear()
                for (document in result) {
                    Log.d("***", document.toString())
                    val blog = document.toObject(Blog::class.java)
                    blogList.add(blog)
                }
                populateBlogView()
            }
            .addOnFailureListener { exception ->
                Log.e("Blog Fetch", "Error fetching blogs", exception)
                Toast.makeText(requireContext(), "Failed to fetch blogs", Toast.LENGTH_SHORT).show()
            }
    }

    private fun populateBlogView() {
        // Clear any existing blog views
//        binding.blogContainer.removeAllViews()

        // Inflate and add blog views
        for (blog in blogList) {
            Log.d("hit", "hit")
            val blogView = layoutInflater.inflate(R.layout.blog_item, binding.blogContainer, false)
//            blogView.findViewById<TextView>(R.id.blogTitle).text = blog.title
            blogView.findViewById<TextView>(R.id.blogTitle).text = blog.title
            blogView.findViewById<TextView>(R.id.blogDescription).text = blog.description
            blogView.findViewById<TextView>(R.id.blogUsername).text = blog.username
            blogView.findViewById<TextView>(R.id.blogTimestamp).text = blog.timestamp.toString()
//            Glide.with(requireContext())
//                .load(blog.url)
//                .into(blogView.findViewById(R.id.blogImage))
            binding.blogContainer.addView(blogView)
        }
    }
}