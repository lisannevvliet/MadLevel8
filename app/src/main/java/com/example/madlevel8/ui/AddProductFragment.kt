package com.example.madlevel8.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.madlevel8.R
import com.example.madlevel8.databinding.FragmentAddProductBinding
import com.example.madlevel8.model.Product
import com.example.madlevel8.vm.ProductViewModel
import com.google.android.material.snackbar.Snackbar

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment.
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAdd.setOnClickListener {
            val name = binding.etName.text.toString()
            val vegan = binding.sVegan.isChecked
            val product = Product(name, vegan)

            viewModel.insertProduct(product)

            Snackbar.make(binding.btnAdd, "${product.name} was successfully added.", Snackbar.LENGTH_LONG).show()

            findNavController().navigate(R.id.action_addProductFragment_to_navigation_home)
        }

        // Retrieve the fragment result from the HomeFragment and pass it onto the bind function.
        setFragmentResultListener(requestKey) { _, bundle ->
            val query = bundle.getString(bundleKey)

            binding.etName.setText(query)
        }
    }
}