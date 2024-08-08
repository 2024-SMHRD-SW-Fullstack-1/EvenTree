package com.example.andhack

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.andhack.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
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

        // 날짜 선택 시 ViewModel에 날짜 설정
        calendar.setOnDateChangedListener { _, date, _ ->
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
}
