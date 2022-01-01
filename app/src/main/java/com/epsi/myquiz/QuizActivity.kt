package com.epsi.myquiz

import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates




class QuizActivity : AppCompatActivity(), View.OnClickListener {
    private val db = Firebase.firestore
    private lateinit var progressBar: ProgressBar
    private lateinit var question : TextView
    private lateinit var response1 : Button
    private lateinit var response2 : Button
    private lateinit var response3 : Button
    private lateinit var response4 : Button
    private var remainingQuestionCount = 0
    private var listQuestions : ArrayList<DocumentSnapshot> = arrayListOf()
    private var currentQuestion : DocumentSnapshot? = null
    private var goodResponse by Delegates.notNull<Int>()
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        initialization()

        listQuestions = questionList()
        response1.setOnClickListener(this)
        response2.setOnClickListener(this)
        response3.setOnClickListener(this)
        response4.setOnClickListener(this)

    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.alert_dialog_title_back)
            .setMessage(R.string.alert_dialog_msg_back)
            .setPositiveButton(R.string.yes,
                DialogInterface.OnClickListener { _, _ ->
                    score = 0
                    finish()
                })
            .setNegativeButton(R.string.no,
                DialogInterface.OnClickListener { _, _ ->
                    Toast.makeText(this, "Canceled", Toast.LENGTH_LONG).show()
                })
        // Create the AlertDialog object and return it
        builder.create().show()
    }

    private fun initialization() {
        question = findViewById(R.id.Question)
        response1 = findViewById(R.id.response1)
        response2 = findViewById(R.id.response2)
        response3 = findViewById(R.id.response3)
        response4 = findViewById(R.id.response4)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun questionList() : ArrayList<DocumentSnapshot>{
        val quizData = db.collection("quiz")
        quizData.get()
            .addOnSuccessListener { result ->
                for(document in result){
                    listQuestions.add(document)
                }
                displayQuestion(getCurrentQuestion())
                remainingQuestionCount = listQuestions.count()
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
        this.listQuestions = listQuestions.shuffled() as ArrayList<DocumentSnapshot>
        return listQuestions
    }

    private fun getCurrentQuestion(): DocumentSnapshot {
        currentQuestion = listQuestions[listQuestions.size - 1]
        return currentQuestion!!
    }

    private fun getNextQuestion(): DocumentSnapshot {
        listQuestions.remove(currentQuestion)
        return getCurrentQuestion()

        }

    @Suppress("UNCHECKED_CAST")
    private fun displayQuestion(Question : DocumentSnapshot) {
        val list : List<String> = Question.data?.getValue("responses") as List<String>
        question.text = Question.data!!.getValue("question").toString()
        response1.text = list[0]
        response2.text = list[1]
        response3.text = list[2]
        response4.text = list[3]
        goodResponse = Integer.parseInt(Question.data!!.getValue("goodResponse").toString())
        progressBar.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {

        val index: Int = when (v) {
            response1 -> {
                1
            }
            response2 -> {
                2
            }
            response3 -> {
                3
            }
            response4 -> {
                4
            }
            else -> {
                throw IllegalStateException("Unknown clicked view : $v")
            }
        }

        if (index == goodResponse){
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
            score += 50
        }else{
            Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show()
        }
        remainingQuestionCount--
        if (remainingQuestionCount > 0){
            displayQuestion(getNextQuestion())
        }else{
            val db = Firebase.firestore
            val user = Firebase.auth.currentUser
            val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val scoreUser = hashMapOf(
                "user" to user!!.email,
                "scoreUser" to score,
                "date" to today
            )
            db.collection("score").add(scoreUser)
            finishGame()
        }
    }

    private fun finishGame() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.alert_dialog_title_finish)
            .setMessage("Votre score est de $score")
            .setPositiveButton(R.string.replay
            ) { _, _ ->
                finish()
                startActivity(intent)
            }
            .setNegativeButton(R.string.leave
            ) { _, _ ->
                finish()
            }
        // Create the AlertDialog object and return it
        builder.create().show()
    }
}