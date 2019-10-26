package com.jcaseydev.geoquiz

import androidx.annotation.StringRes

data class Question(
    @StringRes val textResId: Int,
    val answer: Boolean,
    var answered: Boolean = false,
    var correct: Boolean = false
)