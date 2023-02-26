package com.taylorgirard.beachhacks2023

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.ActionCodeSettings.newBuilder
import com.google.firebase.auth.OAuthProvider.newBuilder
import com.google.firebase.auth.PhoneAuthOptions.newBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.cache.CacheBuilder.newBuilder
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Interners.newBuilder
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpRequest
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.nio.file.attribute.AclEntry.newBuilder
import java.util.*


class AddPinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pin)

        val etAddress = findViewById<EditText>(R.id.etAddress)
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val queue = Volley.newRequestQueue(this)

        btnSubmit.setOnClickListener{
            val address = etAddress.text.toString()
            val title = etTitle.text.toString()
            val description = etDescription.text.toString()

            var weburl = "https://maps.googleapis.com/maps/api/geocode/json?" + "address=" + address + "&key=" + BuildConfig.MAPS_API_KEY

            val stringRequest = StringRequest(Request.Method.GET, weburl,
                { response ->

                    val obj = JSONObject(response)
                    val results = obj.getJSONArray("results")
                    if (results.length() != 0){
                        val addressComponents = results.getJSONObject(0);
                        val geometry = addressComponents.getJSONObject("geometry");
                        val location = geometry.getJSONObject("location");
                        val lat = location.getDouble("lat");
                        val long = location.getDouble("lng");

                        val user = Firebase.auth.currentUser
                        val database = Firebase.database.reference
                        if (user != null) {
                            database.child("Users").child(user.uid).child("Group").get().addOnCompleteListener { task ->
                                val userGroup = task.result.value.toString()
                                var uniqueID = UUID.randomUUID().toString()
                                database.child("Groups").child(userGroup).child("Pins").child(uniqueID).child("Title").setValue(title)
                                database.child("Groups").child(userGroup).child("Pins").child(uniqueID).child("Description").setValue(description)
                                database.child("Groups").child(userGroup).child("Pins").child(uniqueID).child("Lat").setValue(lat)
                                database.child("Groups").child(userGroup).child("Pins").child(uniqueID).child("Lng").setValue(long)

                            }
                        }
                        Log.i("geocoding",response)

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Invalid Address",
                            Toast.LENGTH_SHORT).show()
                    }

                },
                {
                    Toast.makeText(
                        baseContext, "That didn't work!",
                        Toast.LENGTH_SHORT
                    ).show()
                })

            queue.add(stringRequest)

        }

    }
}