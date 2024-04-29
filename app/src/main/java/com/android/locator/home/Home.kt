package com.android.locator.home

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.locator.LoCATorRepo
import com.android.locator.R
import com.android.locator.Witness
import com.android.locator.databinding.HomeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale


class Home : Fragment(), OnMapReadyCallback, OnMarkerClickListener{
    private var _binding: HomeBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var map: GoogleMap? = null
    private val repo =LoCATorRepo.getInstance()
    private lateinit var wits:List<Witness>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val view = binding.root

        // Retrieve the SupportMapFragment through binding and set the map callback
        val mapFragment = childFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)




        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

        }
    }

    override fun onMapReady(p0: GoogleMap) {
        // Bind the GoogleMap instance
        map = p0
        map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        // Add markers to the map or perform other map operations

        lifecycleScope.launch{
            try {
                wits = repo.getWits()
                // Log the size of wits to check if data is fetched
                Log.d("MyFragment", "Wits list size: ${wits.size}")

                // Add markers to the map for each wit
                wits.forEach { wit ->
                    addMarkersToMap(wit)
                }
            } catch (e: Exception) {
                // Log the exception if there is any error
                Log.e("MyFragment", "Error fetching wits or adding markers: ${e.message}")
            }

        }


        //addMarkersToMap()
        map?.setOnMarkerClickListener(this)
    }

    fun geoPointToLatLng(geoPoint: GeoPoint): LatLng {
        // Extract latitude and longitude from GeoPoint
        val latitude = geoPoint.latitude
        val longitude = geoPoint.longitude

        // Create and return a LatLng instance using the latitude and longitude
        return LatLng(latitude, longitude)
    }
    private fun addMarkersToMap(wit:Witness) {

        Log.d("MARKER",wit.geoPoint.toString())

        val latLng = geoPointToLatLng(wit.geoPoint)



        lifecycleScope.launch {
            val catImt: Bitmap? = repo.getWitBitMap(wit.id)

            val markerTitle = "Square Marker"
            val markerSnippet = "This is a square marker with an image"

            map?.let {
                if (catImt != null) {
                    addCustomIconMarker(it, latLng, catImt, markerTitle, markerSnippet,wit.catId,wit.time)
                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
                }
            }
        }

    }

    private fun createSquareBitmapWithImage(drawable: Drawable, size: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)

        return bitmap
    }

    private fun addCustomIconMarker(map: GoogleMap, latLng: LatLng, bitmap: Bitmap, markerTitle: String, markerSnippet: String, tag:String, time:Date) {
        // Convert the bitmap to a BitmapDescriptor
        val frameColor = resources.getColor(R.color.yellow_orange)
        val ersizedMap=resizeBitmap(bitmap, 150, 150)
        val framedMap=addFrameToBitmap(addRoundedCornersToBitmap(ersizedMap,15f),frameColor,50,time)
        val icon = BitmapDescriptorFactory.fromBitmap(framedMap)

        // Create a MarkerOptions with the specified position, title, snippet, and custom icon
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(markerTitle)
            .snippet(markerSnippet)
            .icon(icon)



        // Add the marker to the map
        val marker=map.addMarker(markerOptions)
        marker?.tag=tag
    }

    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        // Create a matrix for resizing the bitmap
        val matrix = Matrix()

        // Calculate the scale factor for width and height
        val scaleWidth = width.toFloat() / bitmap.width
        val scaleHeight = height.toFloat() / bitmap.height

        // Set the scale factors on the matrix
        matrix.postScale(scaleWidth, scaleHeight)

        // Create a new resized bitmap using the matrix
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun addFrameToBitmap(originalBitmap: Bitmap, frameColor: Int, frameWidth: Int, time:Date): Bitmap {
        // Create a new bitmap with padding for the frame
        val framedWidth = originalBitmap.width + frameWidth
        val framedHeight = originalBitmap.height + frameWidth+50
        val framedBitmap = Bitmap.createBitmap(framedWidth, framedHeight+30, Bitmap.Config.ARGB_8888)

        // Create a Canvas to draw on the new bitmap
        val canvas = Canvas(framedBitmap)

        // Create a Paint object for the frame
        val paint = Paint().apply {
            color = frameColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        // Create a Path object for the rounded rectangle
        val path = Path().apply {
            addRoundRect(
                0f, 0f,
                framedWidth.toFloat(), framedHeight.toFloat(),
                30f, 30f,
                Path.Direction.CW
            )
        }

        // Draw the colored rounded rectangle on the new bitmap
        canvas.drawPath(path, paint)

        val trianglePath = Path().apply {
            moveTo(framedWidth / 2f-30, framedHeight.toFloat())
            lineTo((framedWidth / 2f) + 30, framedHeight.toFloat())
            lineTo((framedWidth / 2f), framedHeight.toFloat() +30)
            close()
        }
        canvas.drawPath(trianglePath, paint)

        val textPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
        this.textSize = 50f
        textAlign = Paint.Align.LEFT // Adjust text alignment as needed
    }

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    // Draw the text on the bitmap at the specified coordinates
    canvas.drawText(dateFormat.format(time), 38f, 55f, textPaint)


        // Draw the original bitmap centered within the frame
        val left = frameWidth/2
        val top = frameWidth/2+50
        canvas.drawBitmap(originalBitmap, left.toFloat(), top.toFloat(), null)

        return framedBitmap
    }

    fun addRoundedCornersToBitmap(originalBitmap: Bitmap, cornerRadius: Float): Bitmap {
        // Create a new bitmap with the same width and height as the original bitmap
        val width = originalBitmap.width
        val height = originalBitmap.height
        val roundedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Create a Canvas to draw on the new bitmap
        val canvas = Canvas(roundedBitmap)

        // Create a Path object to define the rounded corners
        val path = Path()
        path.addRoundRect(0f, 0f, width.toFloat(), height.toFloat(), cornerRadius, cornerRadius, Path.Direction.CW)

        // Create a Paint object to draw the bitmap with anti-aliasing enabled
        val paint = Paint()
        paint.isAntiAlias = true

        // Clip the canvas to the rounded corners path
        canvas.clipPath(path)

        // Draw the original bitmap onto the canvas, which will apply the rounded corners
        canvas.drawBitmap(originalBitmap, 0f, 0f, paint)

        return roundedBitmap
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // Retrieve the ID from the marker's tag property
        Toast.makeText(requireContext(),marker.tag.toString(),Toast.LENGTH_SHORT).show()
        Log.d("MARKER","clicked")

        // Return false to allow the default behavior (e.g., displaying an info window)
        return false
    }



}