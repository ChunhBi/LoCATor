package com.android.locator.home.nav_bar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.locator.R
import com.android.locator.databinding.FragmentWitnessBarBinding

class WitnessBar : Fragment() {
    private var _binding: FragmentWitnessBarBinding?=null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWitnessBarBinding.inflate(inflater, container, false)
        binding.apply {

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        binding.apply {
            witnessNavBar.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.witness_2_list -> {
                        // Handle Home button click
                        navController.navigate(
                            WitnessBarDirections.actionWitnessToList()
                        )
                        true
                    }
                    R.id.witness_add_witness -> {
                        // Handle Profile button click
                        true
                    }
                    R.id.witness_2_home -> {
                        // Handle Profile button click
                        navController.navigate(
                            WitnessBarDirections.actionWitnessToHome()
                        )
                        true
                    }
                    else -> false
                }
            }
        }
    }

}