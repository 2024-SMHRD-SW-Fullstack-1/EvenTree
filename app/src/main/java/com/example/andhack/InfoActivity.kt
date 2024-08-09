package com.example.andhack

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.nio.charset.Charset

class InfoActivity : AppCompatActivity() {
    lateinit var queue: RequestQueue

    private lateinit var etNick: EditText
    private lateinit var btnNickChange: Button
    lateinit var tvGetNick : TextView
    lateinit var tvGetWord : TextView
    private lateinit var etPw: EditText
    private lateinit var etPwConfirm: EditText
    private lateinit var prePw: EditText
    private lateinit var btnPwChange: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        etNick = findViewById(R.id.etNick)
        btnNickChange = findViewById(R.id.btnNickChange)
        etPw = findViewById(R.id.etPw)
        etPwConfirm = findViewById(R.id.etPwConfirm)
        prePw = findViewById(R.id.prePw)
        btnPwChange = findViewById(R.id.btnPwChange)


        // 토큰에서 닉네임 가져오기
        val token = SharedPrefManager.getToken(this)
        val nickname = extractNickFromToken(token ?: "")
        etNick.setText(nickname)

        queue = Volley.newRequestQueue(this@InfoActivity)

        btnNickChange.setOnClickListener {
            val newNick = etNick.text.toString().trim()
            if (newNick.isNotEmpty()) {
                sendNickChangeRequest(newNick, token ?: "")
            } else {
                ToastUtil.showFailureToast(this, "닉네임을 입력하세요.")
            }
        }

        btnPwChange.setOnClickListener {
            val currentPw = prePw.text.toString().trim()
            val newPw = etPw.text.toString().trim()
            val newPwConfirm = etPwConfirm.text.toString().trim()

            if (currentPw.isNotEmpty() && newPw.isNotEmpty() && newPw == newPwConfirm) {
                val request = object : StringRequest(
                    Request.Method.POST,
                    "http://192.168.219.63:8089/IZG/updatePw",
                    { response ->
                        Log.d("InfoActivity", "Response: $response")
                        val jsonResponse = JSONObject(response)
                        val newToken = jsonResponse.getString("newToken")
                        SharedPrefManager.saveToken(this, newToken)

                        ToastUtil.showSuccessToast(this, "비밀번호가 변경되었습니다.")
                    },
                    { error ->
                        Log.e("InfoActivity", "Error: $error")
                        ToastUtil.showFailureToast(this, "기존 비밀번호가 맞지 않습니다.")
                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Authorization"] = "Bearer $token"
                        headers["Content-Type"] = "application/json; charset=UTF-8"
                        return headers
                    }

                    override fun getBody(): ByteArray {
                        val jsonBody = JSONObject()
                        jsonBody.put("currentPw", currentPw)
                        jsonBody.put("newPw", newPw)
                        return jsonBody.toString().toByteArray(Charsets.UTF_8)
                    }
                }

                queue.add(request)
            } else {
                ToastUtil.showFailureToast(this, "비밀번호를 확인하세요.")
            }
        }

    }

    private fun extractNickFromToken(token: String): String? {
        return try {
            val split = token.split(".")
            val decodedPayload = getJson(split[1])
            val jsonObject = JSONObject(decodedPayload)
            jsonObject.getString("nick")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getJson(strEncoded: String): String {
        val decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes, Charsets.UTF_8)
    }

    private fun sendNickChangeRequest(newNick: String, token: String) {
        val url = "http://192.168.219.63:8089/IZG/updateNick"

        val jsonBody = JSONObject()
        jsonBody.put("newNick", newNick)

        val request = object : StringRequest(
            Method.POST,
            url,
            { response ->
                Log.d("InfoActivity", "Response: $response")
                try {
                    val jsonResponse = JSONObject(response)
                    val newToken = jsonResponse.getString("newToken")
                    SharedPrefManager.saveToken(this, newToken)

                    // 변경된 사항을 Toast로 알림
                    ToastUtil.showSuccessToast(this, "닉네임이 변경되었습니다.")

                    // 변경 사항 결과를 메인 액티비티로 전달
                    val resultIntent = Intent()
                    resultIntent.putExtra("newNick", newNick)
                    updateSettingsFragmentNickname(newNick)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ToastUtil.showFailureToast(this, "닉네임 변경에 실패했습니다.")
                }
            },
            { error ->
                ToastUtil.showFailureToast(this, "닉네임 변경에 실패했습니다.")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                headers["Content-Type"] = "application/json" // JSON 요청임을 명시
                return headers
            }

            override fun getBody(): ByteArray {
                val body = jsonBody.toString().toByteArray(Charset.forName("UTF-8"))
                Log.d("InfoActivity", "Request Body: ${String(body, Charset.forName("UTF-8"))}")
                return body
            }
        }

        queue.add(request)
    }

    private fun updateNickname(newNick: String) {
        tvGetNick.text = newNick
        tvGetWord.text = newNick.substring(0, 1)
    }
    private fun updateSettingsFragmentNickname(newNick: String) {
        val fragment = supportFragmentManager.findFragmentByTag("SettingsFragment") as? SettingsFragment
        fragment?.updateNickname(newNick)
    }

}
