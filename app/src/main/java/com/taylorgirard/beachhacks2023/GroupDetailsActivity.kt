package com.taylorgirard.beachhacks2023

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

private lateinit var auth: FirebaseAuth
private lateinit var database: DatabaseReference

class GroupDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)

        var btnLeaveGroup = findViewById<Button>(R.id.btnLeaveGroup)
        var groupText = findViewById<TextView>(R.id.GroupNum)

        val user = Firebase.auth.currentUser
        database = Firebase.database.reference

        if(user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (user != null) {
            database.child("Users").child(user.uid).child("Group").get().addOnCompleteListener { task ->
                groupText.text = task.result.value.toString()
            }.addOnFailureListener {
                groupText.text = "error getting group"
            }
        }

        btnLeaveGroup.setOnClickListener {
            if (user != null) {
                var num = ""
                database.child("Users").child(user.uid).child("Group").get().addOnCompleteListener { task ->
                    num = task.result.value.toString()
                }
                database.child("Groups").child(num).child(user.uid).removeValue()
                database.child("Users").child(user.uid).child("Group").removeValue()
            }
        }
    }
}