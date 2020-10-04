package com.example.testproject

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.gms.location.*
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import java.io.IOException
import java.lang.StringBuilder
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    val msklocation = LatLng(55.7558, 37.6173)
    lateinit var myLocation: LatLng
    var myExactLocation: LatLng = LatLng(55.7558, 37.6173)
    private lateinit var map: GoogleMap
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private var PERMISSION_ID = 52

    companion object {
        private const val PERMISSION_CODE = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("TuT", "That's here")
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        Log.d("TuT", "That's here about getLastLocation")
        getLastLocation()
        Log.d("TuT", "That's here after the method")
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.d("TuT", "That's over here")
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miItem1 -> Toast.makeText(
                    applicationContext,
                    "Clicked Item 1",
                    Toast.LENGTH_LONG
                ).show()
                R.id.miItem2 -> Toast.makeText(
                    applicationContext,
                    "Clicked Item 2",
                    Toast.LENGTH_LONG
                ).show()
                R.id.miItem3 -> Toast.makeText(
                    applicationContext,
                    "Clicked Item 3",
                    Toast.LENGTH_LONG
                ).show()
            }
            true
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        bottomSheetBehavior.isHideable = false

        fun getAddress(lat: LatLng): String? {
            val geocoder = Geocoder(this)
            val list = geocoder.getFromLocation(lat.latitude, lat.longitude, 1)
            return list[0].getAddressLine(0)
        }

        Log.d("TuT", "That's here about map")
        (mapFragment as SupportMapFragment).getMapAsync { map ->
            this.map = map
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@getMapAsync
            }
//            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//            val loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            map.cameraPosition = loc.latitude
            Log.d("TuT", "That's here after the map")
            map.isMyLocationEnabled = true
            map.setOnMapLongClickListener { latlng ->
                myLocation = LatLng(latlng.latitude, latlng.longitude)
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_marker)
                val scaleBitmap = Bitmap.createScaledBitmap(bitmap, 110, 110, false)
                val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(scaleBitmap)
                getAddress(myLocation)
                val titleStr = getAddress(myLocation)
                val marker = MarkerOptions()
                    .position(myLocation)
                    .title(titleStr)
                    .draggable(true)
                    .icon(bitmapDescriptor)
                map.addMarker(marker)
                map.setOnMarkerClickListener { marker ->
                   marker.remove()
                    true

                }
            }
            Log.d("TuT", "That's here about bitmap")
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_marker)
            val scaleBitmap = Bitmap.createScaledBitmap(bitmap, 110, 110, false)
            val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(scaleBitmap)
            Log.d("Debug", "I'm here")
            val marker = MarkerOptions()
                .position(myExactLocation)
                .title("Marker")
                .draggable(true)
                .icon(bitmapDescriptor)
            map.addMarker(marker)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(myExactLocation, 17f))
            map.setOnMarkerClickListener { marker ->
                true

            }
        }
        Log.d("TuT", "That's here after bitmap")
    }


    private fun getAddress(latLng: LatLng): String {
        // 1
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            // 2
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }
    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)

        val titleStr = getAddress(location)  // add these two lines
        markerOptions.title(titleStr)

        map.addMarker(markerOptions)
    }
    private fun CheckPermission(): Boolean {
        Log.d("TuT", "That's here above check")
        if (
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        Log.d("TuT", "That's here after check")
        return false
    }

    private fun RequestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun getLastLocation() {
        Log.d("TuT", "That's here above it")
        if (CheckPermission()) {
            if (isLocationEnabled()) {
                Log.d("TuT", "That's here after return")
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location = task.result
                    if (location == null) {
                        getNewLocation()
                    } else {
                        Log.d("TuT", "That's the problem")
                        myExactLocation = LatLng(location.latitude, location.longitude)
                        Log.d("TuT", "That's ${location.latitude + location.longitude}")
                    }
                }
            } else {
                Toast.makeText(this, "Please Enable your Location service", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            RequestPermission()
        }
    }

    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation = p0.lastLocation
            myExactLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
            Log.d("TuT", "That's what  I need")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug", "You have the permission")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

