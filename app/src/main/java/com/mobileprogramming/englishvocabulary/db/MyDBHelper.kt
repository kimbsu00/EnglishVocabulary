package com.mobileprogramming.englishvocabulary.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mobileprogramming.englishvocabulary.data.Problem
import com.mobileprogramming.englishvocabulary.data.Test
import com.mobileprogramming.englishvocabulary.data.Word
import org.json.JSONArray
import org.json.JSONObject

class MyDBHelper(val context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        val DB_NAME = "mydb.db"
        val DB_VERSION = 1
        val TABLE_NAME = arrayOf("favorite", "test")

        // about Favorites
        val ENG = "eng"
        val KOR = "kor"

        // about Test
        val DATE = "date"
        val PROBLEMS = "problems"
        val ANSNUM = "ansnum"
        val SIZE = "size"
        val TYPE = "type"
    }

    fun insertFavorite(word: Word): Boolean {
        val values = ContentValues()
        values.put(ENG, word.eng)
        values.put(KOR, word.kor)

        val db = writableDatabase
        val ret = db.insert(TABLE_NAME[0], null, values) > 0
        db.close()
        return ret
    }

    fun deleteFavorite(word: Word): Boolean {
        val strsql = "select * from ${TABLE_NAME[0]} where $ENG='${word.eng}';"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count != 0
        if (flag) {
            cursor.moveToFirst()
            db.delete(TABLE_NAME[0], "$ENG=?", arrayOf(word.eng))
        }
        cursor.close()
        db.close()
        return flag
    }

    fun getFavorites(): ArrayList<Word> {
        val ret = ArrayList<Word>()

        val strsql = "select * from ${TABLE_NAME[0]};"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()
        if (cursor.count != 0) {
            do {
                val eng = cursor.getString(0)
                val kor = cursor.getString(1)
                ret.add(Word(eng, kor, false, true))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return ret
    }

    fun insertTest(test: Test): Boolean {
        val values = ContentValues()
        values.put(DATE, test.date)
        val problems: String = createJSONArray(test.problems)
        values.put(PROBLEMS, problems)
        values.put(ANSNUM, test.ansNum)
        values.put(SIZE, test.size)
        values.put(TYPE, test.testType)

        val db = writableDatabase
        val ret = db.insert(TABLE_NAME[1], null, values) > 0
        db.close()
        return ret
    }

    fun getTests(): ArrayList<Test> {
        val ret = ArrayList<Test>()

        val strsql = "select * from ${TABLE_NAME[1]};"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()
        if (cursor.count != 0) {
            do {
                val date = cursor.getString(0)
                val problems = cursor.getString(1)
                val ansNum = cursor.getInt(2)
                val size = cursor.getInt(3)
                val type = cursor.getInt(4)

                val test = Test()
                test.date = date
                test.problems = createProblemsFromJSONString(problems)
                test.ansNum = ansNum
                test.size = size
                test.testType = type
                test.wrongAnsNum = size - ansNum
                ret.add(test)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return ret
    }

    fun createJSONArray(problems: ArrayList<Problem>): String {
        val jsonArray: JSONArray = JSONArray()
        for (i in problems.indices) {
            val jsonObject = JSONObject()
            jsonObject.put("answer", problems[i].answer.eng + "/" + problems[i].answer.kor)
            val others: String = problems[i].others[0].eng + "/" + problems[i].others[0].kor + "@" +
                    problems[i].others[1].eng + "/" + problems[i].others[1].kor + "@" +
                    problems[i].others[2].eng + "/" + problems[i].others[2].kor + "@" +
                    problems[i].others[3].eng + "/" + problems[i].others[3].kor

            jsonObject.put("others", others)
            jsonObject.put("isCorrect", problems[i].isCorrect.toString())
            if (problems[i].isCorrect) {
                jsonObject.put("wrongAnswer", "")
            } else {
                jsonObject.put(
                    "wrongAnswer",
                    problems[i].wrongAnswer!!.eng + "/" + problems[i].wrongAnswer!!.kor
                )
            }
            jsonArray.put(jsonObject)
        }
        val ret: JSONObject = JSONObject()
        ret.put("problems", jsonArray)
        return ret.toString()
    }

    fun createProblemsFromJSONString(jsonString: String): ArrayList<Problem> {
        val ret = ArrayList<Problem>()
        val jsonObject = JSONObject(jsonString)
        val problems = jsonObject.getJSONArray("problems")
        for (i in 0..problems.length() - 1) {
            val problem = problems.getJSONObject(i)
            // answer 파싱
            val answerTemp = problem.getString("answer").split('/')
            val answer = Word(answerTemp[0], answerTemp[1], false, false)
            // others 파싱
            val othersTemp = problem.getString("others").split('@')
            val others = ArrayList<Word>()
            for (str in othersTemp) {
                val temp = str.split('/')
                others.add(Word(temp[0], temp[1], false, false))
            }
            // isCorrect 파싱
            val isCorrectTemp = problem.getString("isCorrect")
            val isCorrect = if (isCorrectTemp == "true") true else false
            // wrongAnswer 파싱
            val wrongAnswerTemp = problem.getString("wrongAnswer").split('/')
            val wrongAnswer = when (isCorrect) {
                true -> {
                    Word("", "", false, false)
                }
                false -> {
                    Word(wrongAnswerTemp[0], wrongAnswerTemp[1], false, false)
                }
            }
            ret.add(Problem(answer, others, isCorrect, wrongAnswer))
        }

        return ret
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable1 =
            "create table if not exists ${TABLE_NAME[0]}($ENG text primary key, $KOR text)"
        val createTable2 =
            "create table if not exists ${TABLE_NAME[1]}($DATE text primary key, $PROBLEMS text, $ANSNUM integer, $SIZE integer, $TYPE integer)"
        db?.execSQL(createTable1)
        db?.execSQL(createTable2)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTable1 = "drop table if exists ${TABLE_NAME[0]}"
        val dropTable2 = "drop table if exists ${TABLE_NAME[1]}"
        db?.execSQL(dropTable1)
        db?.execSQL(dropTable2)
        onCreate(db)
    }

}