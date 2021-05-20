package com.mobileprogramming.englishvocabulary.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobileprogramming.englishvocabulary.db.MyDBHelper

class MyViewModel : ViewModel() {
    val words: MutableLiveData<ArrayList<Word>> = MutableLiveData<ArrayList<Word>>()
    val bookmark: MutableLiveData<ArrayList<Word>> = MutableLiveData<ArrayList<Word>>()
    val testRecord: MutableLiveData<ArrayList<Test>> = MutableLiveData<ArrayList<Test>>()
    var myDBHelper: MyDBHelper? = null

    fun setLiveDataWord(words: ArrayList<Word>) {
        this.words.value = words
    }

    fun setLiveDataBookmark(bookmark: ArrayList<Word>) {
        this.bookmark.value = bookmark
    }

    fun setLiveDataTestRecord(testRecord: ArrayList<Test>) {
        this.testRecord.value = testRecord
    }

    fun addBookmark(word: Word): Boolean {
        if (bookmark.value!!.contains(word))
            return false
        return bookmark.value!!.add(word)
    }

    fun removeBookmark(word: Word): Boolean {
        if (!bookmark.value!!.contains(word))
            return false
        return bookmark.value!!.remove(word)
    }

    fun addTestRecord(test: Test) {
        testRecord.value?.add(test)
    }
}