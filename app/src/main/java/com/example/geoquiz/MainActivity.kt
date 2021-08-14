package com.example.geoquiz

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var questionTextView: TextView
    private var counter = 0
    private var rightAnswer = 0

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)


        trueButton.setOnClickListener {
            checkAnswer(true)
            it.isEnabled = false
            falseButton.isEnabled = false
        }
        falseButton.setOnClickListener {
            checkAnswer(false)
            it.isEnabled = false
            trueButton.isEnabled = false
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
        updateQuestion()
    }

    private fun toastAnswer(string: Int) {
        val toast = Toast.makeText(this, string, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
    }

    private fun updateQuestion() {
        val points: Float = (rightAnswer / (counter + 1) * 100).toFloat()
        Toast.makeText(this, "Score: $points%", Toast.LENGTH_SHORT).show()
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
        trueButton.isEnabled = true
        falseButton.isEnabled = true

    }

    private fun checkAnswer(userAnswer: Boolean) {
        counter++
        val correctAnswer = quizViewModel.currentQuestionAnswer
        if (userAnswer == correctAnswer) {
            toastAnswer(R.string.correct_toast)
            rightAnswer++
        } else {
            toastAnswer(R.string.incorrect_toast)
        }
    }
}