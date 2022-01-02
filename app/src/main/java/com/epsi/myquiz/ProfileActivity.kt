package com.epsi.myquiz

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.widget.*
import com.google.firebase.auth.EmailAuthProvider
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
    private lateinit var btnDeleteAccount : Button
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

        btnDeleteAccount.setOnClickListener {
            deleteAccount()
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
        btnDeleteAccount = findViewById(R.id.btn_deleteAccount)
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
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

     private fun deleteAccount() {
         val builder = AlertDialog.Builder(this)
         builder.setTitle(R.string.alert_dialog_delete_confirm)
             .setMessage(R.string.alert_dialog_delete_msg)
         val input = EditText(this)
         input.hint = "Entrez votre mot de passe"
         input.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
         builder.setView(input)
         builder.setPositiveButton(R.string.comfirm
             ) { _, _ ->
                 val scoreData = db.collection("score")
                 scoreData.whereEqualTo("user", user?.email).get()
                     .addOnSuccessListener { result ->
                     if(result != null){
                         for (document in result.documents){
                             db.collection("score").document(document.id).delete()
                         }
                     }
                 }
                 val credential = EmailAuthProvider
                         .getCredential(user!!.email.toString(), input.text.toString())
                 user!!.reauthenticate(credential)
                     .addOnCompleteListener { task ->
                     if (task.isSuccessful) {
                         user!!.delete()
                             .addOnCompleteListener { delete ->
                                 if (delete.isSuccessful) {
                                     Log.d(TAG, "User account deleted.")
                                     finish()
                                 }
                             }
                     }
                 }
             }
             .setNegativeButton(R.string.cancel) { _, _ -> }
         // Create the AlertDialog object and return it
         builder.create().show()
     }
}
