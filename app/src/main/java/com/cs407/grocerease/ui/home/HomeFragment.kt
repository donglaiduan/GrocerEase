package com.cs407.grocerease.ui.home

import AccountActivity
import BlogAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.grocerease.Blog
import com.cs407.grocerease.R
import com.cs407.grocerease.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class HomeFragment : Fragment() {
    private val blogList = mutableListOf<Blog>()
    private lateinit var blogAdapter: BlogAdapter
    private var selectedImageUri: Uri? = null

    private var dialogView: View? = null

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

        // Initialize the RecyclerView and adapter
        blogAdapter = BlogAdapter(blogList)
        binding.blogContainer.layoutManager = LinearLayoutManager(requireContext())
        binding.blogContainer.adapter = blogAdapter

        val addBlogButton = root.findViewById<ImageView>(R.id.addBlogButton)
        val editProfile = root.findViewById<ImageView>(R.id.editProfile)

        editProfile.setOnClickListener {
            val inflater = LayoutInflater.from(requireContext())
            val editProfileView = inflater.inflate(R.layout.edit_profile, binding.root, false)

            // Clear the current content
            binding.root.removeAllViews()

            // Add the new view (edit profile layout)
            binding.root.addView(editProfileView)
        }

        addBlogButton.setOnClickListener {
            addBlogPopup()
        }

        fetchBlogs()

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
        val uploadImageButton = dialogView.findViewById<Button>(R.id.upload_image_button)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancel_button)
        val uploadButton = dialogView.findViewById<Button>(R.id.upload_button)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)

        val dialog = builder.create()
        dialog.show()

        uploadImageButton.setOnClickListener {
            // Open image picker
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        uploadButton.setOnClickListener {
            uploadBlog(
                titleEditText.text.toString(),
                bodyEditText.text.toString(),
                selectedImageUri
            )
            dialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            // Update the image view with the selected image
            val imageView = dialogView?.findViewById<ImageView>(R.id.image_view)
            imageView?.setImageURI(selectedImageUri)
        }
    }

    private fun uploadBlog(title: String, body: String, imageUri: Uri?) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Add some validation
        if (title.isEmpty() || body.isEmpty()) {
            Toast.makeText(requireContext(), "Title and body cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val blog = Blog(
            username = currentUser?.displayName ?: "Anonymous",
            title = title,
            description = body,
            url = "", // TODO what is url
            timestamp = System.currentTimeMillis()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("blogs").add(blog)
            .addOnSuccessListener { documentReference ->
                // Upload the selected image to Firebase Storage
                imageUri?.let { uri ->
                    // Use a more unique filename to prevent overwriting
                    val filename = "${currentUser?.uid}_${System.currentTimeMillis()}.jpg"
                    val storageRef = FirebaseStorage.getInstance().reference.child("blog_images/$filename")

                    storageRef.putFile(uri)
                        .addOnSuccessListener { taskSnapshot ->
                            taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                                // Update the blog's URL field with the download URL
                                val updatedBlog = blog.copy(url = downloadUrl.toString())
                                db.collection("blogs").document(documentReference.id).set(updatedBlog)
                                    .addOnCompleteListener {
                                        fetchBlogs()
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Image Upload", "Failed to upload image", exception)
                            Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                        }
                } ?: run {
                    // If no image was selected, just refresh blogs
                    fetchBlogs()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Blog Upload", "Failed to add blog", exception)
                Toast.makeText(requireContext(), "Failed to add blog", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchBlogs() {
        val db = FirebaseFirestore.getInstance()
        db.collection("blogs")
            .get()
            .addOnSuccessListener { result ->
                blogList.clear()
                for (document in result) {
                    val blog = document.toObject(Blog::class.java)
                    blogList.add(blog)
                }
                blogAdapter.updateBlogs(blogList)
            }
            .addOnFailureListener { exception ->
                Log.e("Blog Fetch", "Error fetching blogs", exception)
                Toast.makeText(requireContext(), "Failed to fetch blogs", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 123
    }
}