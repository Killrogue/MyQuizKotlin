package com.epsi.myquiz

import android.content.Intent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class BaseActivity : AppCompatActivity() {
    protected var user: FirebaseUser? = null
    private lateinit var connection: Button
    private lateinit var subscribe: Button
    private lateinit var logOut: Button
    private lateinit var admin: Button
    private lateinit var profile: Button
    protected lateinit var intentConnection: Intent
    private lateinit var intentSubscribe: Intent
    protected lateinit var intentQuiz: Intent
    private lateinit var intentAdmin: Intent
    private lateinit var intentProfile: Intent

    override fun onResume() {
        super.onResume()

        intentConnection = Intent(this, ConnectionActivity::class.java)
        intentSubscribe = Intent(this, SubscribeActivity::class.java)
        intentQuiz = Intent(this, QuizActivity::class.java)
        intentAdmin = Intent(this, AdminActivity::class.java)
        intentProfile = Intent(this, ProfileActivity::class.java)

        connection = findViewById(R.id.btnConnection)
        subscribe = findViewById(R.id.btnSubscribe)
        logOut = findViewById(R.id.btnLogOut)
        admin = findViewById(R.id.btnAdmin)
        profile = findViewById(R.id.btnProfile)

        connection.setOnClickListener {
            startActivity(intentConnection)
        }
        subscribe.setOnClickListener {
            startActivity(intentSubscribe)
        }
        logOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            updateUi()
            finish()
        }
        admin.setOnClickListener {
            startActivity(intentAdmin)
        }

        profile.setOnClickListener {
            startActivity(intentProfile)
        }

        updateUi()
    }


    private fun updateUi() {
        user = Firebase.auth.currentUser
        user?.displayName
        if (user != null) {
            connection.visibility = View.GONE
            subscribe.visibility = View.GONE
            logOut.visibility = View.VISIBLE
            profile.visibility = View.VISIBLE
            if (user!!.email == "admin@admin.fr" && user!!.displayName == "admin"){
                admin.visibility = View.VISIBLE
            }
        } else {
            connection.visibility = View.VISIBLE
            subscribe.visibility = View.VISIBLE
            logOut.visibility = View.GONE
            profile.visibility = View.GONE
            admin.visibility = View.GONE
        }
    }
}

/**
 * Reste à faire :
 *
 * Possibilité de modifier son adresse mail et/ou son mot de passe
 * Possibilité de supprimer son compte -> suppréssion de toute les données de l'utilisateur
 *
 * Améliorations possibles -> classement, possibilité de soumettre une question (joueur -> validation admin), différents packs de niveaux de questions
 * **/