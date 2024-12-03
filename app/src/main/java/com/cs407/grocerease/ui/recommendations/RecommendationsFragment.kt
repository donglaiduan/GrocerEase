package com.cs407.grocerease.ui.recommendations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cs407.grocerease.R
import com.cs407.grocerease.databinding.FragmentRecommendationsBinding

class RecommendationsFragment : Fragment() {

    private var _binding: FragmentRecommendationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationsBinding.inflate(inflater, container, false)

        // Handle button clicks
        binding.fnRecipesButton.setOnClickListener {
            // Navigate to RecipesFragment using NavController
            findNavController().navigate(R.id.recipesFragment)
        }

        binding.frNutritionButton.setOnClickListener {
            // Navigate to NutritionFragment using NavController
            findNavController().navigate(R.id.nutritionFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
