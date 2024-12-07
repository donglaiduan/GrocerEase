    package com.cs407.grocerease.ui.list

    import android.content.Context
    import android.content.pm.PackageManager
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.TextView
    import android.widget.Toast
    import androidx.fragment.app.Fragment
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.android.volley.Request
    import com.android.volley.toolbox.JsonObjectRequest
    import com.android.volley.toolbox.Volley
    import com.cs407.grocerease.R
    import com.cs407.grocerease.databinding.FragmentListBinding
    import org.json.JSONObject


    class ListFragment : Fragment() {

        private var _binding: FragmentListBinding? = null

        // This property is only valid between onCreateView and
        // onDestroyView.
        private val binding get() = _binding!!

        //groceryItems
        private val currentListItems = mutableListOf<GroceryItem>()
        private lateinit var currentListRecycleView: CurrentListRecycleView

        //Storing list name
        private val sharedPrefs = "GroceryListPrefs"
        private val currentListItemsShared = "CurrentListItems"
        private val currentListNameShared = "CurrentListName"
        private val recentListsShared = "RecentList"

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {

            _binding = FragmentListBinding.inflate(inflater, container, false)
            val root: View = binding.root

            startListRecycle()

            //load data from sharedPrefs
            loadCurrentList()
            loadRecentLists()

            //add item to current list
            binding.AddItemPopup.setOnClickListener {
                addItemPopUp()
            }

            //New list button
            binding.NewListButton.setOnClickListener {
                saveListToRecents()
                createNewList()
            }

            //View recent lists button
            binding.AllListButton.setOnClickListener {
                showAllRecentListsPopup()
            }

            return root
        }

        //save the current list
        private fun saveList() {
            val sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)

            val listName = binding.GroceryListTitleText.text.toString()
            sharedPreferences.edit().putString(currentListNameShared, listName).apply()

            val combinedList = currentListItems.joinToString(";") { "${it.name}:${it.description}:" +
                    "${it.calories}:${it.caloriesUnit}:" +
                    "${it.carbs}:${it.carbsUnit}:" +
                    "${it.fat}:${it.fatUnit}:" +
                    "${it.protein}:${it.proteinUnit}:" +
                    "${it.fiber}:${it.fiberUnit}:" +
                    "${it.potassium}:${it.potassiumUnit}:" +
                    "${it.calcium}:${it.calciumUnit}:" +
                    "${it.iron}:${it.ironUnit}:" +
                    "${it.folate}:${it.folateUnit}:" +
                    "${it.vitaminD}:${it.vitaminDUnit}:" +
                    "${it.amount}" }
            sharedPreferences.edit().putString(currentListItemsShared, combinedList).apply()
        }


        //load the current list
        private fun loadCurrentList(){
            val sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
            val savedName = sharedPreferences.getString(currentListNameShared, "Grocery list")
            binding.GroceryListTitleText.setText(savedName)

            //get current list items
            val combinedList = sharedPreferences.getString(currentListItemsShared, null)
            if(!combinedList.isNullOrEmpty())
            {
                val items = combinedList.split(";").filter { it.isNotEmpty() }
                currentListItems.clear()
                items.forEach{
                    val split = it.split(":")
                    if (split.size == 23) {
                        currentListItems.add(GroceryItem(split[0],split[1],split[2].toDouble(),
                            split[3],split[4].toDouble(),split[5],split[6].toDouble(),
                            split[7],split[8].toDouble(),split[9],split[10].toDouble(),
                            split[11],split[12].toDouble(),split[13],split[14].toDouble(),
                            split[15],split[16].toDouble(),split[17],split[18].toDouble(),
                            split[19],split[20].toDouble(),split[21],split[22].toDouble()))
                    } else {
                        Log.w("ListFragment", "loadCurrentList split error")
                    }
                }
            }
        }

        //create a new List
        private fun createNewList() {
            currentListItems.clear()
            currentListRecycleView.notifyDataSetChanged()

            binding.GroceryListTitleText.setText(getString(R.string.grocery_list))
            binding.RecentListLayout.visibility = View.VISIBLE

            saveList()
        }

        //load recent lists
        private fun loadRecentLists() {
            val sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
            val combinedList = sharedPreferences.getString(recentListsShared, null)
            if(!combinedList.isNullOrEmpty())
            {
                val recentLists = combinedList.split("|").filter { it.isNotEmpty() }
                showRecentLists(recentLists)
            }
        }

        //Show Recent lists
        private fun showRecentLists(recentLists: List<String>) {
            val recentScroll = binding.RecentListLinearLayout
            recentScroll.removeAllViews()
            binding.RecentListLayout.visibility = View.VISIBLE

            for(recentList in recentLists) {
                val split = recentList.split("*")

                if (split.size >= 2) {
                    val listName = split[0]
                    val items = split[1].split(";")
                    val shownItems = items.take(3)
                    val itemTextString = StringBuilder()

                    shownItems.forEach { item ->
                        val itemName = item.split(":")
                        itemTextString.append("${itemName[1]}\n")
                    }

                    if (items.size > 3) {
                        itemTextString.append("...")
                    }

                    val itemText = itemTextString.toString().trim()

                    val recentListInfo = LayoutInflater.from(context).inflate(R.layout.recent_lists_info, recentScroll, false)

                    val recentTitleText = recentListInfo.findViewById<TextView>(R.id.recentListName)
                    val recentItemsText = recentListInfo.findViewById<TextView>(R.id.recentListItems)

                    recentTitleText.text = listName
                    recentItemsText.text = itemText

                    recentScroll.addView(recentListInfo)

                } else {
                    Log.w("ListFragment", "showRecentList split size")
                }
            }
        }

        //save currentList to recents, up to 5
        private fun saveListToRecents() {
            if(currentListItems.isEmpty())
            {
                Toast.makeText(context, "List is empty", Toast.LENGTH_SHORT).show()
                return
            }
            if(binding.GroceryListTitleText.text.toString().isEmpty())
            {
                Toast.makeText(context, "Please Enter A Name for your List", Toast.LENGTH_SHORT).show()
                return
            }

            val sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
            val combinedList = sharedPreferences.getString(recentListsShared, "") ?: ""

            val currentName = binding.GroceryListTitleText.text.toString()
            val currentFullList = "$currentName*${currentListItems.joinToString(";") { "${it.name}:${it.description}:" +
                    "${it.calories}:${it.caloriesUnit}:" +
                    "${it.carbs}:${it.carbsUnit}:" +
                    "${it.fat}:${it.fatUnit}:" +
                    "${it.protein}:${it.proteinUnit}:" +
                    "${it.fiber}:${it.fiberUnit}:" +
                    "${it.potassium}:${it.potassiumUnit}:" +
                    "${it.calcium}:${it.calciumUnit}:" +
                    "${it.iron}:${it.ironUnit}:" +
                    "${it.folate}:${it.folateUnit}:" +
                    "${it.vitaminD}:${it.vitaminDUnit}:" +
                    "${it.amount}" }}"

            val recentLists = combinedList.split("|").toMutableList()

            if (recentLists.contains(currentFullList)) {
                Toast.makeText(context, "List already exists in recent lists", Toast.LENGTH_SHORT).show()
                return
            }

            recentLists.add(0, currentFullList)

            if(recentLists.size > 5) {
                recentLists.removeAt(5)
            }

            val updatedRecentLists = recentLists.joinToString("|")
            sharedPreferences.edit().putString(recentListsShared, updatedRecentLists).apply()
            showRecentLists(recentLists)
        }

        //start recycle view
        private fun startListRecycle() {
            currentListRecycleView = CurrentListRecycleView(currentListItems) { position -> deleteItem(position) }
            binding.GroceryListRecycleView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = currentListRecycleView
            }
        }

        //add item to current list
        private fun addItem(item: GroceryItem, itemAmountInput: Double){
            item.amount = itemAmountInput
            currentListItems.add(item)
            currentListRecycleView.notifyItemInserted(currentListItems.size - 1)
            saveList()
            Log.d("ListFragment", "List size: ${currentListItems.size}")
        }

        //delete item from current list
        private fun deleteItem(position: Int){
            Log.d("position", "Position: $position")
            if (position >= 0 && position < currentListItems.size) {
                currentListItems.removeAt(position)
                currentListRecycleView.notifyItemRemoved(position)
                saveList()
            }
            Log.d("ListFragment", "List size: ${currentListItems.size}")
        }

        private fun addItemPopUp(){

            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.adding_item_dialog, null)

            val addListAdapter = AddItemRecycleView(mutableListOf())
            val addListRecycleView = dialogView.findViewById<RecyclerView>(R.id.addItemRecyclerView)
            addListRecycleView.layoutManager = LinearLayoutManager(requireContext())
            addListRecycleView.adapter = addListAdapter

            val searchButton = dialogView.findViewById<Button>(R.id.search_button)
            searchButton.setOnClickListener {

                val itemNameInput = dialogView.findViewById<android.widget.EditText>(R.id.itemNameInput)
                val itemName = itemNameInput.text.toString().trim()

                // use Nutrition Database to search for Item. If Item found, display results, and allow user to select
                // result from search. then allow user to look at nutrition information, and select a result, and add some
                // amount of that food to the list.
                if(itemName.isNotEmpty()) {
                    loadSearch(itemName, object : SearchCallback {
                        override fun onResultsLoaded(results: MutableList<GroceryItem>) {
                            // Use the results here
                            if(results.isEmpty())
                                Toast.makeText(context, "Food Not Found", Toast.LENGTH_SHORT).show()
                            else {
                                addListRecycleView.adapter = AddItemRecycleView(results)
                                Log.d("Results", results.toString())
                            }
                        }
                    })

                } else {
                    Toast.makeText(context, "Please Enter Food Name", Toast.LENGTH_SHORT).show()
                }
            }

            val itemAmountInput = dialogView.findViewById<TextView>(R.id.itemAmount)

            val addButton = dialogView.findViewById<Button>(R.id.addItemButton)
            addButton.setOnClickListener {
                val selected = (addListRecycleView.adapter as AddItemRecycleView).getSelectedItem()
                val amount = itemAmountInput.text.toString().toDoubleOrNull()
                Log.d("Item", selected.toString())
                if(selected != null && amount != null && !amount.isNaN()){
                    addItem(selected,amount)
                    Toast.makeText(context, "Item Added!", Toast.LENGTH_SHORT).show()
                    addListAdapter.notifyDataSetChanged()
                }
                if (selected == null)
                    Toast.makeText(context, "Error, No Item Selected", Toast.LENGTH_SHORT).show()
                if (amount == null || amount.isNaN())
                    Toast.makeText(context, "Error, Amont is Not a Number", Toast.LENGTH_SHORT).show()

            }
            val dialog = android.app.AlertDialog.Builder(requireContext())
                .setTitle("Add Item")
                .setView(dialogView)
                .setNegativeButton(("Done"), null)
                .create()
            dialog.show()
        }

        interface SearchCallback {
            fun onResultsLoaded(results: MutableList<GroceryItem>)
        }

        private fun loadSearch(searchItem: String, callback: SearchCallback) {
            val volleyQueue = Volley.newRequestQueue(context)
            val app = requireContext().packageManager.getApplicationInfo(
                requireContext().packageName, PackageManager.GET_META_DATA
            )
            val bundle = app.metaData
            val apiKey = bundle.getString("com.cs407.grocerease.NUTRITION_API_KEY")
            val url = "https://api.nal.usda.gov/fdc/v1/foods/search?query=$searchItem&dataType=Survey (FNDDS)&pageSize=100&pageNumber=1&sortBy=dataType.keyword&sortOrder=asc&api_key=$apiKey"
            val results = mutableListOf<GroceryItem>()

            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    val jsonObject = JSONObject(response.toString())
                    val foods = jsonObject.getJSONArray("foods")
                    for (i in 0 until foods.length()) {
                        val resultGroceryItem = GroceryItem(
                            searchItem, "", 0.0, "", 0.0, "", 0.0,
                            "", 0.0, "", 0.0, "", 0.0, "", 0.0,
                            "", 0.0, "", 0.0, "", 0.0, "", 0.0
                        )
                        val food = foods.getJSONObject(i)
                        resultGroceryItem.description = food.getString("description")
                        val nutrients = food.getJSONArray("foodNutrients")
                        for (j in 0 until nutrients.length()) {
                            val nutrient = nutrients.getJSONObject(j)
                            when {
                                nutrient.getString("nutrientName") == "Energy" && nutrient.getInt("nutrientId") == 1008 -> {
                                    resultGroceryItem.calories = nutrient.getDouble("value")
                                    resultGroceryItem.caloriesUnit = nutrient.getString("unitName")
                                }
                                nutrient.getString("nutrientName") == "Carbohydrate, by difference" && nutrient.getInt("nutrientId") == 1005 -> {
                                    resultGroceryItem.carbs = nutrient.getDouble("value")
                                    resultGroceryItem.carbsUnit = nutrient.getString("unitName")
                                }
                                nutrient.getString("nutrientName") == "Total lipid (fat)" && nutrient.getInt("nutrientId") == 1004 -> {
                                    resultGroceryItem.fat = nutrient.getDouble("value")
                                    resultGroceryItem.fatUnit = nutrient.getString("unitName")
                                }
                                nutrient.getString("nutrientName") == "Protein" && nutrient.getInt("nutrientId") == 1003 -> {
                                    resultGroceryItem.protein = nutrient.getDouble("value")
                                    resultGroceryItem.proteinUnit = nutrient.getString("unitName")
                                }
                                nutrient.getString("nutrientName") == "Fiber, total dietary" && nutrient.getInt("nutrientId") == 1079 -> {
                                    resultGroceryItem.fiber = nutrient.getDouble("value")
                                    resultGroceryItem.fiberUnit = nutrient.getString("unitName")
                                }
                                nutrient.getString("nutrientName") == "Potassium, K" && nutrient.getInt("nutrientId") == 1092 -> {
                                    resultGroceryItem.potassium = nutrient.getDouble("value")
                                    resultGroceryItem.potassiumUnit = nutrient.getString("unitName")
                                }
                                nutrient.getString("nutrientName") == "Calcium, Ca" && nutrient.getInt("nutrientId") == 1087 -> {
                                    resultGroceryItem.calcium = nutrient.getDouble("value")
                                    resultGroceryItem.calciumUnit = nutrient.getString("unitName")
                                }
                                nutrient.getString("nutrientName") == "Iron, Fe" && nutrient.getInt("nutrientId") == 1089 -> {
                                    resultGroceryItem.iron = nutrient.getDouble("value")
                                    resultGroceryItem.ironUnit = nutrient.getString("unitName")
                                }
                                nutrient.getString("nutrientName") == "Folate, total" && nutrient.getInt("nutrientId") == 1177 -> {
                                    resultGroceryItem.folate = nutrient.getDouble("value")
                                    resultGroceryItem.folateUnit = nutrient.getString("unitName")
                                }
                                nutrient.getString("nutrientName") == "Vitamin D (D2 + D3)" && nutrient.getInt("nutrientId") == 1114 -> {
                                    resultGroceryItem.vitaminD = nutrient.getDouble("value")
                                    resultGroceryItem.vitaminDUnit = nutrient.getString("unitName")
                                }
                            }
                        }
                        results.add(resultGroceryItem)
                    }
                    callback.onResultsLoaded(results)
                },
                { error ->
                    Toast.makeText(context, "Response: %s".format(error.toString()), Toast.LENGTH_SHORT).show()
                }
            )
            volleyQueue.add(jsonObjectRequest)
        }

        private fun showAllRecentListsPopup() {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.recent_lists_popup, null)

            val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recentListPopupsView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            val sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
            val combinedList = sharedPreferences.getString(recentListsShared, null) ?: ""
            val recentLists = combinedList.split("|").filter { it.isNotEmpty() }.toMutableList()

            val popup = RecentListsPopupView(
                recentLists,
                onDeleteClick = { position ->
                    recentLists.removeAt(position)
                    sharedPreferences.edit().putString(recentListsShared, recentLists.joinToString("|")).apply()
                    recyclerView.adapter?.notifyDataSetChanged()
                    showRecentLists(recentLists)
                    Toast.makeText(context, "List deleted", Toast.LENGTH_SHORT).show()
                },
                onSetCurrentClick = { position ->
                    val sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)

                    val selectedList = recentLists[position]
                    recentLists.removeAt(position)

                    val currentName = binding.GroceryListTitleText.text.toString()
                    if (currentName.isNotEmpty() && currentListItems.isNotEmpty()) {
                        val currentFullList = "$currentName*${currentListItems.joinToString(";") { "${it.name}:${it.description}:" +
                                "${it.calories}:${it.caloriesUnit}:" +
                                "${it.carbs}:${it.carbsUnit}:" +
                                "${it.fat}:${it.fatUnit}:" +
                                "${it.protein}:${it.proteinUnit}:" +
                                "${it.fiber}:${it.fiberUnit}:" +
                                "${it.potassium}:${it.potassiumUnit}:" +
                                "${it.calcium}:${it.calciumUnit}:" +
                                "${it.iron}:${it.ironUnit}:" +
                                "${it.folate}:${it.folateUnit}:" +
                                "${it.vitaminD}:${it.vitaminDUnit}:" +
                                "${it.amount}" }}"
                        recentLists.add(0, currentFullList)
                    }

                    val splitList = selectedList.split("*")
                    val listName = splitList[0]
                    val items = splitList[1].split(";").filter { it.isNotEmpty() }

                    binding.GroceryListTitleText.setText(listName)
                    currentListItems.clear()
                    items.forEach {
                        val itemInfo = it.split(":")
                        if (itemInfo.size == 23) {
                            currentListItems.add(GroceryItem(itemInfo[0],itemInfo[1],itemInfo[2].toDouble(),
                                itemInfo[3],itemInfo[4].toDouble(),itemInfo[5],itemInfo[6].toDouble(),
                                itemInfo[7],itemInfo[8].toDouble(),itemInfo[9],itemInfo[10].toDouble(),
                                itemInfo[11],itemInfo[12].toDouble(),itemInfo[13],itemInfo[14].toDouble(),
                                itemInfo[15],itemInfo[16].toDouble(),itemInfo[17],itemInfo[18].toDouble(),
                                itemInfo[19],itemInfo[20].toDouble(),itemInfo[21],itemInfo[22].toDouble()))                        }
                    }

                    currentListRecycleView.notifyDataSetChanged()
                    sharedPreferences.edit().putString(recentListsShared, recentLists.joinToString("|")).apply()
                    saveList()

                    recyclerView.adapter?.notifyDataSetChanged()
                    showRecentLists(recentLists)

                    Toast.makeText(context, "List set as current", Toast.LENGTH_SHORT).show()
                }
            )

            recyclerView.adapter = popup

            val dialog = android.app.AlertDialog.Builder(requireContext())
                .setTitle("All Lists")
                .setView(dialogView)
                .setNegativeButton("Close", null)
                .create()

            dialog.show()

        }


        //when view is destroyed
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }


    }