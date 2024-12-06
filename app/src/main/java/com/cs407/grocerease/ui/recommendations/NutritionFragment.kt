package com.cs407.grocerease.ui.recommendations

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cs407.grocerease.R
import com.cs407.grocerease.databinding.FragmentNutritionBinding

class NutritionFragment : Fragment() {

    private var _binding: FragmentNutritionBinding? = null
    private val binding get() = _binding!!

    private val sharedPrefs = "GroceryListPrefs"
    private val currentListItemsShared = "CurrentListItems"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNutritionBinding.inflate(inflater, container, false)

        // Handle button click to navigate to another fragment
        binding.fnRecipesButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_recs)
        }

        // Load and display shared preference items
        displaySharedPreferenceItems()

        return binding.root
    }

    private fun displaySharedPreferenceItems() {
        // Retrieve shared preferences
        val sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        val combinedList = sharedPreferences.getString(currentListItemsShared, null)

        // Clear the scrollable layout
        binding.nutritionScrollView.removeAllViews()

        if (!combinedList.isNullOrEmpty()) {
            val items = combinedList.split(";").filter { it.isNotEmpty() }

            for (item in items) {
                val split = item.split(":")
                if (split.size == 14) {
                    val itemCalories = split[2].toDouble()
                    val itemCarbs = split[3].toDouble()
                    val itemFat = split[4].toDouble()
                    val itemProtein = split[5].toDouble()
                    val itemFiber = split[6].toDouble()
                    val itemPotassium = split[7].toDouble()
                    val itemCalcium = split[8].toDouble()
                    val itemIron = split[9].toDouble()
                    val itemFolate = split[10].toDouble()
                    val itemVitaminD = split[11].toDouble()

                    // Inflate the single nutrition item layout
                    val itemView = LayoutInflater.from(context).inflate(R.layout.nutrition_item_view, binding.nutritionScrollView, false)
                    val nutritionTextView = itemView.findViewById<TextView>(R.id.item_nutrition)

                    // Set the nutrition data
                    nutritionTextView.text = "Calories: $itemCalories\n\n Carbs: $itemCarbs\n\n Fat: $itemFat\n\n Protein: $itemProtein\n\n" +
                            "Fiber: $itemFiber\n\n Potassium: $itemPotassium\n\n Calcium: $itemCalcium\n\n Iron: $itemIron\n\n" +
                            "Folate: $itemFolate\n\n VitaminD: $itemVitaminD"

                    // Add the nutrition item to the scrollable view
                    binding.nutritionScrollView.addView(itemView)
                }
            }
        } else {
            // Show a message if no items are found
            val emptyMessage = TextView(requireContext())
            emptyMessage.text = "No items found in the list."
            emptyMessage.textSize = 16f
            binding.nutritionScrollView.addView(emptyMessage)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
