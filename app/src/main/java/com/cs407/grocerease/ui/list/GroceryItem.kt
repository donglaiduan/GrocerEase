package com.cs407.grocerease.ui.list

data class GroceryItem(
    var name: String,
    var description: String,

    var calories: Double,
    var caloriesUnit: String,

    var carbs: Double,
    var carbsUnit: String,

    var fat: Double,
    var fatUnit: String,

    var protein: Double,
    var proteinUnit: String,

    var fiber: Double,
    var fiberUnit: String,

    var potassium: Double,
    var potassiumUnit: String,

    var calcium: Double,
    var calciumUnit: String,

    var iron: Double,
    var ironUnit: String,

    var folate: Double,
    var folateUnit: String,

    var vitaminD: Double,
    var vitaminDUnit: String,

    var amount: Double,
)