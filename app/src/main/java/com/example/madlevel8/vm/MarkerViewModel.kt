package com.example.madlevel8.vm

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import com.example.madlevel8.R
import com.example.madlevel8.model.Marker
import com.example.madlevel8.repository.MarkerRepository
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarkerViewModel(application: Application) : AndroidViewModel(application) {

    private val markerRepository = MarkerRepository(application.applicationContext)

    // Retrieve all markers from the database and add them to the map.
    fun getAllMarkers(map: GoogleMap) {
        CoroutineScope(Dispatchers.Main).launch {
            val markers = withContext(Dispatchers.IO) { markerRepository.getAllMarkers() }

            for (marker in markers) {
                // Retrieve a LatLng object from the position string.
                val string = marker.position.replace("lat/lng: (","").replace(")","").split(",")
                val latitude = string[0].toDouble()
                val longitude = string[1].toDouble()
                val position = LatLng(latitude, longitude)

                // Create a MarkerOptions object and set the marker position.
                val markerOptions = MarkerOptions().position(position)

                // Set the marker title.
                markerOptions.title(marker.title)

                // Set the marker snippet to the address.
                markerOptions.snippet(marker.address)

                // Change the color of the default marker to green.
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

                // Add the marker to the map and make it removable.
                map.addMarker(markerOptions).isDraggable = true
            }
        }
    }

    // Add the marker to the map and database if it does not already exist.
    fun insertMarker(marker: Marker, map: GoogleMap, markerOptions: MarkerOptions, view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            // Check if there is already an entry of the specified marker in the database.
            val exists = withContext(Dispatchers.IO) { markerRepository.existMarker(marker.address) != 0 }

            // If there is not an entry in the database yet, create an entry.
            if (!exists) {
                // Add the marker to the database.
                withContext(Dispatchers.IO) { markerRepository.insertMarker(marker) }

                // Add the marker to the map.
                val marker = map.addMarker(markerOptions)

                // Make the marker removable.
                marker.isDraggable = true

                // Show the info window of the marker.
                marker.showInfoWindow()

                // Show a Snackbar message which says that the marker has been added.
                Snackbar.make(view, getApplication<Application>().resources.getString(R.string.added, marker.title), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    // Delete the marker from the map and database.
    fun deleteMarker(marker: com.google.android.gms.maps.model.Marker, view: View) {
        // Remove the marker from the map.
        marker.remove()

        // Create a marker object from the Google Maps marker.
        val marker = Marker(marker.position.toString(), marker.title, marker.snippet)

        // Delete the marker from the database.
        CoroutineScope(Dispatchers.IO).launch { markerRepository.deleteMarker(marker.address) }

        // Show a Snackbar message which says that the marker has been deleted.
        Snackbar.make(view, getApplication<Application>().resources.getString(R.string.deleted, marker.title), Snackbar.LENGTH_SHORT).show()
    }

    // Delete all markers in the database, with an option to undo the action.
    fun deleteAllMarkers(map: GoogleMap, view: View) {
        // Remove all markers from the map.
        map.clear()

        // Show a Snackbar message which says that all markers have been deleted, with an undo option to undo the action.
        Snackbar.make(view, R.string.all_markers_deleted, Snackbar.LENGTH_LONG)
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(snackbar: Snackbar, event: Int) {
                    when (event) {
                        DISMISS_EVENT_ACTION -> {
                            // Retrieve all markers and put them back on the map.
                            getAllMarkers(map)
                        } else -> {
                            // Permanently delete all markers from the database.
                            CoroutineScope(Dispatchers.IO).launch { markerRepository.deleteAllMarkers() }
                        }
                    }
                }
            })
            .setAction(R.string.undo) { }
            .show()
    }
}