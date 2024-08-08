package com.example.andhack
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue

class StartActivity : AppCompatActivity() {
    lateinit var queue: RequestQueue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnJoin = findViewById<Button>(R.id.btnJoin)
        val token = ShaeredPrefManager.getToken(this)

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        btnJoin.setOnClickListener {

            if(token != null){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this, JoinActivity::class.java)
                startActivity(intent)
            }
        }

    }
}