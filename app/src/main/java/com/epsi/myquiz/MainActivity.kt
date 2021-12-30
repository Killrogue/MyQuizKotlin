package com.epsi.myquiz

import android.os.Bundle
import android.widget.Button

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playTime = findViewById<Button>(R.id.playTime)

        playTime.setOnClickListener {
            if (user == null){
                startActivity(intentConnection)
            }else{
                startActivity(intentQuiz)
            }
        }
    }
}