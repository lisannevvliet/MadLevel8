package com.example.madlevel8.ui

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.madlevel8.R
import com.example.madlevel8.databinding.FragmentMapBinding
import com.example.madlevel8.vm.MarkerViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MarkerViewModel by viewModels()

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment.
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enable a different AppBar for this fragment.
        setHasOptionsMenu(true)

        // Obtain the SupportMapFragment.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        // Get notified when the map is ready to be used.
        mapFragment.getMapAsync(this)

        // Initiate the FusedLocationClient, to be able to retrieve the last known location.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    // Inflate the custom AppBar.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_map, menu)
    }

    // Show the map types upon a click on the three dots in the AppBar.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Delete all markers upon a click on the AppBar trash can, with an option to undo the action.
            R.id.delete -> {
                // Show an AlertDialog to prevent accidental deletion of the markers.
                AlertDialog.Builder(context)
                    .setTitle(R.string.confirmation)
                    .setMessage(R.string.action_markers)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        // Delete all markers in the database, with an option to undo the action.
                        viewModel.deleteAllMarkers(map, requireView())
                    }
                    .setNegativeButton(R.string.no) { _, _ ->
                        // Show a Snackbar message which says that the action has been cancelled.
                        Snackbar.make(requireView(), R.string.cancelled, Snackbar.LENGTH_SHORT).show()
                    }
                    // Prevent the AlertDialog from being closed upon a click outside of the AlertDialog.
                    .setCancelable(false)
                    .create()
                    .show()
                true
            }
            // Change the map type to normal upon a click on the normal map button.
            R.id.normal_map -> {
                map.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            // Change the map type to hybrid upon a click on the hybrid map button.
            R.id.hybrid_map -> {
                map.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            // Change the map type to satellite upon a click on the satellite map button.
            R.id.satellite_map -> {
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            // Change the map type to terrain upon a click on the terrain map button.
            R.id.terrain_map -> {
                map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Request the ACCESS_FINE_LOCATION permission and move the camera to the last known location.
    private fun retrieveLocation() {
        // If the permission has already been granted, enable the my-location layer on the map to show a blue dot on the location.
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            // If it has not already been granted, request the ACCESS_FINE_LOCATION permission.
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // Retrieve the last known location and move the camera to it.
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                lastLocation = it
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f))
            }
        }
    }

    // If the ACCESS_FINE_LOCATION permission has been granted, move the camera to the last known location.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                retrieveLocation()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Retrieve all markers from the database and add them to the map.
        viewModel.getAllMarkers(map)

        // Enable the zoom controls on the map.
        map.uiSettings.isZoomControlsEnabled = true

        // Declare this fragment as the callback triggered upon a click on a marker.
        map.setOnMarkerClickListener(this)

        // Request the ACCESS_FINE_LOCATION permission and move the camera to the last known location.
        retrieveLocation()

        // Add a marker upon a long click on the map.
        map.setOnMapLongClickListener {
            addMarker(it)
        }

        // Add a marker upon a click on a point of interest.
        map.setOnPoiClickListener {
            addMarker(it.latLng, it.name)
        }

        // Delete a marker upon a long click on a marker.
        map.setOnMarkerDragListener(object : OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
                // Remove the marker from the map.
                marker.remove()

                // val marker = com.example.madlevel8.model.Marker(marker.position.toString(), marker.title, marker.snippet)

                // Delete the marker from the database.
                viewModel.deleteMarker(marker.position.toString())

                // Show a Snackbar message which says that the marker has been deleted.
                Snackbar.make(requireView(), getString(R.string.deleted, marker.title), Snackbar.LENGTH_SHORT).show()
            }

            override fun onMarkerDragEnd(marker: Marker) { }

            override fun onMarkerDrag(marker: Marker) { }
        })
    }

    private fun addMarker(position: LatLng, title: String = "") {
        // Create a MarkerOptions object and set the marker position.
        val markerOptions = MarkerOptions().position(position)

        // Turn the latitude and longitude coordinates into an address and store it in a list.
        val addresses = Geocoder(context).getFromLocation(position.latitude, position.longitude, 1)

        // Initialize a string to store the address in.
        var address = ""

        // If the list contains an address, store it in the string.
        if (addresses != null && addresses.isNotEmpty()) {
            address += addresses[0].getAddressLine(0)
        }

        // Set the marker snippet to the address.
        markerOptions.snippet(address)

        // Change the color of the default marker to green.
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        // If the marker is not a point of interest, show an EditText to retrieve a custom title.
        if (title.isBlank()) {
            // Store the input of the EditText.
            val input = EditText(context)

            AlertDialog.Builder(context)
                .setTitle(R.string.add_marker_name)
                .setView(input)
                .setPositiveButton(R.string.add) { _, _ ->
                    // Retrieve the input of the EditText.
                    val title = input.text.toString()

                    // Set the marker title.
                    markerOptions.title(title)

                    // Add the marker to the map.
                    val marker = map.addMarker(markerOptions)

                    // Make the marker removable.
                    marker.isDraggable = true

                    // Show the info window of the marker.
                    marker.showInfoWindow()

                    // Add the marker to the database.
                    viewModel.insertMarker(com.example.madlevel8.model.Marker(position.toString(), title, address), requireView())
                }
                .setNegativeButton(R.string.cancel, null)
                // Prevent the AlertDialog from being closed upon a click outside of the AlertDialog.
                .setCancelable(false)
                .create()
                .show()
        } else {
            // Set the marker title.
            markerOptions.title(title)

            // Add the marker to the map.
            val marker = map.addMarker(markerOptions)

            // Make the marker removable.
            marker.isDraggable = true

            // Show the info window of the marker.
            marker.showInfoWindow()

            // Add the marker to the database.
            viewModel.insertMarker(com.example.madlevel8.model.Marker(position.toString(), title, address), requireView())
        }
    }

    override fun onMarkerClick(p0: Marker?) = false

    // Release the view if the fragment is destroyed to prevent a memory leak.
    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}