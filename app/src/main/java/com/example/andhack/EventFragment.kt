package com.example.andhack

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.andhack.databinding.FragmentEventBinding
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar

class EventFragment : Fragment() {

    private lateinit var binding: FragmentEventBinding
    private lateinit var viewModel: SharedViewModel

    lateinit var btnClose: ImageButton
    lateinit var btnSave: Button
    lateinit var btnMenu: ImageButton
    lateinit var etTitle: EditText
    lateinit var tvStartDate: TextView
    lateinit var tvStartTime: TextView
    lateinit var tvEndDate: TextView
    lateinit var tvEndTime: TextView
    lateinit var etMemo: EditText
    lateinit var btnInput: Button
    lateinit var tvMemo: TextView
    lateinit var queue: RequestQueue
    var eIdx: Int? = null
    var startDate: Calendar? = null
    var endDate: Calendar? = null
    var startTime: Pair<Int, Int>? = null
    var endTime: Pair<Int, Int>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        // RequestQueue 초기화
        queue = Volley.newRequestQueue(requireContext())

        // 달력에서 선택된 날짜 불러오기
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            val formattedDate = String.format("%04d-%02d-%02d", date.year, date.month, date.day)
            tvStartDate.text = formattedDate
            tvEndDate.text = formattedDate

            startDate = Calendar.getInstance().apply {
                set(date.year, date.month - 1, date.day)
            }
            endDate = Calendar.getInstance().apply {
                set(date.year, date.month - 1, date.day)
            }

            tvEndDate.setOnClickListener {
                showDatePicker(tvEndDate, false)
            }
        }

        // 뒤로가기 버튼
        btnClose.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainContent, CalendarFragment())
                .commit()
            true
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

                Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show()
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
                    memoText
                } else {
                    "$existingText\n$memoText"
                }
                Log.d("memoresult", newText) // 입력한 값들 누적된것 = newText
                tvMemo.text = newText
                etMemo.text.clear() // 초기화
            } else {
                Toast.makeText(requireContext(), "메모를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun showDatePicker(targetTextView: TextView, isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
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
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
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


            val time = String.format("%02d:%02d", hour, minute)
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
                                requireContext(),
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
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.event_detail_nav_option, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.m_edit -> {
                    btnSave.visibility = View.VISIBLE // 입력 후 저장버튼 보이기
                    Toast.makeText(requireContext(), "수정하기", Toast.LENGTH_SHORT).show()
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

                    eIdx?.let { deleteEvent(it) }

                    // 삭제 후, 캘린더로 이동
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContent, CalendarFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    fun saveEvent(eventVO: EventVO){
        val token = SharedPrefManager.getToken(requireContext())
        val url = "http://39.114.154.29:8089/IZG/add-event" //서버 주소

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
            {response ->
                Log.d("res!", response)

                // event를 다시 반환 받아서 eIdx를 통해 일정을 삭제가 가능함
                val event: EventVO = Gson().fromJson(response.toString(), EventVO::class.java)
                eIdx = event.eIdx
                btnSave.visibility = View.INVISIBLE //메모 작성 후 저장버튼 사라짐
            },
            {error ->
                Log.d("Err" , error.toString())
                Toast.makeText(requireContext(), "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
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
        val token = SharedPrefManager.getToken(requireContext())
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
                    Toast.makeText(requireContext(), "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Err", error.toString())
                Toast.makeText(requireContext(), "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
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
        val token = SharedPrefManager.getToken(requireContext())
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
            },
            { error ->
                Log.d("error", error.toString())
                Toast.makeText(requireContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
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
}