package com.mobileprogramming.englishvocabulary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobileprogramming.englishvocabulary.R
import com.mobileprogramming.englishvocabulary.data.Word

class FragmentFavoritesAdapter(var items: ArrayList<Word>, var filteredItems: ArrayList<Word>) :
    RecyclerView.Adapter<FragmentFavoritesAdapter.ViewHolder>(), Filterable {

    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onEngTextViewClick(v: View, eng: String)
        fun onKorTextViewClick(v: View, pos: Int)
        fun onWordLongClick(v: View, word: Word)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val engTextView: TextView
        val korTextView: TextView
        val wordLayout: LinearLayout

        init {
            engTextView = itemView.findViewById(R.id.engTextView)
            korTextView = itemView.findViewById(R.id.korTextView)
            wordLayout = itemView.findViewById(R.id.wordLayout)

            engTextView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val pos: Int = adapterPosition
                    if (v != null) {
                        listener?.onEngTextViewClick(v, filteredItems[pos].eng)
                    }
                }
            })
            korTextView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val pos: Int = adapterPosition
                    if (v != null) {
                        listener?.onKorTextViewClick(v, pos)
                    }
                }
            })
            engTextView.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {
                    val pos: Int = adapterPosition
                    if (v != null) {
                        listener?.onWordLongClick(v, filteredItems[pos])
                        return true
                    }
                    return false
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_word, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.engTextView.text = filteredItems[position].eng

        if (filteredItems[position].isShowKor) {
            holder.korTextView.text = filteredItems[position].kor
        } else {
            holder.korTextView.text = "----"
        }
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val str: String = constraint.toString()
                val str_LowerCase: String = str.toLowerCase()

                if (str.isNullOrBlank()) {
                    filteredItems = items
                } else {
                    val filteringList: ArrayList<Word> = ArrayList()
                    for (word in items) {
                        val word_LowerCase: String = word.eng.toLowerCase()
                        if (word_LowerCase.contains(str_LowerCase)) {
                            filteringList.add(word)
                        }
                    }
                    filteredItems = filteringList
                }

                val result: FilterResults = FilterResults()
                result.values = filteredItems

                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null) {
                    filteredItems = results.values as ArrayList<Word>
                    notifyDataSetChanged()
                }
            }
        }
    }
}