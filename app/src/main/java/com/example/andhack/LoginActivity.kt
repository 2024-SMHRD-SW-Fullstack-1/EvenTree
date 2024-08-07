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

class LoginActivity : AppCompatActivity() {

    lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etLoginId = findViewById<EditText>(R.id.etLoginId)
        val etLoginPw = findViewById<EditText>(R.id.etLoginPw)
        val loginButton = findViewById<Button>(R.id.btnLogin2)

        queue = Volley.newRequestQueue(this@LoginActivity)

        loginButton.setOnClickListener {
            val loginId = etLoginId.text.toString()
            val loginPw = etLoginPw.text.toString()

            val mv = MemberVO(loginId, loginPw, null)
            val jsonRequest = JSONObject(Gson().toJson(mv))

            val request = object: StringRequest(
                Request.Method.POST,
                "http://192.168.219.63:8089/IZG/login",
                {response ->
                    println("Response: $response")

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("id", loginId)
                    finishAffinity()
                    startActivity(intent)
                },
                {error ->
                    println("Error: ${error.message}")
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