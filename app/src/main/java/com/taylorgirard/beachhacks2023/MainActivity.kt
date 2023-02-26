package com.taylorgirard.beachhacks2023

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
private lateinit var database: DatabaseReference
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        val btnAddPin = findViewById<Button>(R.id.btnAddPin)
        btnAddPin.setOnClickListener{
            val intent = Intent(this, AddPinActivity::class.java)
            startActivity(intent)
            finish()
        }
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val btnGroups = findViewById<Button>(R.id.btnGroup)
        btnGroups.setOnClickListener {
            val user = Firebase.auth.currentUser
            database = Firebase.database.reference
            if(user != null) {
                database.child("Users").child(user.uid).child("Group").get().addOnCompleteListener { task ->
                    if(task.result.value.toString() != "null") {
                        startActivity(Intent(this, GroupDetailsActivity::class.java))
                    }
                    else {
                        startActivity(Intent(this, GroupActivity::class.java))
                    }
                }
            }
        }

        val btnSignOut = findViewById<Button>(R.id.LogoutBtn)
        btnSignOut.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Set the map's camera position to the current location of the device.
                val lastKnownLocation = task.result
                if (lastKnownLocation != null) {
                    map?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude
                            ), 15.toFloat()
                        )
                    )
                }
            }
        }

        val user = Firebase.auth.currentUser
        val database = Firebase.database.reference
        if (user != null) {
            database.child("Users").child(user.uid).child("Group").get()
                .addOnCompleteListener { task ->
                    val userGroup = task.result.value.toString()
                    val pins = database.child("Groups").child(userGroup).child("Pins")

                    pins.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (pin in dataSnapshot.children) {
                                var position = LatLng(pin.child("Lat").value.toString().toDouble(), pin.child("Lng").value.toString().toDouble())
                                var title = pin.child("Title").value.toString()
                                var description = pin.child("Description").value.toString()
                                var colorPosition = pin.child("Color").value.toString().toInt()
                                var color = BitmapDescriptorFactory.HUE_AZURE
                                when (colorPosition){
                                    0 -> color = BitmapDescriptorFactory.HUE_AZURE
                                    1 -> color = BitmapDescriptorFactory.HUE_BLUE
                                    2 -> color = BitmapDescriptorFactory.HUE_CYAN
                                    3 -> color = BitmapDescriptorFactory.HUE_GREEN
                                    4 -> color = BitmapDescriptorFactory.HUE_MAGENTA
                                    5 -> color = BitmapDescriptorFactory.HUE_ORANGE
                                    6 -> color = BitmapDescriptorFactory.HUE_RED
                                    7 -> color = BitmapDescriptorFactory.HUE_ROSE
                                    8 -> color = BitmapDescriptorFactory.HUE_VIOLET
                                    9 -> color = BitmapDescriptorFactory.HUE_YELLOW
                                }
                                val marker = map.addMarker(MarkerOptions().position(position).title(title).snippet(description).icon(BitmapDescriptorFactory.defaultMarker(color)))
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                }
        }

        map.setOnInfoWindowClickListener {

        }

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Set the map's camera position to the current location of the device.
                val lastKnownLocation = task.result
                if (lastKnownLocation != null) {
                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(lastKnownLocation!!.latitude,
                            lastKnownLocation!!.longitude), 15.toFloat()))
                }
            }
        }
    }




}