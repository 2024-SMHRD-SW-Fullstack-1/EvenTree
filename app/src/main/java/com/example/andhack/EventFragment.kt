package com.example.andhack

import android.app.AlertDialog
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.andhack.databinding.FragmentEventBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

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

        // 달력에서 선택된 날짜 불러오기
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            if (date.year == null || date.month == null || date.day == null) {
                CalendarDay.today()
            } else {
                CalendarDay.from(date.year, date.month, date.day)
            }
            // 선택된 날짜로 작업 수행
            tvStartDate.text = date.toString()
            tvEndDate.text = date.toString()
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
            showDatePicker(tvStartDate)
        }
        // 일정 시작 시간 설정
        tvStartTime.setOnClickListener {
            showTimePicker(tvStartTime)
        }
        // 일정 종료 날짜 설정
        tvEndDate.setOnClickListener {
            showDatePicker(tvEndDate)
        }
        // 일정 종료 시간 설정
        tvEndTime.setOnClickListener {
            showTimePicker(tvEndTime)
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
                Toast.makeText(requireContext(), "메모를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

    }
    //
    fun showTimePicker(targetTextView: TextView) {
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
            val hour = timePicker.currentHour
            val minute = timePicker.currentMinute
            val time = String.format("%02d:%02d", hour, minute)
            targetTextView.text = time
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    fun showDatePicker(targetTextView: TextView) {
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

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        // 꼭 확인할 것! 종료 날짜가 시작 날짜보다 전이면 X !!!!!!!!!!!!!!!!!
        okButton.setOnClickListener {
            val selectedYear = datePicker.year
            val selectedMonth = datePicker.month + 1
            val selectedDay = datePicker.dayOfMonth
            val date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, selectedDay)
            targetTextView.text = date
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
                    Toast.makeText(requireContext(), "편집", Toast.LENGTH_SHORT).show()
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
}
