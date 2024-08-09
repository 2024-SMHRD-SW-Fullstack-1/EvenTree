package com.example.andhack

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.andhack.databinding.ActivityEventDetailBinding
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar

class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding

    private lateinit var btnClose: ImageButton
    private lateinit var btnSave: Button
    private lateinit var btnMenu: ImageButton
    private lateinit var etTitle: EditText
    private lateinit var tvStartDate: TextView
    private lateinit var tvStartTime: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var tvEndTime: TextView
    private lateinit var etMemo: EditText
    private lateinit var btnInput: Button
    private lateinit var tvMemo: TextView
    private lateinit var queue: RequestQueue
    private var eIdx: Int? = null
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private var startTime: Pair<Int, Int>? = null
    private var endTime: Pair<Int, Int>? = null

    // 캘린더에서 클릭한 날짜
    // EventDetailActivity Intent로부터 데이터 받기
    var date: String = ""
    var year: String = ""
    var month: String = ""
    var day: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnClose = binding.btnClose
        btnSave = binding.btnSave
        btnMenu = binding.btnMenu
        etTitle = binding.etTitle
        tvStartDate = binding.tvStartDate
        tvStartTime = binding.tvStartTime
        tvEndDate = binding.tvEndDate
        tvEndTime = binding.tvEndTime
        etMemo = binding.etMemo
        btnInput = binding.btnInput
        tvMemo = binding.tvMemo

        queue = Volley.newRequestQueue(this)

        btnSave.visibility = View.INVISIBLE
        btnMenu.visibility = View.VISIBLE

        eIdx = intent.getIntExtra("eIdx", 0)
        val title = intent.getStringExtra("title") ?: ""
        val mIdx = intent.getIntExtra("mIdx", 0)
        val startDate = intent.getStringExtra("startDate")?.substringBefore("T")
        val startTime = intent.getStringExtra("startDate")?.substringAfter("T")
        val startHour = startTime?.substring(0, 2) ?: "00" // 기본값을 "00"으로 설정
        val startMin = startTime?.substring(3, 5) ?: "00"  // 기본값을 "00"으로 설정
        val endDate = intent.getStringExtra("endDate")?.substringBefore("T")
        val endTime = intent.getStringExtra("endDate")?.substringAfter("T")
        val endHour = endTime?.substring(0, 2) ?: "00" // 기본값을 "00"으로 설정
        val endMin = endTime?.substring(3, 5) ?: "00"  // 기본값을 "00"으로 설정
        val content = intent.getStringExtra("content") ?: ""

        // 일정 리스트에서 클릭한 날짜
        // EventListActivity의 Intent로부터 데이터 받기
        date = intent.getStringExtra("date") ?: ""
        year = intent.getStringExtra("year") ?: ""
        month = intent.getStringExtra("month") ?: ""
        day = intent.getStringExtra("day") ?: ""

        etTitle.setText(title)
        tvStartDate.text = startDate
        tvStartTime.text = startHour + ":" + startMin
        tvEndDate.text = endDate
        tvEndTime.text = endHour + ":" + endMin
        tvMemo.text = content

        // 뒤로가기 버튼
        btnClose.setOnClickListener {
            // 일정 리스트로 이동
            val intent = Intent(this, EventListActivity::class.java)
            intent.putExtra("date", date)
            intent.putExtra("year", year)
            intent.putExtra("month", month)
            intent.putExtra("day", day)
            startActivity(intent)
            finish() // 현재 Activity 종료
        }

        // 저장하기 버튼
        btnSave.setOnClickListener {
            if (tvMemo.text.isNotEmpty()) {
                btnSave.visibility = View.INVISIBLE // 메모 작성 후 저장 버튼 사라짐
                val title = etTitle.text.toString()
                val mIdx = 0 // 토큰으로 가져오기!
                val content = tvMemo.text.toString()

                // 시작 날짜와 시간 값을 가져오기
                val startDateValue = tvStartDate.text.toString()
                val startTimeValue = tvStartTime.text.toString()
                val endDateValue = tvEndDate.text.toString()
                val endTimeValue = tvEndTime.text.toString()

                val startDate = "${startDateValue}T${startTimeValue}:00"
                val endDate = "${endDateValue}T${endTimeValue}:00"

                val event = EventVO(eIdx, title, mIdx, startDate, endDate, content)
                if (eIdx == null) {
                    saveEvent(event)
                } else {
                    updateEvent(event)
                }
                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 메뉴바 버튼
        btnMenu.setOnClickListener {
            showPopupMenu(btnMenu)
        }

        // 일정 시작 날짜 설정
        tvStartDate.setOnClickListener {
            showDatePicker(tvStartDate, true)
        }
        // 일정 종료 날짜 설정
        tvEndDate.setOnClickListener {
            showDatePicker(tvEndDate, false)
        }
        // 일정 시작 시간 설정
        tvStartTime.setOnClickListener {
            showTimePicker(tvStartTime, true)
        }
        // 일정 종료 시간 설정
        tvEndTime.setOnClickListener {
            showTimePicker(tvEndTime, false)
        }
        // 입력하기 버튼
        btnSave.bringToFront() // 저장버튼 앞으로 가져오기
        btnInput.setOnClickListener {

            val memoText = etMemo.text.toString()
            if (memoText.isNotEmpty()) {
                // 기존 텍스트에 새 텍스트를 추가
                val existingText = tvMemo.text.toString()
                val newText = if (existingText.isEmpty()) {
//                    btnSave.visibility = View.VISIBLE
                    memoText // 첫 번째 메모 입력 시
                } else {
//                    btnMenu.visibility = View.VISIBLE // 메모내용이 없으면 메뉴바 사라지게하기
                    "$existingText\n$memoText"
                }
                Log.d("memoresult", newText) // 입력한 값들 누적된것 = newText
                tvMemo.text = newText
                etMemo.text.clear() // 초기화
            } else {
                Toast.makeText(this, "메모를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //

    fun showDatePicker(targetTextView: TextView, isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_date_picker, null)
        builder.setView(dialogView)

        val datePicker: DatePicker = dialogView.findViewById(R.id.datePicker)
        val cancelButton: Button = dialogView.findViewById(R.id.btnDateCancel)
        val okButton: Button = dialogView.findViewById(R.id.btnDateOk)

        val alertDialog = builder.create()

        // 시작 날짜 설정 시, 종료 날짜의 최대 날짜 설정
        if (isStartDate && endDate != null) {
            datePicker.maxDate = endDate!!.timeInMillis
        }
        // 종료 날짜 설정 시, 시작 날짜의 최소 날짜 설정
        else if (!isStartDate && startDate != null) {
            datePicker.minDate = startDate!!.timeInMillis
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        okButton.setOnClickListener {
            val selectedYear = datePicker.year
            val selectedMonth = datePicker.month + 1
            val selectedDay = datePicker.dayOfMonth
            val date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, selectedDay)
            targetTextView.text = date

            // 선택된 날짜를 전역 변수에 저장
            if (isStartDate) {
                startDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth - 1, selectedDay)
                }
                // 종료 날짜의 최소 날짜를 업데이트
                if (endDate != null) {
                    tvEndDate.setOnClickListener {
                        showDatePicker(tvEndDate, false)
                    }
                }
            } else {
                endDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth - 1, selectedDay)
                }
            }
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    fun showTimePicker(targetTextView: TextView, isStartTime: Boolean) {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_time_picker, null)
        builder.setView(dialogView)

        val timePicker: TimePicker = dialogView.findViewById(R.id.timePicker)
        val cancelButton: Button = dialogView.findViewById(R.id.btnCancel)
        val okButton: Button = dialogView.findViewById(R.id.btnOk)

        val alertDialog = builder.create()

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        okButton.setOnClickListener {
            val hour = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                timePicker.hour
            } else {
                timePicker.currentHour
            }
            val minute = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                timePicker.minute
            } else {
                timePicker.currentMinute
            }


            val Day = if (hour >= 12) "오후" else "오전"
            val justHour = if (hour % 12 == 0) 12 else hour % 12
            val time = String.format("%s %02d:%02d", Day, justHour, minute)
            if (isStartTime) {
                startTime = Pair(hour, minute)
                targetTextView.text = time
            } else {
                endTime = Pair(hour, minute)

                // 시작 시간과 종료 시간 비교
                startTime?.let { start ->
                    if (startDate != null && endDate != null && startDate!!.timeInMillis == endDate!!.timeInMillis) {
                        if (endTime!!.first < start.first || (endTime!!.first == start.first && endTime!!.second < start.second)) {
                            Toast.makeText(
                                this,
                                "종료 시간은 시작 시간보다 앞설 수 없습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            targetTextView.text = time
                        }
                    } else {
                        targetTextView.text = time
                    }
                } ?: run {
                    targetTextView.text = time
                }
            }
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.event_detail_nav_option, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.m_edit -> {
                    btnSave.visibility = View.VISIBLE // 입력 후 저장버튼 보이기
                    Toast.makeText(this, "수정하기", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.m_delete -> {
                    etTitle.text.clear()
                    tvStartDate.text = ""
                    tvStartTime.text = ""
                    tvEndDate.text = ""
                    tvEndTime.text = ""
                    etMemo.text.clear()
                    tvMemo.text = ""

                    // DB에서 삭제 요청
                    eIdx?.let { deleteEvent(it) }
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    fun saveEvent(eventVO: EventVO){
        val token = SharedPrefManager.getToken(this)
        val url = "http://39.114.154.29:8089/IZG/add-event" //서버 주소
        // val jsonRequest = JSONObject(Gson().toJson(eventVO))

        // JSON 객체 생성
        val jsonObject = JSONObject().apply {
            put("title", eventVO.title)
            put("content", eventVO.content)
            put("startDate", eventVO.startDate)
            put("endDate", eventVO.endDate)
        }

        Log.d("save eventVO", eventVO.toString())

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                Log.d("save response", response)
                // event를 다시 반환 받아서 eIdx를 통해 일정을 삭제가 가능함
                val event: EventVO = Gson().fromJson(response.toString(), EventVO::class.java)
                eIdx = event.eIdx
                btnSave.visibility = View.INVISIBLE //메모 작성 후 저장버튼 사라짐
            },
            { error ->
                Log.d("save error" , error.toString())
                Toast.makeText(this, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
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
        queue.add(request)
    }

    fun updateEvent(eventVO: EventVO) {
        val token = SharedPrefManager.getToken(this)
        val url = "http://39.114.154.29:8089/IZG/update-event" // 서버 주소

        // JSON 객체 생성
        val jsonObject = JSONObject().apply {
            put("eIdx", eventVO.eIdx)
            put("title", eventVO.title)
            put("content", eventVO.content)
            put("startDate", eventVO.startDate)
            put("endDate", eventVO.endDate)
        }

        Log.d("update eventVO", eventVO.toString())
        Log.d("JSON Request", jsonObject.toString())

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                // 서버 응답이 JSON 객체가 아니라면, 원래 문자열 응답을 로깅합니다
                try {
                    val jsonResponse = JSONObject(response)
                    Log.d("update response", jsonResponse.toString())
                    btnSave.visibility = View.INVISIBLE // 메모 작성 후 저장버튼 사라짐
                } catch (e: JSONException) {
                    Log.e("update response", "Response is not a valid JSON object: $response")
                    Toast.makeText(this, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Err", error.toString())
                Toast.makeText(this, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
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

        queue.add(request)
    }

    fun deleteEvent(eIdx: Int){
        val token = SharedPrefManager.getToken(this)
        val url = "http://39.114.154.29:8089/IZG/delete-event"

        // eIdx를 JSON 객체로 변환
        val jsonRequest = JSONObject()
        jsonRequest.put("eIdx", eIdx)
        Log.d("delete eIdx", eIdx.toString())

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                Log.d("delete response", response)
                // 삭제 후, 일정 리스트로 이동
                val intent = Intent(this, EventListActivity::class.java).apply {
                    putExtra("date", date)
                    putExtra("year", year)
                    putExtra("month", month)
                    putExtra("day", day)
                }
                startActivity(intent)
                finish() // 현재 Activity 종료
            },
            { error ->
                Log.d("error", error.toString())
                Toast.makeText(this, "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                headers["Authorization"] = "Bearer $token"
                return headers
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return jsonRequest.toString().toByteArray(Charsets.UTF_8)
            }
        }
        queue.add(request)
    }

    // 일정 상세 화면에서 시간 변환
    // 14:00:00 -> 오후 02:00 변환
//    private fun formatTime(hour: String, min: String): String {
//        // 오전/오후 계산
//        val hour = hour.toInt()
//        val period = if (hour != null && hour >= 12) "오후" else "오전"
//
//        // 12시간 형식으로 시간 변환 (0시는 12시로 변환)
//        val hourIn12Format = when {
//            hour == 0 -> 12
//            hour != null && hour > 12 -> hour - 12
//            else -> hour
//        }
//        // 최종 변환된 시간 문자열
//        val formattedTime = "$period ${hourIn12Format?.toString()?.padStart(2, '0')}:$min"
//
//        return formattedTime
//    }
}