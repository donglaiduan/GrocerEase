package com.cs407.grocerease.ui.list

import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.grocerease.databinding.FragmentListBinding
import java.time.temporal.TemporalAmount

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //groceryItems
    private val currentListItems = mutableListOf<GroceryItem>()
    private lateinit var currentListRecycleView: CurrentListRecycleView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        startListRecycle()

        binding.AddItemButton.setOnClickListener{
            addItem("Temp item")
        }

        return root
    }

    private fun startListRecycle() {
        currentListRecycleView = CurrentListRecycleView(currentListItems) { position -> deleteItem(position) }
        binding.GroceryListRecycleView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = currentListRecycleView
        }
    }

    private fun addItem(itemName: String){
        val testItemName = "$itemName ${currentListItems.size + 1}"
        currentListItems.add(GroceryItem(testItemName, 1))
        currentListRecycleView.notifyItemInserted(currentListItems.size - 1)
        Log.d("ListFragment", "List size: ${currentListItems.size}")
    }

    private fun deleteItem(position: Int){
        Log.d("position", "Position: ${position}")
        if (position >= 0 && position < currentListItems.size) {
            currentListItems.removeAt(position)
            currentListRecycleView.notifyItemRemoved(position)
        }
        Log.d("ListFragment", "List size: ${currentListItems.size}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}