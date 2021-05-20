package com.mobileprogramming.englishvocabulary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobileprogramming.englishvocabulary.R
import com.mobileprogramming.englishvocabulary.data.Test

class FragmentResultAdapter(var items: ArrayList<Test>) :
    RecyclerView.Adapter<FragmentResultAdapter.ViewHolder>() {

    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onTextViewClick(v: View, test: Test)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val testTime: TextView
        val testInfo: TextView

        init {
            testTime = itemView.findViewById(R.id.testTime)
            testInfo = itemView.findViewById(R.id.testInfo)

            testTime.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val pos: Int = adapterPosition
                    if (v != null) {
                        listener?.onTextViewClick(v, items[pos])
                    }
                }
            })
            testInfo.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val pos: Int = adapterPosition
                    if (v != null) {
                        listener?.onTextViewClick(v, items[pos])
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_result, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var time: String = ""
        val temp = items[position].date.split('T')
        time += temp[0] + "  " + temp[1].split('.')[0] + " 테스트 완료"
        holder.testTime.text = time

        val info: String =
            "문제 수 : " + items[position].size.toString() + "\n정답 : " + items[position].ansNum.toString() + "\n오답 : " + items[position].wrongAnsNum.toString()
        holder.testInfo.text = info
    }

    override fun getItemCount(): Int {
        return items.size
    }
}