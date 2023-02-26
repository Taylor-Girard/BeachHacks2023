package com.taylorgirard.beachhacks2023

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

private lateinit var database: DatabaseReference
private lateinit var auth: FirebaseAuth

class GroupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val user = Firebase.auth.currentUser
        database = Firebase.database.reference

        if(user != null) {
            val gNum = database.child("Users").child(user.uid).child("Group").get().addOnCompleteListener { task ->
                if(task.result.value.toString() != "null") {
                    val intent = Intent(this, GroupDetailsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        var btnJoin = findViewById<Button>(R.id.JoinGroupBtn)
        var joinNumber = findViewById<EditText>(R.id.JoinGroupNumber)
        var createNumber = findViewById<EditText>(R.id.CreateNumber)
        var btnCreate = findViewById<Button>(R.id.CreateGroupBtn)

        database = Firebase.database.reference
        auth = Firebase.auth

        btnJoin.setOnClickListener {
            val joinNum = joinNumber.text.toString()
            if (user != null) {
                database.child("Users").child(user.uid).child("Group").setValue(joinNum)
                database.child("Groups").child(joinNum).child(user.uid).setValue(joinNum)
            }
        }

        btnCreate.setOnClickListener {
            val createNum = createNumber.text.toString()
            if (user != null) {
                database.child("Users").child(user.uid).child("Group").setValue(createNum)
                database.child("Groups").child(createNum).child(user.uid).setValue(createNum)
            }
        }
    }
}