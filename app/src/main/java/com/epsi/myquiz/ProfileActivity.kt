package com.epsi.myquiz

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

open class ProfileActivity : BaseActivity(){
    private lateinit var profileUserName : TextView
    private lateinit var profileUserEmail : TextView
    private lateinit var etEmailProfile : EditText
    private lateinit var etPasswordProfile : EditText
    private lateinit var btnSubmitEmail : Button
    private lateinit var btnSubmitPwd : Button
    private lateinit var errorMessageEmail : TextView
    private lateinit var errorMessagePwd : TextView
    private val db = Firebase.firestore

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        user = Firebase.auth.currentUser
        initialization()
        if (user == null){
            finish()
        }
        val name = user?.displayName
        val email = user?.email

        profileUserName.text = name
        profileUserEmail.text = email
        etEmailProfile.setText(email)

        btnSubmitEmail.setOnClickListener {
            if (Patterns.EMAIL_ADDRESS.matcher(etEmailProfile.text).matches()) {
                user?.updateEmail(etEmailProfile.text.toString())
                Toast.makeText(
                    baseContext, "Modification réussie",
                    Toast.LENGTH_SHORT).show()
            }else
                errorMessageEmail.text = "Adresse Email invalide"
        }
        btnSubmitPwd.setOnClickListener {
            if (etPasswordProfile.text.length >= 6) {
                user?.updatePassword(etPasswordProfile.text.toString())
                Toast.makeText(
                    baseContext, "Modification réussie",
                    Toast.LENGTH_SHORT
                ).show()
            }else
                errorMessagePwd.text = "Le mot de passe doit faire plus de 6 caractères"
        }
    }

    private fun initialization(){
        profileUserName = findViewById(R.id.profileUserName)
        profileUserEmail = findViewById(R.id.profileUserEmail)
        etEmailProfile = findViewById(R.id.et_emailProfile)
        etPasswordProfile = findViewById(R.id.et_passwordProfile)
        btnSubmitEmail = findViewById(R.id.btn_submitEmail)
        btnSubmitPwd = findViewById(R.id.btn_submitPwd)
        errorMessageEmail = findViewById(R.id.errorMessageEmail)
        errorMessagePwd = findViewById(R.id.errorMessagePwd)
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
