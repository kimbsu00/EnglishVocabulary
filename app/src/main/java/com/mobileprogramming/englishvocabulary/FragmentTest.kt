package com.mobileprogramming.englishvocabulary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mobileprogramming.englishvocabulary.data.MyViewModel
import com.mobileprogramming.englishvocabulary.data.Test
import com.mobileprogramming.englishvocabulary.databinding.FragmentTestBinding

class FragmentTest : Fragment() {

    val SELECT_WORD_All_WORD: Int = 1
    val SELECT_WORD_FAVORITE_WORD: Int = 2
    val TEST_METHOD_NOT_SHUFFLE: Int = 3
    val TEST_METHOD_DO_SHUFFLE: Int = 4

    lateinit var getContent: ActivityResultLauncher<Intent>

    // UI 변수 시작
    lateinit var binding: FragmentTestBinding
    // UI 변수 끝

    val myViewModel: MyViewModel by activityViewModels()
    var selectWordFlag: Int = SELECT_WORD_All_WORD
    var testMethodFlag: Int = TEST_METHOD_NOT_SHUFFLE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTestBinding.inflate(layoutInflater)

        getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.data != null) {
                val test: Test = it.data!!.getSerializableExtra("test") as Test
                myViewModel.addTestRecord(test)
                myViewModel.myDBHelper!!.insertTest(test)

                val intent: Intent = Intent(context, PopupTestResultActivity::class.java)
                intent.putExtra("ansNum", test.ansNum)
                intent.putExtra("size", test.size)
                startActivity(intent)
            }
        }

        init()

        return binding.root
    }

    fun init() {
        binding.apply {
            selectWordBtn.setOnClickListener {
                val popupMenu: PopupMenu = PopupMenu(context, selectWordBtn)
                popupMenu.menuInflater.inflate(
                    R.menu.test_menu_1,
                    popupMenu.menu
                )

                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        if (item != null) {
                            when (item.itemId) {
                                R.id.allWord -> {
                                    selectWordFlag = SELECT_WORD_All_WORD
                                    binding.selectWordInfo.text = getString(R.string.all_word)
                                }
                                R.id.favoriteWord -> {
                                    if (myViewModel.bookmark.value!!.isNotEmpty()) {
                                        if (myViewModel.bookmark.value!!.size > 9) {
                                            selectWordFlag = SELECT_WORD_FAVORITE_WORD
                                            binding.selectWordInfo.text =
                                                getString(R.string.favorite_word)
                                        } else {
                                            selectWordFlag = SELECT_WORD_All_WORD
                                            binding.selectWordInfo.text =
                                                getString(R.string.all_word)
                                            Toast.makeText(
                                                context,
                                                "즐겨찾기 단어가 10개 이상이어야 가능합니다!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        selectWordFlag = SELECT_WORD_All_WORD
                                        binding.selectWordInfo.text = getString(R.string.all_word)
                                        Toast.makeText(
                                            context,
                                            "즐겨찾기 단어를 먼저 추가해주세요!",
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
            testMethodBtn.setOnClickListener {
                val popupMenu: PopupMenu = PopupMenu(context, testMethodBtn)
                popupMenu.menuInflater.inflate(
                    R.menu.test_menu_2,
                    popupMenu.menu
                )

                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        if (item != null) {
                            when (item.itemId) {
                                R.id.notShuffle -> {
                                    testMethodFlag = TEST_METHOD_NOT_SHUFFLE
                                    binding.testMethodInfo.text = getString(R.string.not_shuffle)
                                }
                                R.id.doShuffle -> {
                                    testMethodFlag = TEST_METHOD_DO_SHUFFLE
                                    binding.testMethodInfo.text = getString(R.string.do_shuffle)
                                }
                            }
                        }
                        return true
                    }
                })
                popupMenu.show()
            }
            testMeaningTitle.setOnClickListener(TestViewHandler())
            testMeaningInfo.setOnClickListener(TestViewHandler())
            testWordTitle1.setOnClickListener(TestViewHandler())
            testWordInfo1.setOnClickListener(TestViewHandler())
            testWordTitle2.setOnClickListener(TestViewHandler())
            testWordInfo2.setOnClickListener(TestViewHandler())
            testListeningTitle.setOnClickListener(TestViewHandler())
            testListeningInfo.setOnClickListener(TestViewHandler())
        }
    }

    inner class TestViewHandler : View.OnClickListener {
        override fun onClick(v: View?) {
            if (v != null) {
                val viewId = v.id

                when (viewId) {
                    R.id.testMeaningTitle, R.id.testMeaningInfo -> {
                        onTestMeaningClick()
                    }
                    R.id.testWordTitle1, R.id.testWordInfo1 -> {
                        onTestWord1Click()
                    }
                    R.id.testWordTitle2, R.id.testWordInfo2 -> {
                        onTestWord2Click()
                    }
                    R.id.testListeningTitle, R.id.testListeningInfo -> {
                        onTestListeningClick()
                    }
                    else -> {
                        Log.e("TEST ERROR", "TestViewHandler Error Occur")
                    }
                }

            }
        }

        fun onTestMeaningClick() {
            val test: Test = Test()
            test.testType = Test.TEST_MEANING

            val words = when (selectWordFlag) {
                SELECT_WORD_All_WORD -> myViewModel.words.value
                SELECT_WORD_FAVORITE_WORD -> myViewModel.bookmark.value
                else -> myViewModel.words.value
            }
            when (testMethodFlag) {
                TEST_METHOD_NOT_SHUFFLE -> test.makeTest(words!!)
                TEST_METHOD_DO_SHUFFLE -> test.makeRandomTest(words!!)
            }

            val intent: Intent = Intent(context, TestMeaningActivity::class.java)
            intent.putExtra("test", test)
            getContent.launch(intent)
        }

        fun onTestWord1Click() {
            val test: Test = Test()
            test.testType = Test.TEST_WORD1

            val words = when (selectWordFlag) {
                SELECT_WORD_All_WORD -> myViewModel.words.value
                SELECT_WORD_FAVORITE_WORD -> myViewModel.bookmark.value
                else -> myViewModel.words.value
            }
            when (testMethodFlag) {
                TEST_METHOD_NOT_SHUFFLE -> test.makeTest(words!!)
                TEST_METHOD_DO_SHUFFLE -> test.makeRandomTest(words!!)
            }

            val intent: Intent = Intent(context, TestWordActivity::class.java)
            intent.putExtra("test", test)
            getContent.launch(intent)
        }

        fun onTestWord2Click() {
            val test: Test = Test()
            test.testType = Test.TEST_WORD2

            val words = when (selectWordFlag) {
                SELECT_WORD_All_WORD -> myViewModel.words.value
                SELECT_WORD_FAVORITE_WORD -> myViewModel.bookmark.value
                else -> myViewModel.words.value
            }
            when (testMethodFlag) {
                TEST_METHOD_NOT_SHUFFLE -> test.makeTest(words!!)
                TEST_METHOD_DO_SHUFFLE -> test.makeRandomTest(words!!)
            }

            val intent: Intent = Intent(context, TestWordActivity2::class.java)
            intent.putExtra("test", test)
            getContent.launch(intent)
        }

        fun onTestListeningClick() {
            val test: Test = Test()
            test.testType = Test.TEST_LISTENING

            val words = when (selectWordFlag) {
                SELECT_WORD_All_WORD -> myViewModel.words.value
                SELECT_WORD_FAVORITE_WORD -> myViewModel.bookmark.value
                else -> myViewModel.words.value
            }
            when (testMethodFlag) {
                TEST_METHOD_NOT_SHUFFLE -> test.makeTest(words!!)
                TEST_METHOD_DO_SHUFFLE -> test.makeRandomTest(words!!)
            }

            val intent: Intent = Intent(context, TestListeningActivity::class.java)
            intent.putExtra("test", test)
            getContent.launch(intent)
        }
    }
}