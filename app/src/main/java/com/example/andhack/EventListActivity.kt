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
    lateinit var adapter: EventAdapter // 어댑터를 클래스 변수로 선언
    var date: String = ""
    var year: String = ""
    var month: String = ""
    var day: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvDate = binding.tvDate

        date = intent.getStringExtra("date") ?: ""
        Log.d("date", date)
        year = intent.getStringExtra("year") ?: ""
        month = intent.getStringExtra("month") ?: ""
        day = intent.getStringExtra("day") ?: ""

        tvDate.text = date

        val rvEvent = binding.rvEvent

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
        val token = SharedPrefManager.getToken(this)
        val jsonObject = JSONObject().apply {
            put("date", date)
            put("year", year)
            put("month", month)
            put("day", day)
        }

        requestQueue.cache.clear()
        val request = object : StringRequest(
            Request.Method.POST,
            "http://192.168.219.63:8089/IZG/get-events",
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
                        startDate = jsonObject.getString("startDate"),
                        endDate = jsonObject.getString("endDate"),
                        content = jsonObject.getString("content")
                    )
                    newEvents.add(event)
                }

                eventList.clear()
                eventList.addAll(newEvents)
                adapter.notifyDataSetChanged()
            },
            { error ->
                Log.e("eventList error", "Error: ${error.message}")
                Toast.makeText(this, "Error fetching events", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                headers["Authorization"] = "Bearer $token" // 필요시 인증 토큰 추가
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

    override fun onResume() {
        super.onResume()
        Log.d("EventListActivity", "onResume called")
        refreshEventList()
    }
}
