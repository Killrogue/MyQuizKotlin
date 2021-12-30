package com.epsi.myquiz

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ConnectionActivity : AppCompatActivity() {

    private lateinit var etPassword : EditText
    private lateinit var etEmail : EditText
    private lateinit var errorMessage : TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        val btnConnection = findViewById<Button>(R.id.btn_submit)
        val subscribeLink = findViewById<TextView>(R.id.subscribeLink)
        auth = Firebase.auth
        initialization()
        errorMessage.visibility = View.GONE
        btnConnection.setOnClickListener {
            signIn()
        }
        subscribeLink.setOnClickListener{
            val intentConnection = Intent(this, SubscribeActivity::class.java)
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

    private fun initialization() {
        etEmail = findViewById(R.id.et_emailConnection)
        etPassword = findViewById(R.id.et_password)
        errorMessage = findViewById(R.id.errorMessage)
    }

    @SuppressLint("SetTextI18n")
    private fun validateInput(): Boolean{
        if (etEmail.text.isNullOrBlank()){
            errorMessage.text = "Veuillez entrer votre adresse email"
            return false
        }
        if(etPassword.text.isNullOrBlank()){
            errorMessage.text = "Veuillez entrer votre mot de passe"
            return false
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun signIn() {
        if (validateInput()){
            auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInWithEmail:success")
                        Toast.makeText(baseContext, "Connexion réussie",
                        Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        errorMessage.text = "Votre adresse email ou votre mot de passe est erroné"
                        errorMessage.visibility = View.VISIBLE
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
        }else{
            errorMessage.visibility = View.VISIBLE
        }
    }
}
