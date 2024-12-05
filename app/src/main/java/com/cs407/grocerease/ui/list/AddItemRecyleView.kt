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
import kotlinx.coroutines.selects.select

class AddItemRecycleView (
    val groceryItems: MutableList<GroceryItem>,
) : RecyclerView.Adapter<AddItemRecycleView.GroceryItemsView>() {

    private var selectedPosition = -1

    inner class GroceryItemsView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemDescription: TextView = itemView.findViewById(R.id.item_description)
        val infoButton: Button = itemView.findViewById(R.id.display_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryItemsView {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_list, parent, false)
        return GroceryItemsView(view)
    }

    override fun onBindViewHolder(holder: GroceryItemsView, position: Int) {
        val item = groceryItems[position]
        val description = holder.itemDescription
        description.text = item.description
        holder.itemView.setBackgroundColor(if (holder.adapterPosition == selectedPosition) Color.WHITE else Color.TRANSPARENT)
        holder.itemView.findViewById<Button>(R.id.display_info).visibility =
            (if (holder.adapterPosition == selectedPosition) View.VISIBLE else View.GONE)
        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
            onSelectItem(item) // Trigger the click event
        }
        holder.infoButton.setOnClickListener {
            Log.d("InfoButton", item.toString())
            description.text =
                item.description + "\n" +
                        "Nutrient Values are per 100g\n" +
                        item.toString()

        }
    }

    private fun onSelectItem(item: GroceryItem) {
        //Log.d("ItemPosition", selectedPosition.toString())
        Log.d("ItemSelected", getSelectedItem().toString())
    }

    fun getSelectedItem(): GroceryItem? {
        if (selectedPosition == -1)
            return null
        else return groceryItems[selectedPosition]
    }


    override fun getItemCount(): Int = groceryItems.size
}