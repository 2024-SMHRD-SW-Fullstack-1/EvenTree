package com.example.andhack

<<<<<<< HEAD
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
=======
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
>>>>>>> jand-working
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
<<<<<<< HEAD

    // 캘린더에서 클릭한 날짜
    // Intent로부터 데이터 받기
=======
    lateinit var adapter: EventAdapter // 어댑터를 클래스 변수로 선언

>>>>>>> jand-working
    var date: String = ""
    var year: String = ""
    var month: String = ""
    var day: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvDate = binding.tvDate

<<<<<<< HEAD
        // Intent로부터 데이터 받기
=======
>>>>>>> jand-working
        date = intent.getStringExtra("date") ?: ""
        year = intent.getStringExtra("year") ?: ""
        month = intent.getStringExtra("month") ?: ""
        day = intent.getStringExtra("day") ?: ""

<<<<<<< HEAD
        // 일정 리스트 화면의 최상단 날짜 설정
=======
>>>>>>> jand-working
        tvDate.text = date

        val rvEvent = binding.rvEvent
        val btnAddEvent = binding.btnAddEvent

<<<<<<< HEAD
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
=======
        // 큐 초기화
        requestQueue = Volley.newRequestQueue(this@EventListActivity)

        // 리스트와 어댑터 초기화
        eventList = ArrayList()
        adapter = EventAdapter(this@EventListActivity, eventList) { event ->
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra("eIdx", event.eIdx)
            intent.putExtra("title", event.title)
            intent.putExtra("mIdx", event.mIdx)
            intent.putExtra("startDate", event.startDate)
            intent.putExtra("endDate", event.endDate)
            intent.putExtra("content", event.content)
            intent.putExtra("date", date)
            intent.putExtra("year", year)
            intent.putExtra("month", month)
            intent.putExtra("day", day)
            startActivity(intent)
        }

        rvEvent.adapter = adapter
        rvEvent.layoutManager = LinearLayoutManager(this@EventListActivity)

        refreshEventList()
    }

    private fun refreshEventList() {
>>>>>>> jand-working
        val jsonObject = JSONObject().apply {
            put("date", date)
            put("year", year)
            put("month", month)
            put("day", day)
        }
<<<<<<< HEAD

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
=======
        requestQueue.cache.clear()
        val request = object : StringRequest(

            Request.Method.POST,
            "http://39.114.154.29:8089/IZG/get-events",
            { response ->
                Log.d("events response", response)

                val jsonArray = JSONArray(response)
                val newEvents = ArrayList<EventVO>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val event = EventVO(
                        eIdx = jsonObject.getInt("eIdx"),
                        title = jsonObject.getString("title"),
                        mIdx = jsonObject.getInt("mIdx"),
>>>>>>> jand-working
                        startDate = jsonObject.getString("startDate"),
                        endDate = jsonObject.getString("endDate"),
                        content = jsonObject.getString("content")
                    )
                    newEvents.add(event)
                }

<<<<<<< HEAD
                // 기존의 일정 리스트를 초기화
                eventList.clear()

                // 기존 일정 리스트에 출력할 일정들 추가
                eventList.addAll(newEvents)

                // 어댑터에 데이터 변경을 알림
=======
                eventList.clear()
                eventList.addAll(newEvents)
>>>>>>> jand-working
                adapter.notifyDataSetChanged()
            },
            { error ->
                Log.e("Error", "Error: ${error.message}")
<<<<<<< HEAD
=======
                Toast.makeText(this, "Error fetching events", Toast.LENGTH_SHORT).show()
>>>>>>> jand-working
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }
<<<<<<< HEAD

=======
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
>>>>>>> jand-working
            override fun getBody(): ByteArray {
                return jsonObject.toString().toByteArray(Charsets.UTF_8)
            }
        }

        requestQueue.add(request)
    }
<<<<<<< HEAD
}
=======

    override fun onResume() {
        super.onResume()
        Log.d("EventListActivity", "onResume called")
        refreshEventList()
    }
}
>>>>>>> jand-working
