package com.android.locator.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.locator.R
import com.android.locator.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        binding.apply {
            homeNavBar.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home_2_list -> {
                        // Handle Home button click
                        navController.navigate(
                            HomeFragmentDirections.actionHome2CatList()
                        )
                        true
                    }
                    R.id.home_witness -> {
                        // Handle Profile button click
                        true
                    }
                    R.id.home_2_not -> {
                        // Handle Profile button click
                        navController.navigate(
                            HomeFragmentDirections.actionPageHome2WitnessList()
                        )
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}