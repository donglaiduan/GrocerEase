    package com.cs407.grocerease.ui.list

    import android.content.Context
    import android.icu.text.Transliterator.Position
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.LinearLayout
    import android.widget.TextView
    import android.widget.Toast
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.ViewModelProvider
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.cs407.grocerease.R
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
            binding.AddItemButton.setOnClickListener {
                addItemPopUp()
            }

            //New list button
            binding.NewListButton.setOnClickListener {
                saveListToRecents()
                createNewList()
            }

            return root
        }

        //save the current list
        private fun saveList() {
            val sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)

            val listName = binding.GroceryListTitleText.text.toString()
            sharedPreferences.edit().putString(currentListNameShared, listName).apply()

            val combinedList = currentListItems.joinToString(";") { "${it.name},${it.amount}" }
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
                    val split = it.split(",")
                    if (split.size == 2) {
                        val itemName = split[0]
                        val itemAmount = split[1].toInt() ?: 1
                        currentListItems.add(GroceryItem(itemName, itemAmount))
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

            binding.GroceryListTitleText.setText("Grocery List")
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
                val split = recentList.split(":")

                if (split.size == 2) {
                    val listName = split[0]
                    val items = split[1].split(";")
                    val shownItems = items.take(3)
                    val itemTextString = StringBuilder()

                    shownItems.forEach { item ->
                        itemTextString.append("$item\n")
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

            val sharedPreferences = requireContext().getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
            val combinedList = sharedPreferences.getString(recentListsShared, "") ?: ""

            val currentName = binding.GroceryListTitleText.text.toString()
            val currentFullList = "$currentName:${currentListItems.joinToString(";") { "${it.name},${it.amount}" }}"

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
        private fun addItem(itemName: String, itemAmount: Int){
            currentListItems.add(GroceryItem(itemName, itemAmount))
            currentListRecycleView.notifyItemInserted(currentListItems.size - 1)
            saveList()
            Log.d("ListFragment", "List size: ${currentListItems.size}")
        }

        //delete item from current list
        private fun deleteItem(position: Int){
            Log.d("position", "Position: ${position}")
            if (position >= 0 && position < currentListItems.size) {
                currentListItems.removeAt(position)
                currentListRecycleView.notifyItemRemoved(position)
                saveList()
            }
            Log.d("ListFragment", "List size: ${currentListItems.size}")
        }

        private fun addItemPopUp(){

            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.adding_item_dialog, null)

            val itemAmountInput = dialogView.findViewById<android.widget.Spinner>(R.id.itemAmountSpinner)
            val adapter = android.widget.ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.item_amount_array)
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            itemAmountInput.adapter = adapter

            val dialog = android.app.AlertDialog.Builder(requireContext())
                .setTitle("Add Item")
                .setView(dialogView)
                .setPositiveButton("Add") {_, _ ->

                    val itemNameInput = dialogView.findViewById<android.widget.EditText>(R.id.itemNameInput)

                    val itemName = itemNameInput.text.toString().trim()
                    val itemAmount = itemAmountInput.selectedItem.toString().toInt()

                    if(itemName.isNotEmpty()) {
                        addItem(itemName, itemAmount)
                    } else {
                        Toast.makeText(context, "Item Name must be filled", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton(("Cancel"), null).create()

            dialog.show()
        }


        //when view is destroyed
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }


    }