package com.mobileprogramming.englishvocabulary.network

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class PapagoNMT {

    companion object {
        val ENG_TO_KOR: Int = 100
        val KOR_TO_ENG: Int = 200
    }

    val apiURL: String = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation"
    val CLIENT_ID: String = "YOUR_CLIENT_ID"
    val CLIENT_SECRET: String = "YOUR_CLIENT_SECRET"

    var translateResult: String = ""

    fun translate(input: String, type: Int): Boolean {
        var outputText: String = ""
        try {
            val text = URLEncoder.encode(input, "UTF-8")
            val url = URL(apiURL)
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", CLIENT_ID)
            con.setRequestProperty("X-NCP-APIGW-API-KEY", CLIENT_SECRET)
            val postParams = when (type) {
                ENG_TO_KOR -> "source=en&target=ko&text="
                KOR_TO_ENG -> "source=ko&target=en&text="
                else -> ""
            } + text
            if (postParams == text)
                return false

            con.doOutput = true
            val wr = DataOutputStream(con.outputStream)
            wr.writeBytes(postParams)
            wr.flush()
            wr.close()
            val responseCode = con.responseCode
            val br: BufferedReader
            if (responseCode == 200) {
                br = BufferedReader(InputStreamReader(con.inputStream))
            } else {
                br = BufferedReader(InputStreamReader(con.errorStream))
            }
            var inputLine: String?
            val response = StringBuffer()
            while (br.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            br.close()

            outputText += response.toString()
        } catch (e: Exception) {
            Log.e("Translate Error", e.toString())
            return false
        }

        val json: JSONObject = JSONObject(outputText)
        val result: JSONObject = json.getJSONObject("message").getJSONObject("result")
        translateResult = result.getString("translatedText").trim()

        return true
    }

}
