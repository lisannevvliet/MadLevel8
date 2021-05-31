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

                // Add the marker to the map.
                map.addMarker(markerOptions)
            }
        }
    }

    // Add the marker to the database.
    fun insertMarker(marker: Marker) {
        CoroutineScope(Dispatchers.IO).launch { markerRepository.insertMarker(marker) }
    }

    // Delete the marker from the database.
    fun deleteMarker(marker: String) {
        CoroutineScope(Dispatchers.IO).launch { markerRepository.deleteMarker(marker) }
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