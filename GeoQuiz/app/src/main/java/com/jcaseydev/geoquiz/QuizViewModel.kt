package com.jcaseydev.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {

    var currentIndex = 0

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    val questionBankSize = questionBank.size

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    var currentQuestionCorrect: Boolean
        get() = questionBank[currentIndex].correct
        set(value) {
            questionBank[currentIndex].correct = value
        }

    var currentQuestionAnswered: Boolean
        get() = questionBank[currentIndex].answered
        set(value) {
            questionBank[currentIndex].answered = value
        }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        currentIndex = (currentIndex + questionBank.size - 1) % questionBank.size
    }

    fun calcGrade(): Float {
        val totalQuestions: Float = questionBank.size.toFloat()
        var correctAnswers: Float = 0.toFloat()

        for (x in questionBank.indices) {
            Log.d(TAG, questionBank[x].correct.toString())
            if (questionBank[x].correct) {
                correctAnswers++
            }
            questionBank[x].answered = false
        }
        return (correctAnswers / totalQuestions) * 100
    }

}