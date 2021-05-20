package com.mobileprogramming.englishvocabulary

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobileprogramming.englishvocabulary.adapter.PopupTestResultDetailAdapter
import com.mobileprogramming.englishvocabulary.data.Problem
import com.mobileprogramming.englishvocabulary.data.Test
import com.mobileprogramming.englishvocabulary.data.Word
import com.mobileprogramming.englishvocabulary.databinding.ActivityPopupTestResultDetailBinding

class PopupTestResultDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityPopupTestResultDetailBinding
    lateinit var adapter: PopupTestResultDetailAdapter

    lateinit var test: Test
    val problems: ArrayList<Problem> = ArrayList()
    val addToBookmark: ArrayList<Word> = ArrayList()
    val bookmark: ArrayList<Word> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopupTestResultDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    fun init() {
        test = intent.getSerializableExtra("test") as Test
        for (problem in test.problems) {
            if (!problem.isCorrect) {
                problems.add(problem)
            }
        }
        bookmark.addAll(intent.getSerializableExtra("bookmark") as ArrayList<Word>)

        binding.apply {
            backBtn.setOnClickListener {
                val intent: Intent = Intent()
                intent.putExtra("addToBookmark", addToBookmark)
                setResult(RESULT_OK, intent)
                finish()
            }

            recyclerView.layoutManager = LinearLayoutManager(
                this@PopupTestResultDetailActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = PopupTestResultDetailAdapter(problems)
            adapter.listener = object : PopupTestResultDetailAdapter.OnItemClickListener {
                override fun onTextViewClick(v: View, word: Word) {
                    val popupMenu: PopupMenu = PopupMenu(this@PopupTestResultDetailActivity, v)
                    popupMenu.menuInflater.inflate(
                        R.menu.word_add_favorite_items,
                        popupMenu.menu
                    )

                    popupMenu.setOnMenuItemClickListener(object :
                        PopupMenu.OnMenuItemClickListener {
                        override fun onMenuItemClick(item: MenuItem?): Boolean {
                            if (item != null) {
                                when (item.itemId) {
                                    R.id.addFavoriteMenu -> {
                                        if (!bookmark.contains(word)) {
                                            addToBookmark.add(word)
                                            Toast.makeText(
                                                this@PopupTestResultDetailActivity,
                                                "${word.eng} 이(가) 즐겨찾기에 추가되었습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@PopupTestResultDetailActivity,
                                                "${word.eng} 는(은) 이미 즐겨찾기에 추가된 단어입니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                            return true
                        }
                    })
                    popupMenu.show()
                }
            }
            recyclerView.adapter = adapter

            val date = test.date.split('T')
            testDate.text = date[0] + "  " + date[1].split('.')[0]
            testType.text = when (test.testType) {
                Test.TEST_DEFAULT -> "TEST_DEFAULT"
                Test.TEST_MEANING -> getString(R.string.test_meaning_title)
                Test.TEST_WORD1 -> getString(R.string.test_word_title1)
                Test.TEST_WORD2 -> getString(R.string.test_word_title2)
                Test.TEST_LISTENING -> getString(R.string.test_listening_title)
                else -> "TEST_DEFAULT"
            }
            testSize.text = test.size.toString()
            testAnsNum.text = test.ansNum.toString()
            testWrongAnsNum.text = test.wrongAnsNum.toString()
        }
    }

    override fun onBackPressed() {
        val intent: Intent = Intent()
        intent.putExtra("addToBookmark", addToBookmark)
        setResult(RESULT_OK, intent)
        finish()
    }
}