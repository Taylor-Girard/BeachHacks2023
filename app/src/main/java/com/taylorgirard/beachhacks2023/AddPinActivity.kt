package com.taylorgirard.beachhacks2023

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
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.cache.CacheBuilder.newBuilder
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Interners.newBuilder
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpRequest
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import org.json.JSONObject
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.nio.file.attribute.AclEntry.newBuilder


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

            var weburl = "https://maps.googleapis.com/maps/api/geocode/json?" + "address=" + address + "&key=" + BuildConfig.MAPS_API_KEY

            val stringRequest = StringRequest(Request.Method.GET, weburl,
                { response ->

                    val obj = JSONObject(response)
//                    if (obj.optString("status") == "true"){
                        val results = obj.getJSONArray("results")
                        val addressComponents = results.getJSONObject(0);
                        val geometry = addressComponents.getJSONObject("geometry");
                        val location = geometry.getJSONObject("location");
                        val lat = location.getDouble("lat");
                        val long = location.getDouble("lng");

                        Log.i("geocoding",response)
                        Toast.makeText(baseContext, "Response is: $lat + $long", Toast.LENGTH_SHORT).show()
//                    } else {
                        //Toast.makeText(baseContext, "ahhhh", Toast.LENGTH_SHORT).show()
                    //}
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