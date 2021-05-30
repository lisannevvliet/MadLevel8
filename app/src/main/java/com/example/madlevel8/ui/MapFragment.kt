package com.example.madlevel8.ui

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.madlevel8.R
import com.example.madlevel8.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment.
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the SupportMapFragment.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        // Get notified when the map is ready to be used.
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker in New York and move the camera.
        /*val myPlace = LatLng(40.73, -73.99)
        map.addMarker(MarkerOptions().position(myPlace).title("My Favorite City"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 12.0f))*/

        // Enable the zoom controls on the map.
        map.uiSettings.isZoomControlsEnabled = true

        // Declare this fragment as the callback triggered when the user clicks a marker on this map.
        map.setOnMarkerClickListener(this)

        // Request the location permission.
        setUpMap()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    // Check if the app has been granted the ACCESS_FINE_LOCATION permission. If it has not, then request it from the user.
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // Enable the my-location layer which draws a light blue dot on the user’s location and adds a button to the map that, when tapped, centers the map on the user’s location.
        map.isMyLocationEnabled = true

        // Retrieve the last known location.
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                lastLocation = it
                val currentLatLng = LatLng(it.latitude, it.longitude)
                // Move the camera to the last known location.
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    override fun onMarkerClick(p0: Marker?) = false

    // Release the view if the fragment is destroyed to prevent a memory leak.
    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}