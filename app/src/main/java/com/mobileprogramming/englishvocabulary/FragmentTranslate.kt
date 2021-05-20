package com.mobileprogramming.englishvocabulary

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mobileprogramming.englishvocabulary.databinding.FragmentTranslateBinding
import com.mobileprogramming.englishvocabulary.network.PapagoNMT

class FragmentTranslate : Fragment() {

    lateinit var binding: FragmentTranslateBinding
    var translateFlag: Int = PapagoNMT.ENG_TO_KOR

    val papagoNMT: PapagoNMT = PapagoNMT()
    val translateHandler: TranslateHandler = TranslateHandler()
    val myProgressBar: MyProgressBar = MyProgressBar()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTranslateBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    fun init() {
        binding.apply {
            translateBtn.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val input: String = inputEdit.text.toString()
                    if (input.isNotEmpty()) {
                        myProgressBar.progressON(activity, "번역중..")
                        val thread = TranslateThread(input, translateFlag)
                        thread.start()
                    } else {
                        Toast.makeText(
                            context,
                            "번역하고 싶은 문장을 먼저 입력해주세요!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })

            translateOption.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    if (translateFlag == PapagoNMT.ENG_TO_KOR) {
                        translateFlag = PapagoNMT.KOR_TO_ENG
                        translateOption.text = getString(R.string.translate_kor_to_eng)
                    } else {
                        translateFlag = PapagoNMT.ENG_TO_KOR
                        translateOption.text = getString(R.string.translate_eng_to_kor)
                    }
                }
            })
        }
    }

    inner class TranslateHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val bundle: Bundle = msg.data
            if (!bundle.isEmpty) {
                val isSuccess = bundle.getBoolean("isSuccess")
                if (isSuccess) {
                    binding.translateResult.text = papagoNMT.translateResult
                    binding.translateResult.setTextColor(Color.parseColor("#000000"))
                } else {
                    Log.e("TranslateHandler", "Translate Fail")
                    binding.translateResult.text = getText(R.string.translate_emtpy)
                    binding.translateResult.setTextColor(Color.parseColor("#9F9F9F"))
                }
            }
            myProgressBar.progressOFF()
        }
    }

    inner class TranslateThread(val input: String, val type: Int) : Thread() {
        override fun run() {
            val message: Message = translateHandler.obtainMessage()
            val bundle: Bundle = Bundle()

            val isSuccess = papagoNMT.translate(input, type)
            bundle.putBoolean("isSuccess", isSuccess)

            message.data = bundle
            translateHandler.sendMessage(message)
        }
    }
}