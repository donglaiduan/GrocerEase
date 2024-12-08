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

            //set map marker listener
            map.setOnMarkerClickListener { marker ->

                marker.showInfoWindow()

                val storeInfo = StoreInfoPopup.newInstance(
                    marker.title ?: "Store Name",
                    marker.snippet ?: "Store Info"
                )
                storeInfo.show(parentFragmentManager, StoreInfoPopup.TAG)
                true
            }

            //Fresh Market
            setLocationMarker(
                googleMap = map,
                destinationName = "Fresh Market",
                destinationDescription = "Address: 703 University Ave, Madison, WI 53715\n" +
                        "$$$\n" +
                        "Located near the center of Madison, this grocery store is " +
                        "very convenient for most students. Fresh has a large selection " +
                        "of high quality and organic items which increases its pricing. " +
                        "Fresh also supplies fresh meat and ready made meals",
                destination = LatLng(43.07303410641694, -89.39772010890856))
            //Trader Joes
            setLocationMarker(
                googleMap = map,
                destinationName = "Trader Joe's",
                destinationDescription = "Address: 1810 Monroe St, Madison, WI 53711\n" +
                        "$$\n" +
                        "Located southwest of campus, Trader Joe's is known for its " +
                        "wide variety of affordable ingredients, snacks, and ready-made " +
                        "meals.",
                destination = LatLng(43.06530796820925, -89.41766107417776))

            //Capital Centre Market
            setLocationMarker(
                googleMap = map,
                destinationName = "Capital Centre Market",
                destinationDescription = "Address: 111 N Broom St, Madison, WI 53703\n" +
                        "$$\n" +
                        "Located near the capital, Capital Centre Market is a small " +
                        "family-run grocery store with offering produce, meat, and a deli " +
                        "at an affordable price.",
                destination = LatLng(43.07277509306866, -89.39002591639587))

            //Target
            setLocationMarker(
                googleMap = map,
                destinationName = "Target",
                destinationDescription = "Address: 610 State St, Madison, WI 53703\n" +
                        "$$\n" +
                        "Located on state street, Target has a large selection of instant meals " +
                        "and snacks at an affordable price. Target also has a small selection of " +
                        "ingredients and frozen foods along with household goods and electronics.",
                destination = LatLng(43.07518772160185, -89.39607681824377))

            //Walgreens 1
            setLocationMarker(
                googleMap = map,
                destinationName = "Walgreens",
                destinationDescription = "Address: 311 E Campus Mall, Madison, WI 53715\n" +
                        "$$\n" +
                        "Located in the middle of campus, Walgreen has many quick-grab " +
                        "groceries with a focus on snacks and instant foods. Walgreens is" +
                        "open until midnight and also provides a pharmacy.",
                destination = LatLng(43.07245969228958, -89.39856426057304))

            //Walgreens 2
            setLocationMarker(
                googleMap = map,
                destinationName = "Walgreens",
                destinationDescription = "Address: 676 State St, Madison, WI 53703\n" +
                        "$$\n" +
                        "Located at the beginning of state street, Walgreen has many quick-grab " +
                        "groceries with a focus on snacks and instant foods. Walgreens is" +
                        "open until midnight and also provides a pharmacy.",
                destination = LatLng(43.07517782906013, -89.3971707891812))

            //KwikTrip
            setLocationMarker(
                googleMap = map,
                destinationName = "Kwik Trip",
                destinationDescription = "Address: 1421 Monroe St, Madison, WI 53711\n" +
                        "$\n" +
                        "Located southwest of campus, Kwik Trip is a convenience store " +
                        "that not only provides snacks, but fresh food including sandwiches, " +
                        "salads, and bakery items along with some groceries.",
                destination = LatLng(43.069961781760966, -89.40929217221888))

            //Asian Midway Foods
            setLocationMarker(
                googleMap = map,
                destinationName = "Asian Midway Foods",
                destinationDescription = "Address: 301 S Park St, Madison, WI 53715\n" +
                        "$$\n" +
                        "Located south of campus, Asian Midway Foods has a focus on east " +
                        "Asian cuisine and has traditional ingredients, fresh produce, and " +
                        "authentic snacks.",
                destination = LatLng(43.064092429098295, -89.4003252587596))

            //J&P Fresh Market
            setLocationMarker(
                googleMap = map,
                destinationName = "J&P Fresh Market",
                destinationDescription = "Address: 346 State St, Madison, WI 53703\n" +
                        "$$\n" +
                        "Located at the end of state street, J&P is a small family-owned " +
                        "market with high-quality produce and meats. J&P has a strong focus" +
                        "on east items and snacks.",
                destination = LatLng(43.0751807110881, -89.39133827758543))

            //Pinkus Market
            setLocationMarker(
                googleMap = map,
                destinationName = "Pinkus Market",
                destinationDescription = "Address: 673 State St, Madison, WI 53703\n" +
                        "$$\n" +
                        "Located on state street, Pinkus Market is a small convenience store " +
                        "with a collection of essential groceries making it a quick and easy " +
                        "stop when in a rush.",
                destination = LatLng(43.07483094992227, -89.39718234164462))

            //Regent Mart
            setLocationMarker(
                googleMap = map,
                destinationName = "Regent Mart & Cafe",
                destinationDescription = "Address: 1401 Regent St, Madison, WI 53711\n" +
                        "$$\n" +
                        "Located south of campus, Regent Mart is a convenience store with" +
                        "a decent collection of groceries. Regent Mart has a strong selection of " +
                        "Indian goods and snacks along with a hot deli.",
                destination = LatLng(43.06750437858409, -89.40929200194383))

            //Pick 'n Save
            setLocationMarker(
                googleMap = map,
                destinationName = "Pick 'n Save",
                destinationDescription = "Address: 1312 S Park St, Madison, WI 53715\n" +
                        "$\n" +
                        "Located south of campus, Pick 'n Save is a full suparmarket with a " +
                        "wide variety of groceries and household items. Although a bit far, it " +
                        "has affordable prices and weakly deals.",
                destination = LatLng(43.05247737233041, -89.39591557577499))



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