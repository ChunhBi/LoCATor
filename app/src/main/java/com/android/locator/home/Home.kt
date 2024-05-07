package com.android.locator.home

import LocationHelper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.locator.LoCATorRepo
import com.android.locator.R
import com.android.locator.UpdateListener
import com.android.locator.UpdateType
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
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date


class Home : Fragment(), OnMapReadyCallback, OnMarkerClickListener, UpdateListener{
    private var _binding: HomeBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var map: GoogleMap? = null
    private val repo =LoCATorRepo.getInstance()
    private lateinit var wits:List<Witness>
    private lateinit var groupedWitnesses:Map<String, List<Witness>>
    private lateinit var latestWitnesses: MutableList<Witness>

    var currentLocation:LatLng= LatLng(0.0,0.0)

    private var status=0

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
            homeRefreshBtn.setOnClickListener { refresh() }
        }
    }

    fun loadData(){
            try {
                wits = repo.getWits()
                // Log the size of wits to check if data is fetched
                Log.d("MyFragment", "Wits list size: ${wits.size}")

                groupedWitnesses = wits.groupBy { it.catId }

                latestWitnesses = mutableListOf<Witness>()

                groupedWitnesses.forEach { (_, witnesses) ->
                    // Get the witness with the latest time for each catId
                    val latestWitness = witnesses.maxByOrNull { it.time }
                    // Add the latest witness to the list
                    latestWitness?.let { latestWitnesses.add(it) }
                }

            } catch (e: Exception) {
                // Log the exception if there is any error
                Log.e("MyFragment", "Error fetching wits or adding markers: ${e.message}")
            }


    }

    override fun onMapReady(p0: GoogleMap) {
        // Bind the GoogleMap instance
        map = p0
        map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        // Add markers to the map or perform other map operations

        loadData()
        drawLatestMarkers()


        //addMarkersToMap()
        map?.setOnMarkerClickListener(this)

        refresh()
    }

    fun refresh() {
        val locationHelper=LocationHelper(requireContext())
        locationHelper.getCurrentLocation(object : LocationHelper.LocationCallback {
            override fun onLocationResult(location: Location) {
                currentLocation= LatLng(location.latitude, location.longitude)
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                map?.addMarker(MarkerOptions().position(currentLocation).title("You are here"))
            }
            override fun onLocationUnavailable() {
                Toast.makeText(requireContext(),"Location unavailable", Toast.LENGTH_SHORT).show()
            }
        })
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
            var catImg: Bitmap? = repo.getWitBitMap(wit.id)
            if(catImg==null){
                catImg=repo.getCatFirstImg(wit.catId)
            }

            val markerTitle = "Square Marker"
            val markerSnippet = "This is a square marker with an image"

            map?.let {
                if (catImg != null) {
                    addCustomIconMarker(it, latLng, catImg, markerTitle, markerSnippet,wit.catId,wit.time)
//                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
//                    map?.addMarker(MarkerOptions().position(currentLocation).title("You are here"))
                    //map?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))

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
        val ersizedMap=BitmapHelper.resizeBitmap(bitmap, 150, 150)
        val framedMap=BitmapHelper.addFrameToBitmap(BitmapHelper.addRoundedCornersToBitmap(ersizedMap,15f),frameColor,50,time)
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







    override fun onMarkerClick(marker: Marker): Boolean {
        // Retrieve the ID from the marker's tag property
        val tag=marker.tag.toString()
        //Toast.makeText(requireContext(),marker.tag.toString(),Toast.LENGTH_SHORT).show()
        Log.d("MARKER","clicked")
        map?.clear()

        if(status==0){
            drawMarkersOfACat(tag)
        }else{
            drawLatestMarkers()
        }




        // Return false to allow the default behavior (e.g., displaying an info window)
        return false
    }

    fun drawMarkersOfACat(catId:String){
        var catwits=getSortedCatWits(catId)
        Log.d("MARKER","num of wit: ${catwits.size}")
        val posList= mutableListOf<LatLng>()
        catwits.forEach {
            addMarkersToMap(it)
            posList.add(convertGeoPointToLatLng(it.geoPoint))
        }
        map?.let { drawPolyline(it,posList, R.color.orange, 10f) }
        map?.addMarker(MarkerOptions().position(currentLocation).title("You are here"))
        //map?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
        status=1
    }

    fun drawLatestMarkers(){
        val last10 = latestWitnesses.takeLast(10)
        last10.forEach { wit ->
            addMarkersToMap(wit)
        }
        map?.addMarker(MarkerOptions().position(currentLocation).title("You are here"))
        //map?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
        status=0
    }

    fun getSortedCatWits(catId:String): List<Witness> {
        groupedWitnesses.keys.forEach{Log.d("MARKER","Cat as key : ${it}")}
        Log.d("MARKER","Cat key : ${catId}")
        val witnessesForCat = groupedWitnesses[catId] ?: emptyList()

        val sortedWitnesses = witnessesForCat.sortedBy { it.time }
        return sortedWitnesses
    }

    fun convertGeoPointToLatLng(geoPoint: GeoPoint): LatLng {
        val latitude = geoPoint.latitude
        val longitude = geoPoint.longitude
        return LatLng(latitude, longitude)
    }

    fun drawPolyline(googleMap: GoogleMap, points: List<LatLng>, color: Int, width: Float) {
        // Define polyline options
        val polylineOptions = PolylineOptions()
            .addAll(points) // Add all LatLng points to the polyline
            .color(color) // Set polyline color
            .width(width) // Set polyline width

        // Add the polyline to the map
        googleMap.addPolyline(polylineOptions)
    }

    override fun update(type: UpdateType) {
        if(type==UpdateType.WITNESS){
            loadData()
            map?.clear()
            drawLatestMarkers()
        }
        else if (type==UpdateType.SHOW_SPECIFIC_CAT) {
            // TODO: show history of a selected cat, should add a parameter catid
        }
    }


}