package com.android.locator.home

import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.locator.Cat
import com.android.locator.LoCATorRepo
import com.android.locator.databinding.FragmentCatInfoBinding
import com.android.locator.databinding.FragmentReportBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class CatInfoFragment:Fragment() {
    private var _binding: FragmentCatInfoBinding? = null
    private val repo=LoCATorRepo.getInstance()
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val args: CatInfoFragmentArgs by navArgs() // use args.catId
    private var cat: Cat?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        CoroutineScope(Dispatchers.Main).launch {
            cat=repo.findCatById(args.catId)

        }
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
        CoroutineScope(Dispatchers.Main).launch {
            binding.apply {
                catInfoImg.setImageBitmap(repo.getCatImg(cat?.images?.get(0)))
                catInfoName.text= "Name: "+cat?.name ?: "undefined"
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(cat?.createdAt)
                catInfoDate.text= "Added at: "+formattedDate
            }
        }
    }

}