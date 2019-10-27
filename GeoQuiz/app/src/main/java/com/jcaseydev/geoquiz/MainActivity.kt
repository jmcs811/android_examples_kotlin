package com.jcaseydev.geoquiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView

    // setting up ViewModel object
    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // getting currentIndex from savedState if available
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        // wiring up Views
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)

        // Click listeners for all buttons
        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }

        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }

        cheatButton.setOnClickListener { view: View ->
            /** start the CheatActivity, and
             * pass the answer to the current question
             */
            val intent = CheatActivity.newIntent(
                this@MainActivity,
                quizViewModel.currentQuestionAnswer
            )

            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }

        questionTextView.setOnClickListener { view: View ->
            quizViewModel.moveToNext()
            updateQuestion()
        }

        prevButton.setOnClickListener { view: View ->
            quizViewModel.moveToPrev()
            isAnswered()
            updateQuestion()
        }

        nextButton.setOnClickListener { view: View ->
            quizViewModel.moveToNext()
            isAnswered()
            updateQuestion()
        }

        updateQuestion()
    }

    // Determine if user actually cheated
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false

            if (quizViewModel.isCheater) {
                quizViewModel.currentQuestionCheat = true
            }
        }
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        // complicated but works ;)
        val messageId = if (userAnswer == correctAnswer) {
            quizViewModel.currentQuestionCorrect = true
            if (quizViewModel.currentQuestionCheat) {
                R.string.judgment_toast
            } else {
                R.string.correct_toast
            }
        } else {
            quizViewModel.currentQuestionCorrect = false
            if (quizViewModel.currentQuestionCheat) {
                R.string.judgment_toast
            } else {
                R.string.incorrect_toast
            }
        }

        quizViewModel.currentQuestionAnswered = true

        if (quizViewModel.currentIndex == quizViewModel.questionBankSize - 1) {
            showGrade()
        } else {
            Toast.makeText(
                this,
                messageId,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun isAnswered() {
        val isQuestionAnswered = quizViewModel.currentQuestionAnswered
        trueButton.isEnabled = !isQuestionAnswered
        falseButton.isEnabled = !isQuestionAnswered
    }

    private fun showGrade() {
        Toast.makeText(this, quizViewModel.calcGrade().toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        Log.i(TAG, "onSavedInstanceState")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }
}
