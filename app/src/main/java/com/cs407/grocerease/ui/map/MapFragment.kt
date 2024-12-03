package com.cs407.grocerease.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cs407.grocerease.R
import com.cs407.grocerease.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync{ map ->
            val madisonCamPosition = (LatLng(43.0722, -89.4008))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(madisonCamPosition,14.5f))

            //Fresh Market
            setLocationMarker(map,"Fresh Market",
                "Address: 703 University Ave, Madison, WI 53715\n" +
                        "$$\n" +
                        "Located near the center of Madison, this grocery store is " +
                        "very convenient for most students living in the south east " +
                        "area of campus.",
                LatLng(43.07303410641694, -89.39772010890856))
            //Trader Joes

            //Capital Centre Market

            //Target

            //Walgreens

            //KwikTrip

            //




        }
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLocationMarker(googleMap: GoogleMap, destinationName: String,
                                  destinationDescription: String, destination: LatLng){
        googleMap.addMarker(
            MarkerOptions()
                .title(destinationName)
                .snippet(destinationDescription)
                .position(destination)
        )
    }
}