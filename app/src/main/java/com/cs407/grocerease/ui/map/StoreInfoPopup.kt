package com.cs407.grocerease.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cs407.grocerease.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.reflect.KDeclarationContainer

class StoreInfoPopup : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "StoreInfoPopup"
        private const val ARG_TITLE = "title"
        private const val ARG_INFO = "info"

        fun  newInstance(title: String, info: String): StoreInfoPopup {
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_INFO, info)
            val fragment = StoreInfoPopup()
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.store_popup, container, false)
        val titleText: TextView = view.findViewById(R.id.storeTitle)
        val infoText: TextView = view.findViewById(R.id.storeInfo)

        titleText.text = arguments?.getString(ARG_TITLE)
        infoText.text = arguments?.getString(ARG_INFO)

        return view
    }
}