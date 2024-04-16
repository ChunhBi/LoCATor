package com.android.locator.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.locator.databinding.FragmentCatInfoBinding
import com.android.locator.databinding.FragmentReportBinding

class CatInfoFragment:Fragment() {
    private var _binding: FragmentCatInfoBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val args: CatInfoFragmentArgs by navArgs() // use args.catId
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        binding.apply {
            catInfoBack.setOnClickListener {
                navController.navigate(
                    CatInfoFragmentDirections.actionCatInfo2CatList()
                )
            }
        }
    }

}