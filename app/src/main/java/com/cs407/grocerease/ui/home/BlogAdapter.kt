package com.cs407.grocerease.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.grocerease.Blog
import com.cs407.grocerease.R

class BlogAdapter(private var blogs: List<Blog>) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.blogTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.blogDescription)
        val usernameTextView: TextView = itemView.findViewById(R.id.blogUsername)
        val timestampTextView: TextView = itemView.findViewById(R.id.blogTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.blog_item, parent, false)
        return BlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = blogs[position]
        holder.titleTextView.text = blog.title
        holder.descriptionTextView.text = blog.description
        holder.usernameTextView.text = blog.username
        holder.timestampTextView.text = blog.timestamp.toString()
    }

    override fun getItemCount(): Int = blogs.size

    // Method to update the list of blogs
    fun updateBlogs(newBlogs: List<Blog>) {
        blogs = newBlogs
        notifyDataSetChanged()
    }
}