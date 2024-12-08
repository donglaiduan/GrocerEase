package com.cs407.grocerease.ui.list
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.grocerease.R

class CurrentListRecycleView (
    private val groceryItems: MutableList<GroceryItem>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<CurrentListRecycleView.GroceryItemsView>() {

    inner class GroceryItemsView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.ItemName)
        val deleteButton: ImageView = itemView.findViewById(R.id.DeleteItemButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryItemsView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grocery_list, parent, false)
        return GroceryItemsView(view)
    }

    override fun onBindViewHolder(holder: GroceryItemsView, position: Int) {
        val item = groceryItems[position]
        holder.itemName.text = "(${item.amount}) ${item.description} "
        holder.deleteButton.setOnClickListener {
            onDeleteClick(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = groceryItems.size

}