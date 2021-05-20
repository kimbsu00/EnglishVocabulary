package com.mobileprogramming.englishvocabulary.data

import java.io.Serializable

data class Word(val eng: String, val kor: String, var isShowKor: Boolean, var isFavorite: Boolean) : Serializable