package com.cs407.grocerease.ui.list
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.grocerease.R

class AddItemRecyleView (
    private val groceryItems: MutableList<GroceryItem>,
) : RecyclerView.Adapter<AddItemRecyleView.GroceryItemsView>() {

    inner class GroceryItemsView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.ItemName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryItemsView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_list, parent, false)
        return GroceryItemsView(view)
    }

    override fun onBindViewHolder(holder: GroceryItemsView, position: Int) {
        val item = groceryItems[position]
        holder.itemName.text = item.description
    }

    override fun getItemCount(): Int = groceryItems.size

}