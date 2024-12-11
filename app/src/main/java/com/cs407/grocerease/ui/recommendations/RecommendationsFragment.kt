package com.cs407.grocerease.ui.recommendations

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.allViews
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cs407.grocerease.R
import com.cs407.grocerease.databinding.FragmentRecommendationsBinding

class RecommendationsFragment : Fragment() {

    private var _binding: FragmentRecommendationsBinding? = null
    private val binding get() = _binding!!

    // SharedPreferences keys
    private val sharedPrefs = "GroceryListPrefs"
    private val currentListItemsShared = "CurrentListItems"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationsBinding.inflate(inflater, container, false)

        // Load and display recommendations
        loadRecommendations()

        // Load Saved Favorite Recipes
        loadRecipesFromFavoritesSharedPreferences()

        // Handle button clicks
        binding.frRecipesButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_recs)
        }

        binding.frNutritionButton.setOnClickListener {
            findNavController().navigate(R.id.nutritionFragment)
        }

        return binding.root
    }

    private fun loadRecommendations() {
        val sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        val combinedList = sharedPreferences.getString(currentListItemsShared, null)

        // Target the LinearLayout inside the ScrollView
        val recipesContainer = binding.root.findViewById<LinearLayout>(R.id.recipesContainer)
        val favoritesContainer = binding.root.findViewById<LinearLayout>(R.id.favoritesContainer)
        recipesContainer.removeAllViews()

        // Example recipe data with ingredients
        val recipes = mapOf(
            "Spaghetti Carbonara" to listOf("pasta", "egg", "cheese"),
            "Chicken Alfredo" to listOf("chicken", "alfredo", "pasta"),
            "Vegetable Stir-Fry" to listOf("bell peppers", "broccoli", "soy sauce"),
            "Spaghetti Egg" to listOf("pasta", "egg", "keese"),
            "Egg Carbonara" to listOf("egg", "egg", "cheese"),
            "Egg egg" to listOf("pasta", "egg", "egg")
        )

        if (!combinedList.isNullOrEmpty()) {
            val items = combinedList.split(";").filter { it.isNotEmpty() }
            val itemNames = items.map { it.split(":")[0].lowercase() } // Extract item names from sharedPreferences
            var index = 0

            for ((recipeName, recipeIngredients) in recipes) {
                // Check if any ingredient matches the item names from SharedPreferences
                if (recipeIngredients.any { ingredient -> itemNames.contains(ingredient.lowercase()) }) {
                    // Create and add a view for each matching recipe
                    val recipeView = LayoutInflater.from(context)
                        .inflate(R.layout.recommendation_item_view, recipesContainer, false)

                    val titleTextView = recipeView.findViewById<TextView>(R.id.recommendationTitle)
                    val descriptionTextView = recipeView.findViewById<TextView>(R.id.recommendationDescription)
                    val addToFavoritesButton = recipeView.findViewById<Button>(R.id.addToFavoritesButton)

                    // Set the text for the recipe
                    titleTextView.text = recipeName
                    descriptionTextView.text = "Ingredients: ${recipeIngredients.joinToString(", ")}"

                    val backgroundColor = if (index % 2 == 0) {
                        resources.getColor(android.R.color.holo_blue_light, null) // Dark gray for even index
                    } else {
                        resources.getColor(android.R.color.holo_purple, null) // White for odd index
                    }
                    recipeView.setBackgroundColor(backgroundColor)

                    // Handle adding to favorites
                    addToFavoritesButton.setOnClickListener {
                        val recipes = sharedPreferences.getString("currentFavoriteRecipes", "")
                        if(!recipes.isNullOrEmpty() && recipes.contains("${titleTextView.text};${descriptionTextView.text}")){
                            //Log.d("FavoriteRecipe", "Duplicate Found")
                            Toast.makeText(requireContext(), "Error, Duplicate Found", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            val favoriteView = LayoutInflater.from(context)
                                .inflate(R.layout.recommendation_item_view, favoritesContainer, false)

                            val favoriteTitleTextView =
                                favoriteView.findViewById<TextView>(R.id.recommendationTitle)
                            val favoriteDescriptionTextView =
                                favoriteView.findViewById<TextView>(R.id.recommendationDescription)
                            val removeButton = favoriteView.findViewById<Button>(R.id.addToFavoritesButton)

                            favoriteTitleTextView.text = recipeName
                            favoriteDescriptionTextView.text =
                                "Ingredients: ${recipeIngredients.joinToString(", ")}"

                            removeButton.setBackgroundResource(R.drawable.ic_delete_symbol)
                            // Handle removing from favorites
                            removeButton.setOnClickListener {
                                favoritesContainer.removeView(favoriteView)
                                saveRecipeToFavoritesSharedPreferences()
                            }

                            favoritesContainer.addView(favoriteView)
                            saveRecipeToFavoritesSharedPreferences()
                        }
                    }

                    // Add the recipe block to the container
                    recipesContainer.addView(recipeView)
                    index++
                }
            }
        } else {
            // Show a message if no data is available
            val noDataMessage = TextView(requireContext()).apply {
                text = "No recommendations available. Add items to your grocery list."
                textSize = 16f
                setPadding(16, 16, 16, 16)
            }
            recipesContainer.addView(noDataMessage)
        }
    }

    private fun saveRecipeToFavoritesSharedPreferences(){
        val sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        val favoritesContainer = binding.root.findViewById<LinearLayout>(R.id.favoritesContainer)
        var currentFavorites = ""
        favoritesContainer.forEach { view->
                val favoriteTitleTextView =
                    view.findViewById<TextView>(R.id.recommendationTitle).text.toString()
                val favoriteDescriptionTextView =
                    view.findViewById<TextView>(R.id.recommendationDescription).text.toString()
                currentFavorites = currentFavorites.plus("$favoriteTitleTextView;$favoriteDescriptionTextView|")
        }
        sharedPreferences.edit().putString("currentFavoriteRecipes", currentFavorites).apply()
    }

    private fun loadRecipesFromFavoritesSharedPreferences(){
        val sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        val savedFavoriteRecipes = sharedPreferences.getString("currentFavoriteRecipes","")
        val favoritesContainer = binding.root.findViewById<LinearLayout>(R.id.favoritesContainer)
        savedFavoriteRecipes!!.split("|").forEach() { recipe ->
            if(recipe.isEmpty()){
                Log.d("SavedRecipes", "Error Blank Recipe")
            }
            else {
                val recipeInfo = recipe.split(";")
                val recipeName = recipeInfo[0]
                val recipeIngredients = recipeInfo[1]

                val favoriteView = LayoutInflater.from(context)
                    .inflate(R.layout.recommendation_item_view, favoritesContainer, false)

                val favoriteTitleTextView =
                    favoriteView.findViewById<TextView>(R.id.recommendationTitle)
                val favoriteDescriptionTextView =
                    favoriteView.findViewById<TextView>(R.id.recommendationDescription)
                val removeButton = favoriteView.findViewById<Button>(R.id.addToFavoritesButton)

                favoriteTitleTextView.text = recipeName
                favoriteDescriptionTextView.text = recipeIngredients
                removeButton.setBackgroundResource(R.drawable.ic_delete_symbol)

                // Handle removing from favorites
                removeButton.setOnClickListener {
                    favoritesContainer.removeView(favoriteView)
                    saveRecipeToFavoritesSharedPreferences()
                }

                favoritesContainer.addView(favoriteView)
            }
        }
    }

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class Recipe(val name: String, val ingredients: List<String>, val instructions: String)
}
