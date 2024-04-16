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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.locator.R
import com.android.locator.databinding.FragmentWitnessBinding
import kotlinx.coroutines.launch

class WitnessListFragment:Fragment() {
    private var _binding: FragmentWitnessBinding?=null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val witnessListViewModel: WitnessListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWitnessBinding.inflate(inflater, container, false)
        binding.witnessRecyclerView.layoutManager = LinearLayoutManager(context)
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
                            WitnessListFragmentDirections.actionWitnessList2CatList()
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
                            WitnessListFragmentDirections.actionWitnessList2Home()
                        )
                        true
                    }
                    else -> false
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                witnessListViewModel.witnesses.collect {witnesses ->
                    binding.witnessRecyclerView.adapter = WitnessListAdapter(witnesses)
                }
            }
        }
    }
}