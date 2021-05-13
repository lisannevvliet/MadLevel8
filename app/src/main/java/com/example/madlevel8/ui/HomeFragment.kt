package com.example.madlevel8.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.madlevel8.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment.
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnScan.setOnClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this)

            // Remove the default text.
            integrator.setPrompt("")
            // Disable the beep upon a successful scan.
            integrator.setBeepEnabled(false)
            // Start the barcode scanner.
            integrator.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result.contents != null) {
            Snackbar.make(binding.btnScan, "Scanned: " + result.contents, Snackbar.LENGTH_LONG).show()
        } else {
            Snackbar.make(binding.btnScan, "Cancelled", Snackbar.LENGTH_LONG).show()
        }
    }
}