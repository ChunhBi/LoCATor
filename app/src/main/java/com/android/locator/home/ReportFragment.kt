package com.android.locator.home

import LocationHelper
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.android.locator.LoCATorRepo
import com.android.locator.Witness
import com.android.locator.databinding.FragmentReportBinding
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class ReportFragment:Fragment() {
    companion object {
        const val CAMERA_REQUEST_CODE = 100
    }
    private var _binding: FragmentReportBinding? = null
    private lateinit var catsAdapter: CatReportAdapter
    private val repo=LoCATorRepo.getInstance()
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val reportViewModel: ReportViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        binding.apply {
            submitBackBtn.setOnClickListener {
                navController.navigate(
                    ReportFragmentDirections.actionReport2Home()
                )
            }
            reportSubmit.setOnClickListener{
                val selectedCatId=catsAdapter.getSelectedCat()
                if(selectedCatId==null){
                    Toast.makeText(requireContext(),"Select a cat to submit",Toast.LENGTH_SHORT).show()
                }else{
                    Log.d("SELECT","reporting cat: "+selectedCatId)

                        val locationHelper = LocationHelper(requireContext())
                        locationHelper.getCurrentLocation(object : LocationHelper.LocationCallback {
                            override fun onLocationResult(location: Location) {
                                // Handle the obtained location
                                val latitude = location.latitude
                                val longitude = location.longitude
                                val position = GeoPoint(location.latitude, location.longitude)
                                // Do something with latitude and longitude
                                val newWit=Witness(id="", catId =selectedCatId, geoPoint = position, time = Date())
                                CoroutineScope(Dispatchers.Main).launch{
                                    val newWitId=repo.add_witness(newWit)
                                    val drawable: Drawable? = binding.reportImg.drawable
                                    if (drawable != null && drawable is BitmapDrawable) {
                                        // Get the Bitmap from the BitmapDrawable
                                        val bitmap: Bitmap = drawable.bitmap
                                        // Use the bitmap as needed
                                        repo.upload_witness_img(newWitId,bitmap)

                                        Toast.makeText(requireContext(),"Witness reported",Toast.LENGTH_SHORT).show()
                                        repo.reloadWitnesses()
                                        navController.navigate(
                                            ReportFragmentDirections.actionReport2Home()
                                        )

                                    }
                                }


                            }

                            override fun onLocationUnavailable() {
                                Toast.makeText(requireContext(),"Location unavailable",Toast.LENGTH_SHORT).show()
                            }
                        })



                }
            }
            reportImg.setOnClickListener(){
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(),"Camera permission denied.",Toast.LENGTH_SHORT).show()

                } else {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
                }

            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                reportViewModel.cats.collect {cats ->
                    catsAdapter = CatReportAdapter(cats)
                    binding.catsRecyclerView.adapter = catsAdapter
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val photo: Bitmap = data?.extras?.get("data") as Bitmap
            binding.reportImg.setImageBitmap(photo)
        }
    }

}