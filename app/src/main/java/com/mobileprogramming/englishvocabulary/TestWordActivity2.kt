package com.mobileprogramming.englishvocabulary

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobileprogramming.englishvocabulary.data.Test
import com.mobileprogramming.englishvocabulary.data.Word
import com.mobileprogramming.englishvocabulary.databinding.ActivityTestWord2Binding
import java.util.*
import kotlin.collections.HashMap

class TestWordActivity2 : AppCompatActivity() {

    lateinit var binding: ActivityTestWord2Binding
    lateinit var test: Test

    val map: HashMap<Int, Word> = HashMap()
    var problemIndex: Int = 0

    val FINISH_INTERVAL_TIME: Long = 2000
    var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestWord2Binding.inflate(layoutInflater)
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
            problemMeaning.text = problem.answer.kor

            val pIndex: Int = problemIndex - 1
            nextBtn.setOnClickListener {
                val userAns: String = answerEdit.text.toString()

                if (userAns.isBlank()) {
                    Toast.makeText(this@TestWordActivity2, "정답을 입력해주세요!", Toast.LENGTH_SHORT).show()
                } else {
                    if (userAns.toLowerCase() == test.problems[pIndex].answer.eng.toLowerCase()) {
                        test.problems[pIndex].isCorrect = true
                    } else {
                        test.problems[pIndex].isCorrect = false
                        test.problems[pIndex].wrongAnswer =
                            Word(
                                userAns,
                                test.problems[pIndex].answer.kor,
                                false,
                                test.problems[pIndex].answer.isFavorite
                            )
                    }

                    if (problemIndex < test.size) {
                        setProblem(problemIndex++)
                    } else {
                        finishTest()
                    }
                }
            }

            passBtn.setOnClickListener {
                test.problems[pIndex].isCorrect = false
                test.problems[pIndex].wrongAnswer = Word("", "", false, false)

                if (problemIndex < test.size) {
                    setProblem(problemIndex++)
                } else {
                    finishTest()
                }
            }
        }
    }

    fun finishTest() {
        test.finishTest()

        val intent = Intent()
        intent.putExtra("test", test)
        setResult(RESULT_OK, intent)
        finish()
    }
}