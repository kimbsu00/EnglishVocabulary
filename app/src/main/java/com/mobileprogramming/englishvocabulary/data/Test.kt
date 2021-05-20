package com.mobileprogramming.englishvocabulary.data

import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class Test : Serializable {

    companion object {
        val TEST_DEFAULT = -1
        val TEST_MEANING: Int = 1
        val TEST_WORD1: Int = 2
        val TEST_WORD2: Int = 3
        val TEST_LISTENING: Int = 4
    }

    var problems: ArrayList<Problem> = ArrayList<Problem>()
    var ansNum: Int = -1
    var wrongAnsNum: Int = -1
    var size: Int = 30
    var date: String = ""
    var testType: Int = TEST_DEFAULT

    fun makeTest(words: ArrayList<Word>) {
        problems.clear()

        if (words.size < 30) {
            size = words.size
        }

        // 문제별로 정답 생성
        val answers: ArrayList<Word> = ArrayList<Word>()
        val random = Random()
        val max: Int = if (words.size - size == 0) size else words.size - size
        val start = random.nextInt(max)
        for (i in 1..size) {
            answers.add(words[start + i - 1])
        }

        // 문제별로 선택지 생성
        for (i in 1..size) {
            words.remove(answers[i - 1])

            val others = ArrayList<Word>()
            for (j in 0..3) {
                val num = random.nextInt(words.size)
                others.add(words[num])
                words.remove(others[j])
            }
            problems.add(Problem(answers[i - 1], others))

            words.addAll(others)
            words.add(answers[i - 1])
        }
    }

    fun makeRandomTest(words: ArrayList<Word>) {
        problems.clear()

        if (words.size < 30) {
            size = words.size
        }

        val random = Random()
        // 문제별로 정답 생성
        val answers: ArrayList<Word> = ArrayList<Word>()
        for (i in 1..size) {
            val num = random.nextInt(words.size)
            answers.add(words[num])
            words.remove(answers[i - 1])
        }
        for (i in 1..size) {
            words.add(answers[i - 1])
        }

        // 문제별로 선택지 생성
        for (i in 1..size) {
            words.remove(answers[i - 1])

            val others = ArrayList<Word>()
            for (j in 0..3) {
                val num = random.nextInt(words.size)
                others.add(words[num])
                words.remove(others[j])
            }
            problems.add(Problem(answers[i - 1], others))

            words.addAll(others)
            words.add(answers[i - 1])
        }
    }

    fun finishTest() {
        ansNum = 0
        wrongAnsNum = 0

        for (i in 0..size - 1) {
            if (problems[i].isCorrect) {
                ansNum += 1
            } else {
                wrongAnsNum += 1
            }
        }

        val current = LocalDateTime.now()
        // yyyy-MM-ddThh:mm:ss 형태로 날짜를 저장한다.
        // https://developer.android.com/reference/java/time/format/DateTimeFormatter#ISO_LOCAL_DATE_TIME
        val formatted = current.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        date = formatted
    }
}