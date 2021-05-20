package com.mobileprogramming.englishvocabulary.data

import java.io.Serializable

data class Problem(
    var answer: Word,
    var others: ArrayList<Word>,
    var isCorrect: Boolean = false,
    var wrongAnswer: Word? = null
) : Serializable