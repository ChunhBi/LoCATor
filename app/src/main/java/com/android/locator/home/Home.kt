package com.android.locator.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.locator.databinding.FragmentHomeBinding
import com.android.locator.databinding.HomeBinding

class Home : Fragment(){
    private var _binding: HomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
}