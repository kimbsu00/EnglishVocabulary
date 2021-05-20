package com.mobileprogramming.englishvocabulary

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobileprogramming.englishvocabulary.adapter.FragmentWordsAdapter
import com.mobileprogramming.englishvocabulary.data.MyViewModel
import com.mobileprogramming.englishvocabulary.data.Word
import java.util.*
import kotlin.collections.ArrayList

class FragmentWords : Fragment() {

    // UI 변수 시작
    lateinit var rootView: View
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: FragmentWordsAdapter
    lateinit var searchView: SearchView
    lateinit var sortTextView: TextView
    lateinit var showKorTextView: TextView
    // UI 변수 끝

    val words: ArrayList<Word> = ArrayList()
    var tts: TextToSpeech? = null

    val myViewModel: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_words, container, false)

        init()

        return rootView
    }

    private fun init() {
        recyclerView = rootView.findViewById(R.id.wordsRecyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapter = FragmentWordsAdapter(words, words)
        adapter.setOnItemClickListener(object : FragmentWordsAdapter.OnItemClickListener {
            override fun onEngTextViewClick(v: View, eng: String) {
                tts?.speak(eng, TextToSpeech.QUEUE_ADD, null, null)
            }

            override fun onKorTextViewClick(v: View, pos: Int) {
                if (words[pos].isShowKor) {
                    words[pos].isShowKor = false
                } else {
                    words[pos].isShowKor = true
                }
                adapter.notifyDataSetChanged()
            }

            fun getMenuId(isFavorite: Boolean): Int {
                return when (isFavorite) {
                    false -> R.menu.word_add_favorite_items
                    true -> R.menu.word_delete_favorite_items
                }
            }

            override fun onWordLongClick(v: View, word: Word) {
                val popupMenu: PopupMenu = PopupMenu(context, v)
                popupMenu.menuInflater.inflate(
                    getMenuId(myViewModel.bookmark.value!!.contains(word)),
                    popupMenu.menu
                )

                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        if (item != null) {
                            when (item.itemId) {
                                R.id.addFavoriteMenu -> {
                                    if (myViewModel.addBookmark(word)) {
                                        // DB에 단어 저장
                                        myViewModel.myDBHelper!!.insertFavorite(word)
                                        Toast.makeText(
                                            context,
                                            "${word.eng} 이(가) 즐겨찾기에 추가되었습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                R.id.deleteFravoriteMenu -> {
                                    if (myViewModel.removeBookmark(word)) {
                                        // DB에 저장되어 있는 단어 삭제
                                        myViewModel.myDBHelper!!.deleteFavorite(word)
                                        Toast.makeText(
                                            context,
                                            "${word.eng} 이(가) 즐겨찾기에서 삭제되었습니다.",
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
        })
        recyclerView.adapter = adapter

        searchView = rootView.findViewById(R.id.searchView)
        searchView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                searchView.isIconified = false
            }

        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // 검색 버튼이 클릭되었을 때 이벤트 처리
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return true
            }

            // 검색어가 변경될 때 이벤트 처리
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        sortTextView = rootView.findViewById<TextView>(R.id.sortTextView)
        sortTextView.text =
            getText(R.string.sort).toString().plus(getText(R.string.sort_less).toString())
        sortTextView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val popupMenu: PopupMenu = PopupMenu(context, v)
                popupMenu.menuInflater.inflate(R.menu.popup_sort_items, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(object :
                    PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        if (item != null) {
                            when (item.itemId) {
                                R.id.sort_less -> {
                                    sortTextView.text = getText(R.string.sort).toString()
                                        .plus(getText(R.string.sort_less).toString())
                                    Collections.sort(words, object : Comparator<Word> {
                                        override fun compare(o1: Word?, o2: Word?): Int {
                                            return o1!!.eng.compareTo(o2!!.eng)
                                        }
                                    })
                                    adapter.notifyDataSetChanged()
                                }
                                R.id.sort_greater -> {
                                    sortTextView.text = getText(R.string.sort).toString()
                                        .plus(getText(R.string.sort_greater).toString())
                                    Collections.sort(words, object : Comparator<Word> {
                                        override fun compare(o1: Word?, o2: Word?): Int {
                                            return o2!!.eng.compareTo(o1!!.eng)
                                        }
                                    })
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                        return false
                    }
                })
                popupMenu.show()
            }
        })

        showKorTextView = rootView.findViewById<TextView>(R.id.showKorTextView)
        showKorTextView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val show: String = getText(R.string.show_kor).toString()
                val hide: String = getText(R.string.hide_kor).toString()
                when (showKorTextView.text.toString()) {
                    show -> {
                        for (word in words) {
                            word.isShowKor = true
                        }
                        showKorTextView.text = hide
                    }
                    hide -> {
                        for (word in words) {
                            word.isShowKor = false
                        }
                        showKorTextView.text = show
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })
    }
}