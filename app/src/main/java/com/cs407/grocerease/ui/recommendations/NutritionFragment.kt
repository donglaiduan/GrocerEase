package com.cs407.grocerease.ui.recommendations

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cs407.grocerease.R
import com.cs407.grocerease.databinding.FragmentNutritionBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NutritionFragment : Fragment() {

    private var _binding: FragmentNutritionBinding? = null
    private val binding get() = _binding!!

    private val sharedPrefs = "GroceryListPrefs"
    private val currentListItemsShared = "CurrentListItems"

    private val userID = FirebaseAuth.getInstance().currentUser?.uid

    private var days = 0;
    private var weight = 0;
    private var gender = "";
    private var age = 0;
    private var height = 0;

    private var dailyCal = 2000.0;
    private var dailyCarb = 275.0;
    private var dailyFat = 78.0;
    private var dailyProtein = 50.0;
    private var dailyFiber = 28.0;
    private var dailyPotassium = 4700.0;
    private var dailyCalcium = 1300.0;
    private var dailyIron = 18.0;
    private var dailyFolate = 400.0;
    private var dailyVitaminD = 20.0;


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNutritionBinding.inflate(inflater, container, false)
        val db = FirebaseFirestore.getInstance()

        if (userID != null) {
            db.collection("users").document(userID).get()
                .addOnSuccessListener { document ->
                    days = (document.getLong("days")?.toInt() ?: 7)
                    weight = (document.getLong("weight")?.toInt() ?: 160)
                    gender = (document.getString("gender") ?: "other")
                    age = (document.getLong("age")?.toInt() ?: 20)
                    height = (document.getLong("height")?.toInt() ?: 70)

                    displaySharedPreferenceItems()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to fetch days or weight.", Toast.LENGTH_SHORT).show()
                }
        } else {
            displaySharedPreferenceItems()
        }

        binding.fnRecipesButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_recs)
        }

        displaySharedPreferenceItems()

        return binding.root
    }

    private fun calculateDailyNeeds(){
        val weightKG = weight * 0.453592;
        val heightCM = height * 2.54;

        if(gender == "Male") {
            dailyCal = (9.99 * weightKG) + (6.25 * heightCM) - (4.92 * age) + 5;
            Log.d("dailyCalCaluculations", dailyCal.toString())
            dailyCarb = (dailyCal * .5) / 4;
            dailyFat = (dailyCal * .35) / 9;
            dailyProtein = (dailyCal * .15) / 4;
            dailyFiber = 33.6;
            dailyPotassium = 4700.0;
            dailyCalcium = 1000.0;
            dailyIron = 8.0;
            dailyFolate = 400.0;
            dailyVitaminD = 12.5;
        } else if(gender == "female") {
            dailyCal = (9.99 * weightKG) + (6.25 * heightCM) - (4.92 * age) - 161;
            dailyCarb = (dailyCal * .5) / 4;
            dailyFat = (dailyFat * .35) / 9;
            dailyProtein = (dailyCal * .15) / 4;
            dailyFiber = 28.0;
            dailyPotassium = 4700.0;
            dailyCalcium = 1000.0;
            dailyIron = 18.0;
            dailyFolate = 400.0;
            dailyVitaminD = 12.5;
        } else {
            dailyCal = (9.99 * weightKG) + (6.25 * heightCM) - (4.92 * age) - 83;
            dailyCarb = (dailyCal * .5) / 4;
            dailyFat = (dailyFat * .35) / 9;
            dailyProtein = (dailyCal * .15) / 4;
            dailyFiber = 30.8;
            dailyPotassium = 4700.0;
            dailyCalcium = 1000.0;
            dailyIron = 13.0;
            dailyFolate = 400.0;
            dailyVitaminD = 12.5;
        }
    }

    private fun displaySharedPreferenceItems() {
        val sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        val combinedList = sharedPreferences.getString(currentListItemsShared, null)

        calculateDailyNeeds()

        if (!combinedList.isNullOrEmpty()) {
            val items = combinedList.split(";").filter { it.isNotEmpty() }

            var itemCalories = 0.0;
            var itemCarbs = 0.0;
            var itemFat = 0.0;
            var itemProtein = 0.0;
            var itemFiber = 0.0;
            var itemPotassium = 0.0;
            var itemCalcium = 0.0;
            var itemIron = 0.0;
            var itemFolate = 0.0;
            var itemVitaminD = 0.0;

            for (item in items) {
                val split = item.split(":")
                if (split.size == 23) {
                    itemCalories += (split[2].toDouble() * split[22].toDouble())
                    itemCarbs += (split[4].toDouble() * split[22].toDouble())
                    itemFat += (split[6].toDouble() * split[22].toDouble())
                    itemProtein += (split[8].toDouble() * split[22].toDouble())
                    itemFiber += (split[10].toDouble() * split[22].toDouble())
                    itemPotassium += (split[12].toDouble() * split[22].toDouble())
                    itemCalcium += (split[14].toDouble() * split[22].toDouble())
                    itemIron += (split[16].toDouble() * split[22].toDouble())
                    itemFolate += (split[18].toDouble() * split[22].toDouble())
                    itemVitaminD += (split[20].toDouble() * split[22].toDouble())
                }
            }

            val caloriesPercentage = calculatePercentage(itemCalories, days * dailyCal)
            binding.caloriesValueText.text = "%.2f kcal".format(itemCalories)
            setProgressBar(binding.caloriesProgressBar, caloriesPercentage)
            binding.caloriesPercentText.text = "${caloriesPercentage}%"

            val carbsPercentage = calculatePercentage(itemCarbs, days * dailyCarb)
            binding.carbsValueText.text = "%.2f g".format(itemCarbs)
            setProgressBar(binding.carbsProgressBar, carbsPercentage)
            binding.carbsPercentText.text = "${carbsPercentage}%"

            val fatPercentage = calculatePercentage(itemFat, days * dailyFat)
            binding.fatValueText.text = "%.2f g".format(itemFat)
            setProgressBar(binding.fatProgressBar, fatPercentage)
            binding.fatPercentText.text = "${fatPercentage}%"

            val proteinPercentage = calculatePercentage(itemProtein, days * dailyProtein)
            binding.proteinValueText.text = "%.2f g".format(itemProtein)
            setProgressBar(binding.proteinProgressBar, proteinPercentage)
            binding.proteinPercentText.text = "${proteinPercentage}%"

            val fiberPercentage = calculatePercentage(itemFiber, days * dailyFiber)
            binding.fiberValueText.text = "%.2f g".format(itemFiber)
            setProgressBar(binding.fiberProgressBar, fiberPercentage)
            binding.fiberPercentText.text = "${fiberPercentage}%"

            val potassiumPercentage = calculatePercentage(itemPotassium, days * dailyPotassium)
            binding.potassiumValueText.text = "%.2f mg".format(itemPotassium)
            setProgressBar(binding.potassiumProgressBar, potassiumPercentage)
            binding.potassiumPercentText.text = "${potassiumPercentage}%"

            val calciumPercentage = calculatePercentage(itemCalcium, days * dailyCalcium)
            binding.calciumValueText.text = "%.2f mg".format(itemCalcium)
            setProgressBar(binding.calciumProgressBar, calciumPercentage)
            binding.calciumPercentText.text = "${calciumPercentage}%"

            val ironPercentage = calculatePercentage(itemIron, days * dailyIron)
            binding.ironValueText.text = "%.2f mg".format(itemIron)
            setProgressBar(binding.ironProgressBar, ironPercentage)
            binding.ironPercentText.text = "${ironPercentage}%"

            val folatePercentage = calculatePercentage(itemFolate, days * dailyFolate)
            binding.folateValueText.text = "%.2f ug".format(itemFolate)
            setProgressBar(binding.folateProgressBar, folatePercentage)
            binding.folatePercentText.text = "${folatePercentage}%"

            val vitaminDPercentage = calculatePercentage(itemVitaminD, days * dailyVitaminD)
            binding.vitaminDValueText.text = "%.2f ug".format(itemVitaminD)
            setProgressBar(binding.vitaminDProgressBar, vitaminDPercentage)
            binding.vitaminDPercentText.text = "${vitaminDPercentage}%"

            binding.totalDaysText.text = days.toString()

        } else {
            Toast.makeText(context, "No items found in the list.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculatePercentage(value: Double, totalValue: Double): Int {
        return ((value / totalValue) * 100).toInt().coerceAtMost(100)
    }

    private fun setProgressBar(progressBar: ProgressBar, percentage: Int) {
        progressBar.max = 100
        progressBar.progress = percentage

        val color = when {
            percentage < 50 -> requireContext().getColor(R.color.red)
            percentage < 80 -> requireContext().getColor(R.color.yellow)
            else -> requireContext().getColor(R.color.green)
        }

        val drawable = progressBar.progressDrawable.mutate() as LayerDrawable

        val progressLayer = drawable.findDrawableByLayerId(android.R.id.progress)
        progressLayer?.setTint(color)

        progressBar.progressDrawable = drawable
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
