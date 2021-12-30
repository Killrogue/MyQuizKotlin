package com.epsi.myquiz

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

open class ProfileActivity : BaseActivity(){
    private lateinit var profileUserName : TextView
    private lateinit var profileUserEmail : TextView
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        user = Firebase.auth.currentUser
        if (user == null){
            finish()
        }
        val name = user?.displayName
        val email = user?.email
        profileUserName = findViewById(R.id.profileUserName)
        profileUserEmail = findViewById(R.id.profileUserEmail)

        profileUserName.text = name
        profileUserEmail.text = email
    }

    override fun onResume() {
        super.onResume()
        val scoreData = db.collection("score")
        scoreData.whereEqualTo("user", user?.email).limit(10)
            .get()
            .addOnSuccessListener { result ->
                if(result != null){
                    val listAdapter = ProfileAdapter(this@ProfileActivity, result.documents)
                    val listView = findViewById<ListView>(R.id.listViewProfile)
                    listView.adapter = listAdapter
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }
}
