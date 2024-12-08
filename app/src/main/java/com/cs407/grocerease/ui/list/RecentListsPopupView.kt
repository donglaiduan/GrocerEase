package com.cs407.grocerease.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.grocerease.R

class RecentListsPopupView (
    private val recentLists: MutableList<String>,
    private val onDeleteClick: (Int) -> Unit,
    private val onSetCurrentClick: (Int) -> Unit
    ) : RecyclerView.Adapter<RecentListsPopupView.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val deleteButton: Button = itemView.findViewById(R.id.recentListDeleteButton)
            val setCurrentButton: Button = itemView.findViewById(R.id.recentListSetCurrentButton)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_lists_individual_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listInfo = recentLists[position].split("*")
        val listTitle = listInfo[0]

        holder.itemView.findViewById<TextView>(R.id.recentListTitle).text = listTitle
        holder.deleteButton.setOnClickListener { onDeleteClick(position) }
        holder.setCurrentButton.setOnClickListener { onSetCurrentClick(position) }

    }

    override fun getItemCount(): Int {
        return recentLists.size
    }

}