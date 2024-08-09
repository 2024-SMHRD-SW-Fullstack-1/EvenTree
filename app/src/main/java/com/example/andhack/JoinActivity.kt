package com.example.andhack

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject

class JoinActivity : AppCompatActivity() {
    lateinit var queue: RequestQueue

    private lateinit var etJoinPw : EditText
    private lateinit var etJoinPwConfirm : EditText
    private lateinit var  tvValidation : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val etJoinId = findViewById<EditText>(R.id.etJoinId)
        etJoinPw = findViewById(R.id.etJoinPw)
        etJoinPwConfirm = findViewById(R.id.etJoinPwConfirm)
        val etJoinNick = findViewById<EditText>(R.id.etJoinNick)
        val buttonjoin = findViewById<Button>(R.id.btnJoin2)
        tvValidation = findViewById(R.id.tvValidation)

        queue = Volley.newRequestQueue(this@JoinActivity)



        // TextWatcher 설정
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePasswords() // 입력이 변경될 때마다 호출
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        // TextWatcher를 EditText에 추가
        etJoinPw.addTextChangedListener(textWatcher)
        etJoinPwConfirm.addTextChangedListener(textWatcher)

        buttonjoin.setOnClickListener {
            val joinId = etJoinId.text.toString()
            val joinNick = etJoinNick.text.toString()
            val joinPw = etJoinPw.text.toString()
            val joinPwValidate = etJoinPwConfirm.text.toString()

            if(joinPw == joinPwValidate && joinId.isNotEmpty() && joinNick.isNotEmpty()){
                val mv = MemberVO(joinId, joinPw, joinNick, null)
                val jsonRequest = JSONObject(Gson().toJson(mv))

                val request = object :StringRequest(
                    Request.Method.POST,
                    "http://192.168.219.63:8089/IZG/join",
                    {
                            response ->
                        val intent = Intent(this,LoginActivity::class.java)
                        finishAffinity()
                        startActivity(intent)

                    },
                    {
                            error ->
                        Toast.makeText(this, "Request Error: ${error.message}", Toast.LENGTH_SHORT).show()
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
            }else{
                if(joinPw != joinPwValidate) {
                    Toast.makeText(this,"입력한 비밀번호 확인해주세요.",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"비어있으면 안됩니다용",Toast.LENGTH_SHORT).show()
                }


            }
        }
    }
    private fun validatePasswords() {
        val joinPw = etJoinPw.text.toString()
        val joinPwConfirm = etJoinPwConfirm.text.toString()

        if (joinPwConfirm.isEmpty()) {
            tvValidation.setText(R.string.password_empty)
            tvValidation.setTextColor(Color.BLACK)
        } else {
            if (joinPw == joinPwConfirm) {
                tvValidation.setText(R.string.password_match)
                tvValidation.setTextColor(Color.parseColor("#4CAF50"))
            } else {
                tvValidation.setText(R.string.password_mismatch)
                tvValidation.setTextColor(Color.parseColor("#F44336"))
            }
        }
    }

}