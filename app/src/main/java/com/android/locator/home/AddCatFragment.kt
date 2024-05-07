package com.android.locator.home

import AccessPermissionHelper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.locator.Cat
import com.android.locator.LoCATorRepo
import com.android.locator.R
import com.android.locator.UpdateType
import com.android.locator.databinding.FragmentAddCatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class AddCatFragment:Fragment() {
    private var _binding: FragmentAddCatBinding? = null
    private var hasImg=false
    val repo=LoCATorRepo.getInstance()
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // Update the ImageView with the selected image
        if (uri != null) {
            binding.addCatImg.setImageURI(uri)
            hasImg=true
        } else {
            binding.addCatImg.setImageResource(R.drawable.baseline_add_a_photo_24)
            hasImg=false
        }
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
            addCatImg.setOnClickListener{
                openImagePicker()
            }

            addCatSubmit.setOnClickListener{
                val catName=editTextText.text.toString()
                val drawable: Drawable? = binding.addCatImg.drawable
                if(hasImg==false){
                    Toast.makeText(requireContext(),"Please select a photo of the cat.",Toast.LENGTH_SHORT).show()
                }else if(catName==null||catName.equals("")){
                    Toast.makeText(requireContext(),"Cat name cannot be empty.",Toast.LENGTH_SHORT).show()
                }else if(drawable != null && drawable is BitmapDrawable){
                    val bitmap: Bitmap = drawable.bitmap
                    var c:String=""
                    if(repo.campus==null){
                        Toast.makeText(requireContext(),"You don't have a campus!",Toast.LENGTH_SHORT).show()
                        navController.navigate(
                            AddCatFragmentDirections.actionAddCat2CatList()
                        )
                    }else{
                        c= repo.campus!!
                    }

                    val newCat= Cat(campus = c, createdAt = Date(), name = catName,id="", images = mutableListOf())
                    CoroutineScope(Dispatchers.Main).launch {
                        repo.add_cat(newCat, bitmap)
                        repo.reloadCats()
                        repo.notifyUpdate(UpdateType.CAT)
                        Thread.sleep(1000)
                        Toast.makeText(requireContext(),"Cat added.",Toast.LENGTH_SHORT).show()
                        navController.navigate(
                            AddCatFragmentDirections.actionAddCat2CatList()
                        )
                    }
                }
            }
        }
    }

    private fun openImagePicker() {
        if(!AccessPermissionHelper.isGalleryPermissionGranted()){
            AccessPermissionHelper.requestGalleryPermission()
        }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch("image/*")
    }
}