package com.android.locator.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.locator.databinding.FragmentAddCatBinding
import com.android.locator.databinding.FragmentCatInfoBinding

class AddCatFragment:Fragment() {
    private var _binding: FragmentAddCatBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCatBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        binding.apply {
            addCatBackBtn.setOnClickListener {
                navController.navigate(
                    AddCatFragmentDirections.actionAddCat2CatList()
                )
            }
        }
    }

}