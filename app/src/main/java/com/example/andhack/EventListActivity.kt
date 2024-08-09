package com.example.andhack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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

        // CalendarFragment의 intent로 데이터 받기
        date = intent.getStringExtra("date") ?: ""
        year = intent.getStringExtra("year") ?: ""
        month = intent.getStringExtra("month") ?: ""
        day = intent.getStringExtra("day") ?: ""

        // EventFragment에게 intent로 데이터 date 넘기기
//        val intent = Intent(this, EventFragment::class.java)
//        intent.putExtra("date", date)
//        startActivity(intent)




        // 일정 리스트 화면의 최상단 날짜 설정
        tvDate.text = date

        val rvEvent = binding.rvEvent
        val btnAddEvent = binding.btnAddEvent

        // 일정 추가 누르면 메인 화면으로 이동했다가 일정 추가 프래그먼트로 이동
//        btnAddEvent.setOnClickListener {
//            // 버튼 클릭 시 MainActivity를 호출하고, MainActivity에서 Fragment를 교체
//            val intent = Intent(this, MainActivity::class.java)
//            intent.putExtra("fragment_to_open", "EventFragment")
//            startActivity(intent)
//        }


        // 요청이 여러 개이더라도 큐는 1개만 필요
        requestQueue = Volley.newRequestQueue(this@EventListActivity)

        // 일정 데이터 저장할 리스트
        eventList = ArrayList<EventVO>()

        // 어댑터 생성
        val adapter = EventAdapter(this@EventListActivity, eventList) { event ->
            // 클릭된 일정의 일정 상세 화면으로 이동
            // intent로 데이터 date, year, month, day 넘기기
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra("title", event.title)
            intent.putExtra("mIdx", event.mIdx)
            intent.putExtra("startDate", event.startDate)
            intent.putExtra("endDate", event.endDate)
            intent.putExtra("content", event.content)
            startActivity(intent)
        }
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
                        mIdx = 1,
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
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                return jsonObject.toString().toByteArray(Charsets.UTF_8)
            }
        }

        requestQueue.add(request)
    }
}
