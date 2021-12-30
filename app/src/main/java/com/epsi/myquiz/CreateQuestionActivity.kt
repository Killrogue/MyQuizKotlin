package com.epsi.myquiz

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateQuestionActivity : BaseActivity(){
    private lateinit var etQuestion : EditText
    private lateinit var etResponse1 : EditText
    private lateinit var etResponse2 : EditText
    private lateinit var etResponse3 : EditText
    private lateinit var etResponse4 : EditText
    private lateinit var etGoodResponse : EditText
    private lateinit var errorMessage : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createquestion)
        val btnSubmitForm = findViewById<Button>(R.id.btn_submitQuestion)
        initialization()
        errorMessage.visibility = View.GONE

        btnSubmitForm.setOnClickListener {
            addNewQuestion()
        }
    }

    override fun onBackPressed(){
        finish()
    }

    private fun initialization() {
        etQuestion = findViewById(R.id.et_question)
        etResponse1 = findViewById(R.id.et_response1)
        etResponse2 = findViewById(R.id.et_response2)
        etResponse3 = findViewById(R.id.et_response3)
        etResponse4 = findViewById(R.id.et_response4)
        etGoodResponse = findViewById(R.id.et_goodResponse)
        errorMessage = findViewById(R.id.errorMessage)
    }

    @SuppressLint("SetTextI18n")
    private fun validateInput() : Boolean {
        if (etQuestion.text.isNullOrBlank()){
            errorMessage.text = "Veulliez renseigner votre question"
            return false
        }
        if (etResponse1.text.isNullOrBlank() || etResponse2.text.isNullOrBlank() || etResponse3.text.isNullOrBlank() || etResponse4.text.isNullOrBlank()){
            errorMessage.text = "Vous devez remplir toutes les possibilités de réponses"
            return false
        }
        if (etGoodResponse.text.isNullOrBlank()){
            errorMessage.text = "Vous devez préciser le numéro de la bonne réponse"
            return false
        }
        try {
            if ((Integer.parseInt(etGoodResponse.text.toString()) > 4) ||(Integer.parseInt(etGoodResponse.text.toString()) <= 0)){
                errorMessage.text = "Valeur invalide"
                return false
            }
        }catch (e: NumberFormatException){
            errorMessage.text = "Valeur invalide"
            return false
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun addNewQuestion() {
        if (validateInput()){
            // Write a message to the database
            val db = Firebase.firestore
            val question = hashMapOf(
                "question" to etQuestion.text.toString(),
                "responses" to arrayListOf(etResponse1.text.toString(), etResponse2.text.toString(), etResponse3.text.toString(), etResponse4.text.toString()),
                "goodResponse" to Integer.parseInt(etGoodResponse.text.toString())
            )
            db.collection("quiz")
                .add(question)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    Toast.makeText(baseContext, "Ajout réussi",
                        Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(baseContext, "Erreur lors de l'ajout",
                        Toast.LENGTH_SHORT).show()
                }
        }else{
            errorMessage.visibility = View.VISIBLE
        }
    }
}