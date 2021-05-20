package com.mobileprogramming.englishvocabulary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobileprogramming.englishvocabulary.R
import com.mobileprogramming.englishvocabulary.data.Problem
import com.mobileprogramming.englishvocabulary.data.Word

class PopupTestResultDetailAdapter(var items: ArrayList<Problem>) :
    RecyclerView.Adapter<PopupTestResultDetailAdapter.ViewHolder>() {

    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onTextViewClick(v: View, word: Word)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val answer: TextView
        val wrongAnswer: TextView

        init {
            answer = itemView.findViewById(R.id.answer)
            wrongAnswer = itemView.findViewById(R.id.wrongAnswer)

            answer.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val pos: Int = adapterPosition
                    if (v != null) {
                        listener?.onTextViewClick(v!!, items[pos].answer)
                    }
                }
            })
            wrongAnswer.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val pos: Int = adapterPosition
                    if (v != null) {
                        listener?.onTextViewClick(v!!, items[pos].wrongAnswer!!)
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_result_detail, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.answer.text = items[position].answer.eng + " / " + items[position].answer.kor
        holder.wrongAnswer.text =
            items[position].wrongAnswer!!.eng + " / " + items[position].wrongAnswer!!.kor
    }

    override fun getItemCount(): Int {
        return items.size
    }
}