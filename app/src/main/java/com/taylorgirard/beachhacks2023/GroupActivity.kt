package com.taylorgirard.beachhacks2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

private lateinit var database: DatabaseReference
private lateinit var auth: FirebaseAuth

class GroupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        var btnJoin = findViewById<Button>(R.id.JoinGroupBtn)
        var joinNumber = findViewById<EditText>(R.id.JoinGroupNumber)
        var createNumber = findViewById<EditText>(R.id.CreateNumber)
        var btnCreate = findViewById<Button>(R.id.CreateGroupBtn)

        database = Firebase.database.reference
        auth = Firebase.auth

        btnJoin.setOnClickListener {
            val joinNum = joinNumber.text.toString()
            database.child("Users").child(auth.currentUser!!.uid).child("Group").setValue(joinNum)
        }

        btnCreate.setOnClickListener {
            val createNum = createNumber.text.toString()
            database.child("Users").child(auth.currentUser!!.uid).child("Group").setValue(createNum)
        }
    }
}