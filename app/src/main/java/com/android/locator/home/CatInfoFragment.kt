package com.android.locator.home

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.locator.Cat
import com.android.locator.LoCATorRepo
import com.android.locator.UpdateType
import com.android.locator.databinding.FragmentCatInfoBinding
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
                //catInfoImg.setImageBitmap(cat?.let { repo.getCatFirstImg(it.id) })
                val catImg=cat?.let { repo.getCatFirstImg(it.id) }
                catInfoImg.setImageBitmap(catImg?.let { BitmapHelper.addRoundedCornersToBitmap(it,15f) })
                catInfoName.text= "Name: "+cat?.name ?: "undefined"
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(cat?.createdAt)
                catInfoDate.text= "Added at: "+formattedDate
                catInfoHistory.setOnClickListener {
                    cat?.let { it1 -> repo.notifyUpdate(UpdateType.SHOW_SPECIFIC_CAT, it1.id) }
                    findNavController().navigate(
                        CatInfoFragmentDirections.actionCatInfo2Home()
                    )
                }
                catInfoDelete.setOnClickListener{
                    if(repo.isManager){
                        CoroutineScope(Dispatchers.Main).launch{
                            var id=""
                            cat?.let { it1 -> id=it1.id }
                            repo.deleteCat(id)
                            repo.reloadCats()
                            repo.notifyUpdate(UpdateType.CAT)
                            repo.notifyUpdate(UpdateType.WITNESS)
                            Thread.sleep(1000)
                            navController.navigate(
                                CatInfoFragmentDirections.actionCatInfo2CatList()
                            )

                        }
                    }else{
                        Toast.makeText(requireContext(),"Only a manager can delete a cat.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}