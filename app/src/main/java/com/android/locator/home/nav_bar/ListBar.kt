package com.android.locator.home.nav_bar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.locator.R
import com.android.locator.databinding.FragmentListBarBinding

class ListBar : Fragment() {
    private var _binding: FragmentListBarBinding?=null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBarBinding.inflate(inflater, container, false)
        binding.apply {

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            listNavBar.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.list_2_home -> {
                        // Handle Home button click
                        findNavController().navigate(
                            ListBarDirections.actionListBarToHomeBar()
                        )
                        true
                    }
                    R.id.list_add_cat -> {
                        // Handle Profile button click
                        true
                    }
                    R.id.list_2_not -> {
                        // Handle Profile button click
                        true
                    }
                    else -> false
                }
            }
        }
    }

}