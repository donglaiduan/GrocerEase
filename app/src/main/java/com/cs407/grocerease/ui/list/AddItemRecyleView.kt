package com.cs407.grocerease.ui.list
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cs407.grocerease.R

class AddItemRecyleView (
    private val groceryItems: MutableList<GroceryItem>,
) : RecyclerView.Adapter<AddItemRecyleView.GroceryItemsView>() {

    private var selectedPosition = -1
    inner class GroceryItemsView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemDescription: TextView = itemView.findViewById(R.id.item_description)
        val infoButton: Button = itemView.findViewById(R.id.display_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryItemsView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_list, parent, false)
        return GroceryItemsView(view)
    }

    override fun onBindViewHolder(holder: GroceryItemsView, position: Int) {
        val item = groceryItems[position]
        holder.itemDescription.text = item.description
        holder.itemView.setBackgroundColor(if(holder.adapterPosition == selectedPosition) Color.WHITE else Color.TRANSPARENT)
        holder.itemView.findViewById<Button>(R.id.display_info).visibility = (if(holder.adapterPosition == selectedPosition) View.VISIBLE else View.GONE)
        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
            holder.infoButton.setOnClickListener{
                //TODO bring up popup with nutrition Info
                Log.d("ItemSelected",item.toString())
            }
            onSelectItem(item) // Trigger the click event
        }
    }

    private fun onSelectItem(item: GroceryItem){
        Log.d("ItemSelected", item.description)
    }


    override fun getItemCount(): Int = groceryItems.size

}