package com.epsi.myquiz

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SubscribeActivity : AppCompatActivity() {

    private lateinit var etUserName : EditText
    private lateinit var etPassword : EditText
    private lateinit var etEmail : EditText
    private lateinit var etConfirmPassword : EditText
    private lateinit var errorMessage : TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val btnSubscribe = findViewById<Button>(R.id.btn_subscribe)
        val connectionLink = findViewById<TextView>(R.id.connectionLink)
        auth = Firebase.auth
        initialization()
        errorMessage.visibility = View.GONE

        btnSubscribe.setOnClickListener {
            signUp()
        }
        connectionLink.setOnClickListener{
            val intentConnection = Intent(this, ConnectionActivity::class.java)
            finish()
            startActivity(intentConnection)
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            finish()
        }
    }

    override fun onBackPressed(){
        finish()
    }

    private fun initialization(){
        etUserName = findViewById(R.id.et_user_name)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        errorMessage = findViewById(R.id.errorMessage)
    }

    @SuppressLint("SetTextI18n")
    private fun validateInput() : Boolean {
        if (etUserName.text.isNullOrBlank()){
            errorMessage.text = "Veuillez entrer votre nom"
            return false
        }
        if (etEmail.text.isNullOrBlank()){
            errorMessage.text = "Veuillez entrer votre adresse email"
            return false
        }
        if(etPassword.text.isNullOrBlank()){
            errorMessage.text = "Veuillez entrer votre mot de passe"
            return false
        }
        if(etConfirmPassword.text.isNullOrBlank()){
            errorMessage.text = "Veuillez confirmer votre mot de passe"
            return false
        }
        if (!isEmailValid(etEmail.text.toString())){
            errorMessage.text = "Veuillez entrer une adresse email valide"
        }
        if (etPassword.text.length < 6){
            errorMessage.text = "Le mot de passe doit faire plus de 6 caractères"
        }
        if (etPassword.text.toString() != etConfirmPassword.text.toString()){
            errorMessage.text = "Les mots de passe doivent être identiques"
            return false
        }
        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun signUp(){
        if (validateInput()){
            auth.createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        val infoUser = UserProfileChangeRequest.Builder()
                            .setDisplayName(etUserName.text.toString()).build()

                        user?.updateProfile(infoUser)
                            ?.addOnCompleteListener { it->
                            if (it.isSuccessful){
                                Log.e("Update info", "infos updated")
                                Toast.makeText(baseContext, "Inscription réussie",
                                    Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Log.e("Update info", "infos updated")
                            }
                        }
                            ?.addOnFailureListener { err ->
                            err.printStackTrace()
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    }
                }.addOnFailureListener {
                    it.printStackTrace()
                }
        }else{
            errorMessage.visibility = View.VISIBLE
        }
    }
}