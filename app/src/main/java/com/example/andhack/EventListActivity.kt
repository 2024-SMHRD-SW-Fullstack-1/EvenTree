package com.example.andhack

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.andhack.EventAdapter
import com.example.andhack.EventVO
import com.example.andhack.databinding.ActivityEventListBinding
import org.json.JSONArray
import org.json.JSONObject

class EventListActivity : AppCompatActivity() {
    lateinit var binding: ActivityEventListBinding
    lateinit var requestQueue: RequestQueue
    lateinit var eventList: ArrayList<EventVO>
    lateinit var tvDate: TextView

    // 캘린더에서 클릭한 날짜
    // Intent로부터 데이터 받기
    var date: String = ""
    var year: String = ""
    var month: String = ""
    var day: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvDate = binding.tvDate

        // Intent로부터 데이터 받기
        date = intent.getStringExtra("date") ?: ""
        year = intent.getStringExtra("year") ?: ""
        month = intent.getStringExtra("month") ?: ""
        day = intent.getStringExtra("day") ?: ""

        // 일정 리스트 화면의 최상단 날짜 설정
        tvDate.text = date

        val rvEvent = binding.rvEvent
        val btnAddEvent = binding.btnAddEvent

        // 요청이 여러 개이더라도 큐는 1개만 필요
        requestQueue = Volley.newRequestQueue(this@EventListActivity)

        // 일정 데이터 저장할 리스트
        eventList = ArrayList<EventVO>()

        // 어댑터 생성
        val adapter = EventAdapter(this@EventListActivity, eventList)
        // 뷰에 어댑터 부착
        rvEvent.adapter = adapter
        rvEvent.layoutManager = LinearLayoutManager(this@EventListActivity)

        // JSON 객체 생성
        val jsonObject = JSONObject().apply {
            put("date", date)
            put("year", year)
            put("month", month)
            put("day", day)
        }

        val request = object : StringRequest(
            Request.Method.POST,
            "http://192.168.219.63:8089/IZG/get-events",
            { response ->
                Log.d("events response", response)

                // JSON 응답을 JSONArray로 변환
                val jsonArray = JSONArray(response)

                // 새로운 이벤트 리스트를 생성
                val newEvents = ArrayList<EventVO>()

                // 각 JSONObject를 EventVO 객체로 변환하여 리스트에 추가
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    // 일정 리스트에서 writer는 보여주지 않기 때문에 스프링에서 데이터를 받지 않아 빈문자열 처리
                    val event = EventVO(
                        title = jsonObject.getString("title"),
                        writer = "",
                        startDate = jsonObject.getString("startDate"),
                        endDate = jsonObject.getString("endDate"),
                        content = jsonObject.getString("content")
                    )
                    newEvents.add(event)
                }

                // 기존의 일정 리스트를 초기화
                eventList.clear()

                // 기존 일정 리스트에 출력할 일정들 추가
                eventList.addAll(newEvents)

                // 어댑터에 데이터 변경을 알림
                adapter.notifyDataSetChanged()
            },
            { error ->
                Log.e("Error", "Error: ${error.message}")
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }

            override fun getBody(): ByteArray {
                return jsonObject.toString().toByteArray(Charsets.UTF_8)
            }
        }

        requestQueue.add(request)
    }
}
