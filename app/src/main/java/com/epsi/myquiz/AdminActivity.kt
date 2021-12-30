package com.epsi.myquiz

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminActivity : BaseActivity(){
    private val db = Firebase.firestore
    private lateinit var quizLayout: LinearLayout
    private lateinit var edit: ImageView
    private lateinit var delete: ImageView


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        val createQuestion = findViewById<Button>(R.id.createQuestion)
        createQuestion.setOnClickListener{
            if (user!!.email == "admin@admin.fr" && user!!.displayName == "admin"){
                val intent = Intent(this, CreateQuestionActivity::class.java)
                startActivity(intent)
            }
        }
    }
    fun reload() {
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()
        val quizData = db.collection("quiz")
        quizData.get()
            .addOnSuccessListener { result ->
                if(result != null){
                    val listAdapter = Adapter(this@AdminActivity, result.documents)
                    val listView = findViewById<ListView>(R.id.listViewQuiz)
                    listView.adapter = listAdapter
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }
}