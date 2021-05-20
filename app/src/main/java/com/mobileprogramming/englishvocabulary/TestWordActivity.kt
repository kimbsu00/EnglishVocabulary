package com.mobileprogramming.englishvocabulary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobileprogramming.englishvocabulary.data.Test
import com.mobileprogramming.englishvocabulary.data.Word
import com.mobileprogramming.englishvocabulary.databinding.ActivityTestMeaningBinding
import java.util.*
import kotlin.collections.HashMap

class TestWordActivity : AppCompatActivity() {

    lateinit var binding: ActivityTestMeaningBinding
    lateinit var test: Test

    val map: HashMap<Int, Word> = HashMap()
    var problemIndex: Int = 0

    val FINISH_INTERVAL_TIME: Long = 2000
    var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestMeaningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    override fun onBackPressed() {
        val tempTime: Long = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime

        if (0 <= intervalTime && intervalTime <= FINISH_INTERVAL_TIME) {
            super.onBackPressed()
        } else {
            backPressedTime = tempTime
            val msg: String = "뒤로 가기를 한 번 더 누르면 테스트가 중단됩니다."
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun init() {
        test = intent.getSerializableExtra("test") as Test

        binding.apply {
            answer1.setOnClickListener(AnswerBtnHandler())
            answer2.setOnClickListener(AnswerBtnHandler())
            answer3.setOnClickListener(AnswerBtnHandler())
            answer4.setOnClickListener(AnswerBtnHandler())
            answer5.setOnClickListener(AnswerBtnHandler())
        }

        setProblem(problemIndex++)
    }

    fun setProblem(index: Int) {
        map.clear()

        val problem = test.problems[index]
        val random = Random()
        val ansIndex = random.nextInt(5) + 1
        map.put(ansIndex, problem.answer)
        var num = 0
        for (i in 1..5) {
            if (i == ansIndex)
                continue
            map.put(i, problem.others[num++])
        }

        binding.apply {
            problemNumber.text = (index + 1).toString() + "/" + test.problems.size.toString()
            problemWord.text = problem.answer.kor

            answer1.text = map.get(1)?.eng
            answer2.text = map.get(2)?.eng
            answer3.text = map.get(3)?.eng
            answer4.text = map.get(4)?.eng
            answer5.text = map.get(5)?.eng
        }
    }

    fun finishTest() {
        test.finishTest()

        val intent = Intent()
        intent.putExtra("test", test)
        setResult(RESULT_OK, intent)
        finish()
    }

    inner class AnswerBtnHandler : View.OnClickListener {
        override fun onClick(v: View?) {
            if (v != null) {
                val viewId = v.id

                val index = problemIndex - 1
                val btnIndex = when (viewId) {
                    R.id.answer1 -> 1
                    R.id.answer2 -> 2
                    R.id.answer3 -> 3
                    R.id.answer4 -> 4
                    R.id.answer5 -> 5
                    else -> 1
                }
                if (map.get(btnIndex) == test.problems[index].answer) {
                    test.problems[index].isCorrect = true
                } else {
                    test.problems[index].isCorrect = false
                    test.problems[index].wrongAnswer = map.get(btnIndex)
                }

                if (problemIndex < test.size) {
                    setProblem(problemIndex++)
                } else {
                    finishTest()
                }
            }
        }


    }
}