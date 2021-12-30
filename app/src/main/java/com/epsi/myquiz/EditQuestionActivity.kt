package com.epsi.myquiz

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditQuestionActivity : BaseActivity(){
    private lateinit var etQuestion : EditText
    private lateinit var etResponse1 : EditText
    private lateinit var etResponse2 : EditText
    private lateinit var etResponse3 : EditText
    private lateinit var etResponse4 : EditText
    private lateinit var etGoodResponse : EditText
    private lateinit var errorMessage : TextView
    private lateinit var idQuestion : String
    private val db = Firebase.firestore
    private val quizData = db.collection("quiz")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createquestion)
        val lblEdit = findViewById<TextView>(R.id.lbl_title)
        lblEdit.text = "Modifier une question"
        idQuestion = intent.getStringExtra("idQuestion").toString()
        val btnSubmitForm = findViewById<Button>(R.id.btn_submitQuestion)
        initialization()
        errorMessage.visibility = View.GONE
        btnSubmitForm.setOnClickListener {
            editQuestion()
        }
    }

    override fun onBackPressed(){
        finish()
    }

    @Suppress("UNCHECKED_CAST")
    private fun initialization() {
        etQuestion = findViewById(R.id.et_question)
        etResponse1 = findViewById(R.id.et_response1)
        etResponse2 = findViewById(R.id.et_response2)
        etResponse3 = findViewById(R.id.et_response3)
        etResponse4 = findViewById(R.id.et_response4)
        etGoodResponse = findViewById(R.id.et_goodResponse)
        errorMessage = findViewById(R.id.errorMessage)

        quizData.document(idQuestion).get()
            .addOnSuccessListener { document ->
                if (document != null){
                    val list : List<String> = document.data?.getValue("responses") as List<String>
                    etQuestion.setText(document.data?.getValue("question").toString())
                    etResponse1.setText(list[0])
                    etResponse2.setText(list[1])
                    etResponse3.setText(list[2])
                    etResponse4.setText(list[3])
                    etGoodResponse.setText(document.data?.getValue("goodResponse").toString())
                }
            }
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

    private fun editQuestion() {
        if (validateInput()) {
            // Write a message to the database
            val question = hashMapOf(
                "question" to etQuestion.text.toString(),
                "responses" to arrayListOf(
                    etResponse1.text.toString(),
                    etResponse2.text.toString(),
                    etResponse3.text.toString(),
                    etResponse4.text.toString()
                ),
                "goodResponse" to Integer.parseInt(etGoodResponse.text.toString())
            )
            quizData.document(idQuestion).update(question as Map<String, Any>)
            finish()
        }else{
            errorMessage.visibility = View.VISIBLE
        }
    }
}
