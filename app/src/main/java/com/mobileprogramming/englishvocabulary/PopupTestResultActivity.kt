package com.mobileprogramming.englishvocabulary

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.Window
import com.mobileprogramming.englishvocabulary.databinding.ActivityPopupTestResultBinding

class PopupTestResultActivity : Activity() {

    val COLOR_GOOD_SCORE: String = "#30A9DE"
    val COLOR_BAD_SCORE: String = "#E53A40"

    lateinit var binding: ActivityPopupTestResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopupTestResultBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.root)

        init()
    }

    fun init() {
        val answerNum: Int = intent.getIntExtra("ansNum", 0)
        val totalProblemNum: Int = intent.getIntExtra("size", 0)

        binding.apply {
            ansNum.text = answerNum.toString()
            if (answerNum < totalProblemNum / 2) {
                ansNum.setTextColor(Color.parseColor(COLOR_BAD_SCORE))
            } else {
                ansNum.setTextColor(Color.parseColor(COLOR_GOOD_SCORE))
            }
            totalNum.text = totalProblemNum.toString()

            confirmBtn.setOnClickListener {
                finish()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_OUTSIDE)
            return false
        return true
    }

    override fun onBackPressed() {
        return
    }
}