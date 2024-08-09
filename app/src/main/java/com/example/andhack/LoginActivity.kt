package com.example.andhack

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import android.text.method.PasswordTransformationMethod
import android.widget.TextView

class LoginActivity : AppCompatActivity() {

    lateinit var queue: RequestQueue
    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etLoginId = findViewById<EditText>(R.id.etLoginId)
        val etLoginPw = findViewById<EditText>(R.id.etLoginPw)
        val loginButton = findViewById<Button>(R.id.btnLogin2)

        val btnTogglePw = findViewById<ImageView>(R.id.btnTogglePassword)
        val btnJoinToLogin = findViewById<TextView>(R.id.btnJoinToLogin)

        // 비밀번호 토글 기능 설정
        btnTogglePw.setOnClickListener {
            if (isPasswordVisible) {
                etLoginPw.transformationMethod = PasswordTransformationMethod.getInstance()
                btnTogglePw.setImageResource(R.drawable.ic_visibility_off)
            } else {
                etLoginPw.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnTogglePw.setImageResource(R.drawable.ic_visibility)
            }
            isPasswordVisible = !isPasswordVisible
            etLoginPw.setSelection(etLoginPw.text.length) // 커서를 텍스트 끝으로 이동
        }

        btnJoinToLogin.setOnClickListener{
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }

        queue = Volley.newRequestQueue(this@LoginActivity)

        loginButton.setOnClickListener {
            val loginId = etLoginId.text.toString()
            val loginPw = etLoginPw.text.toString()

            val mv = MemberVO(loginId, loginPw, null,null)
            val jsonRequest = JSONObject(Gson().toJson(mv))

            val request = object: StringRequest(
                Request.Method.POST,
                "http://192.168.219.63:8089/IZG/login",
                {response ->
                    println("Response: $response")

                    //JWT 토큰을 SharedPreferences에 저장
                    val token = SharedPrefManager.saveToken(this, response)
                    Log.d("token",token.toString())
                    if(response == "fail"){
                        println("Login failed")
                        ToastUtil.showFailureToast(this,"비밀번호를 확인해보세요")
                    }else{
                        //JWT 토큰을 SharedPreferences에 저장
                        SharedPrefManager.saveToken(this, response)

                        val intent = Intent(this, MainActivity::class.java)
                        finishAffinity()
                        startActivity(intent)
                        ToastUtil.showSuccessToast(this,"로그인 성공")
                    }

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