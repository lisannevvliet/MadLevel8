package com.example.madlevel8.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.madlevel8.R
import com.example.madlevel8.databinding.FragmentSettingsBinding
import com.example.madlevel8.vm.ProductViewModel
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment.
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDeleteProducts.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(R.string.confirmation)
                .setMessage(R.string.action)
                .setPositiveButton(R.string.yes) { _, _ ->
                    viewModel.deleteAllProducts()
                    Snackbar.make(binding.btnDeleteProducts, getString(R.string.deleted, "Products"), Snackbar.LENGTH_LONG).show()
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    Snackbar.make(binding.btnDeleteProducts, R.string.cancelled, Snackbar.LENGTH_LONG).show()
                }
                .create()
                .show()
        }
    }

    // Release the view if the fragment is destroyed to prevent a memory leak.
    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}