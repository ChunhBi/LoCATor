package com.android.locator.home.nav_bar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.locator.R
import com.android.locator.databinding.FragmentHomeBarBinding
import com.android.locator.home.HomeFragmentDirections

class HomeBar : Fragment() {
    private var _binding: FragmentHomeBarBinding?=null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            homeNavBar.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home_2_list -> {
                        // Handle Home button click
                        findNavController().navigate(
                            HomeBarDirections.actionHomeBarToListBar()
                        )
//                        supportFragmentManager.findFragmentById
//                        findNavController(R.id.page_container)
//                            .navigate(
//                            HomeFragmentDirections.actionNavigationHomeToCatList()
//                        )
                        true
                    }
                    R.id.home_witness -> {
                        // Handle Profile button click
                        true
                    }
                    R.id.home_2_not -> {
                        // Handle Profile button click
                        true
                    }
                    else -> false
                }
            }
        }
    }

}