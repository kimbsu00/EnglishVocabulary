package com.mobileprogramming.englishvocabulary

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mobileprogramming.englishvocabulary.data.MyViewModel
import com.mobileprogramming.englishvocabulary.data.Word
import com.mobileprogramming.englishvocabulary.db.MyDBHelper
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    // Fragment 변수 시작
    val fragmentFavorites: Fragment = FragmentFavorites()
    val fragmentWords: Fragment = FragmentWords()
    val fragmentTest: Fragment = FragmentTest()
    val fragmentResult: Fragment = FragmentResult()
    val fragmentTranslate: Fragment = FragmentTranslate()
    // Fragment 변수 종료

    // UI 변수 시작
    lateinit var frameLayout: FrameLayout;
    lateinit var bottomNavBar: BottomNavigationView
    // UI 변수 끝

    val myViewModel: MyViewModel by viewModels<MyViewModel>()

    val words: ArrayList<Word> = ArrayList()
    lateinit var tts: TextToSpeech
    var isTtsReady: Boolean = false

    val FINISH_INTERVAL_TIME: Long = 2000
    var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initDB()
        initData()
        init()
        initTTS()
    }

    override fun onStop() {
        super.onStop()
        tts.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }

    override fun onBackPressed() {
        val tempTime: Long = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime

        if (0 <= intervalTime && intervalTime <= FINISH_INTERVAL_TIME) {
            super.onBackPressed()
        } else {
            backPressedTime = tempTime
            val msg: String = "뒤로 가기를 한 번 더 누르면 종료됩니다."
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun initDB() {
        myViewModel.myDBHelper = MyDBHelper(this)
        myViewModel.setLiveDataBookmark(myViewModel.myDBHelper!!.getFavorites())
        myViewModel.setLiveDataTestRecord(myViewModel.myDBHelper!!.getTests())
    }

    fun initData() {
        val scan: Scanner = Scanner(resources.openRawResource(R.raw.words))
        while (scan.hasNextLine()) {
            val eng = scan.nextLine().trim()
            val kor = scan.nextLine().trim()
            var isFavorite = false
            for (word in myViewModel.bookmark.value!!) {
                if (word.eng == eng) {
                    isFavorite = true
                    break
                }
            }
            words.add(Word(eng, kor, false, isFavorite))
        }
        Collections.sort(words, object : Comparator<Word> {
            override fun compare(o1: Word?, o2: Word?): Int {
                return o1!!.eng.compareTo(o2!!.eng)
            }
        })
        (fragmentWords as FragmentWords).words.addAll(this.words)

        myViewModel.setLiveDataWord(words)
    }

    fun init() {

        frameLayout = findViewById(R.id.frameLayout)
        replaceFragment(FragmentFavorites())

        bottomNavBar = findViewById(R.id.bottomNavBar)
        bottomNavBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_favorites -> {
                    this.words.clear()
                    this.words.addAll((fragmentWords as FragmentWords).words)
                    replaceFragment(fragmentFavorites)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_words -> {
                    (fragmentWords as FragmentWords).words.clear()
                    (fragmentWords as FragmentWords).words.addAll(this.words)
                    replaceFragment(fragmentWords)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_test -> {
                    replaceFragment(fragmentTest)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_result -> {
                    replaceFragment(fragmentResult)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_translate -> {
                    replaceFragment(fragmentTranslate)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }
    }

    fun initTTS() {
        tts = TextToSpeech(this, TextToSpeech.OnInitListener {
            isTtsReady = true
            tts.language = Locale.US
            (fragmentWords as FragmentWords).tts = tts
            (fragmentFavorites as FragmentFavorites).tts = tts
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

}