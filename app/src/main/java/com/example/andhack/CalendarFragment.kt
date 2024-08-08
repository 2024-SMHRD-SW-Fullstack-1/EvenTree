package com.example.andhack

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.andhack.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var calendar: MaterialCalendarView
    private val events = mutableListOf<CalendarDay>()
    private var selectedDate: CalendarDay? = null
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 캘린더
        calendar = binding.calendarView

        // 색칠할 날짜를 calendarDayList에 추가
        events.add(CalendarDay.from(2022, 5, 25))
        events.add(CalendarDay.from(2022, 5, 24))
        events.add(CalendarDay.from(2022, 5, 23))

        // 오늘 날짜 선택
        calendar.setSelectedDate(CalendarDay.today())

        // Decorator
        // ToDay Decorator
        val todayDecorator = ToDayDecorator(requireContext())
        // Saturday and Sunday Decorators
        val saturdayDecorator = SaturdayDecorator()
        val sundayDecorator = SundayDecorator()
        val selectedDecorator = SelectedDecorator(requireContext())
        // val eventDecorator = EventDecorator()
        // val minMaxDecorator = MinMaxDecorator()

        // Add Decorator
        binding.calendarView.addDecorators(
            todayDecorator,
            saturdayDecorator,
            sundayDecorator,
            selectedDecorator
        )

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // 달력 화면 접속 시 ViewModel에 있는 선택된 날짜 데이터를 불러오기
        viewModel.selectedDate.observe(viewLifecycleOwner, { date ->
            selectedDate = date
            Log.d("CalFragment entered", date.toString())
            selectedDecorator.setDate(selectedDate)
            binding.calendarView.invalidateDecorators() // 데코레이터 새로고침
        })

        // 날짜 선택 시
        // ViewModel에 날짜 설정
        // 클릭한 날짜 값 date(YYYY-MM-DD), year, month, day로 넘기기
        calendar.setOnDateChangedListener { widget, date, selected ->
            if (selected) {
                // date :
                // 날짜 부분 추출
                val datePart: String = date.toString().substringAfter('{').substringBefore('}')
                Log.d("datePart", datePart)

                // LocalDate로 변환
                // DateTimeFormatter를 사용하여 문자열 형식 정의
                val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
                val localDate = LocalDate.parse(datePart, formatter)

                // 연도, 월, 일을 추출하여 로그로 출력
                Log.d("localDate year", localDate.year.toString()) // 출력: 2024
                Log.d("localDate month", localDate.monthValue.toString().padStart(2, '0')) // 출력: 08
                Log.d("localDate day", localDate.dayOfMonth.toString().padStart(2, '0')) // 출력: 23

                // 날짜와 시간 출력
                Log.d("localDate", localDate.toString()) // 출력: 2024-08-23T00:00

                // intent로 데이터 date, year, month, day 넘기기
                val intent = Intent(requireActivity(), EventListActivity::class.java)
                intent.putExtra("date", localDate.toString())
                intent.putExtra("year", localDate.year.toString())
                intent.putExtra("month", localDate.monthValue.toString().padStart(2, '0'))
                intent.putExtra("day", localDate.dayOfMonth.toString().padStart(2, '0'))
                startActivity(intent)
            }

            viewModel.setSelectedDate(date)
            selectedDate = date
            selectedDecorator.setDate(selectedDate)
            Log.d("CalFragment clicked", selectedDate.toString())
            binding.calendarView.invalidateDecorators() // 데코레이터 새로고침
        }
    }

    // 오늘 날짜 색상 변경
    inner class ToDayDecorator(context: Context) : DayViewDecorator {

        private var date = CalendarDay.today()
        val drawble =
            context?.resources?.getDrawable(com.example.andhack.R.drawable.date_today_deco, null)

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day?.equals(date)!!
        }

        override fun decorate(view: DayViewFacade?) {
            if (drawble != null) {
                view?.setBackgroundDrawable(drawble)
            }
        }
    }

    // 토요일 파란 색상 변경
    inner class SaturdayDecorator : DayViewDecorator {
        private val calendar = Calendar.getInstance()

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            day?.let {
                // CalendarDay의 연도, 월, 일로 Calendar 인스턴스를 설정합니다.
                calendar.set(it.year, it.month - 1, it.day) // 월은 0부터 시작하므로 -1
                val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
                return weekDay == Calendar.SATURDAY
            }
            return false
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(ForegroundColorSpan(Color.BLUE))
        }
    }

    // 일요일 빨간 색상 변경
    inner class SundayDecorator : DayViewDecorator {
        private val calendar = Calendar.getInstance()

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            day?.let {
                // CalendarDay의 연도, 월, 일로 Calendar 인스턴스를 설정합니다.
                calendar.set(it.year, it.month - 1, it.day) // 월은 0부터 시작하므로 -1
                val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
                return weekDay == Calendar.SUNDAY
            }
            return false
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(ForegroundColorSpan(Color.RED))
        }
    }

    // 선택된 날짜 색상 변경
    inner class SelectedDecorator(context: Context) : DayViewDecorator {

        private var date = selectedDate

        val drawble =
            context?.resources?.getDrawable(
                com.example.andhack.R.drawable.calendar_selected_date_background,
                null
            )

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day?.equals(date)!!
        }

        override fun decorate(view: DayViewFacade?) {
            if (drawble != null) {
                view?.setBackgroundDrawable(drawble)
            }
        }

        fun setDate(date: CalendarDay?) {
            this.date = date
        }
    }

    // 일정이 있는 날짜에 표시
    inner class EventDecorator(private val dates: Collection<CalendarDay>) : DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(8f, Color.GREEN)) // 날짜 밑에 빨간 점으로 표시
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertCalendarDayToLocalDate(calendarDay: CalendarDay): LocalDate {
        // CalendarDay에서 연도, 월, 일을 추출
        val year = calendarDay.year
        val month = calendarDay.month
        val day = calendarDay.day

        // LocalDate로 변환
        return LocalDate.of(year, month, day)
    }
}
