package com.example.geoquiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import org.w3c.dom.Text


private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0
private const val EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown"


class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var cheatButton: Button
    private lateinit var cheatingCounterTextView: TextView
    private var counter = 0
    private var rightAnswer = 0
    private val maxCheating = 3
    private var cheatingCounter = 0


    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatButton = findViewById(R.id.cheat_button)
        cheatingCounterTextView = findViewById(R.id.cheating_counter)

        countCheating()

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
            quizViewModel.isCheater = false
        }

        cheatButton.setOnClickListener{
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            quizViewModel.countCheat()
            countCheating()
            if (cheatingCounter == 3){
                cheatButton.isEnabled = false
            }
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }
        updateQuestion()
    }

    private fun countCheating() {
        cheatingCounter = quizViewModel.cheatingCounter
        val countOfCheatingRemain = "Осталось попыток ${maxCheating - cheatingCounter}"
        cheatingCounterTextView.text = countOfCheatingRemain
    }

    private fun toastAnswer(string: Int) {
        val toast = Toast.makeText(this, string, Toast.LENGTH_SHORT)
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
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK){
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }
}