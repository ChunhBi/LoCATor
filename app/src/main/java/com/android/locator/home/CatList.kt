package com.android.locator.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.locator.R
import com.android.locator.databinding.FragmentListBinding
import kotlinx.coroutines.launch

class CatList:Fragment() {
    private var _binding: FragmentListBinding?=null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val catListViewModel: CatListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.catsRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        binding.apply {
            listNavBar.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.list_2_home -> {
                        // Handle Home button click
                        navController.navigate(
                            CatListDirections.actionCatList2Home()
                        )
                        true
                    }
                    R.id.list_add_cat -> {
                        // Handle Profile button click
                        true
                    }
                    R.id.list_2_witness -> {
                        // Handle Profile button click
                        navController.navigate(
                            CatListDirections.actionCatList2WitnessList()
                        )
                        true
                    }
                    else -> false
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                catListViewModel.cats.collect {cats ->
                    binding.catsRecyclerView.adapter = CatListAdapter(cats)
                }
            }
        }
    }
}