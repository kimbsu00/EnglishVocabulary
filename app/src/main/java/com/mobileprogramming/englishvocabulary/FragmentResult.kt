package com.mobileprogramming.englishvocabulary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobileprogramming.englishvocabulary.adapter.FragmentResultAdapter
import com.mobileprogramming.englishvocabulary.data.MyViewModel
import com.mobileprogramming.englishvocabulary.data.Test
import com.mobileprogramming.englishvocabulary.data.Word
import com.mobileprogramming.englishvocabulary.databinding.FragmentResultBinding

class FragmentResult : Fragment() {

    val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.data != null) {
            val addToBookmark: ArrayList<Word> =
                it.data!!.getSerializableExtra("addToBookmark") as ArrayList<Word>
            for (word in addToBookmark) {
                if (myViewModel.addBookmark(word)) {
                    myViewModel.myDBHelper!!.insertFavorite(word)
                }
            }
        }
    }

    lateinit var binding: FragmentResultBinding

    lateinit var adapter: FragmentResultAdapter
    val tests: ArrayList<Test> = ArrayList()

    val myViewModel: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    fun init() {
        myViewModel.testRecord.observe(viewLifecycleOwner, Observer {
            tests.clear()
            tests.addAll(myViewModel.testRecord.value!!)
            adapter.notifyDataSetChanged()
        })

        binding.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = FragmentResultAdapter(tests)
            adapter.listener = object : FragmentResultAdapter.OnItemClickListener {
                override fun onTextViewClick(v: View, test: Test) {
                    val intent: Intent = Intent(context, PopupTestResultDetailActivity::class.java)
                    intent.putExtra("test", test)
                    intent.putExtra("bookmark", myViewModel.bookmark.value!!)
                    getContent.launch(intent)
                }
            }
            recyclerView.adapter = adapter
        }
    }

}