package com.example.andhack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject

class JoinActivity : AppCompatActivity() {
    lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val etJoinId = findViewById<EditText>(R.id.etJoinId)
        val etJoinPw = findViewById<EditText>(R.id.etJoinPw)
        val buttonjoin = findViewById<Button>(R.id.btnJoin2)

        queue= Volley.newRequestQueue(this@JoinActivity)

        buttonjoin.setOnClickListener {

            val joinId = etJoinId.text.toString()
            val joinPw = etJoinPw.text.toString()

            val mv = MemberVO(joinId, joinPw, null)
            val jsonRequest = JSONObject(Gson().toJson(mv))

            val request = object :StringRequest(
                Request.Method.POST,
                "http://192.168.219.51:8089/IZG/join",
                {
                        response ->
                    Intent(this, StartActivity::class.java)

                    startActivity(intent)
                    finishAffinity()
                },
                {
                        error ->

                }
            ){
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return jsonRequest.toString().toByteArray(Charsets.UTF_8)
                }
            }
            queue.add(request)
        }

    }
}