package com.example.andhack

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.LineBackgroundSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.andhack.databinding.FragmentCalendarBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import org.json.JSONArray
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var calendar: MaterialCalendarView
    private val events = mutableListOf<CalendarDay>()
    private var selectedDate: CalendarDay? = null
    private lateinit var viewModel: SharedViewModel
    lateinit var queue: RequestQueue

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

        // RequestQueue 초기화
        queue = Volley.newRequestQueue(requireContext())

        // DB에서 일정 가져오기
        fetchEvents()

        // 색칠할 날짜를 calendarDayList에 추가
        // events.add(CalendarDay.from(2022, 5, 25))
        // events.add(CalendarDay.from(2022, 5, 24))
        // events.add(CalendarDay.from(2022, 5, 23))

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
        calendar.addDecorators(
            todayDecorator,
            saturdayDecorator,
            sundayDecorator,
            selectedDecorator,
        )

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // 달력 화면 접속 시 ViewModel에 있는 선택된 날짜 데이터를 불러오기
        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            selectedDate = date
            Log.d("CalFragment entered", date.toString())
            selectedDecorator.setDate(selectedDate)
            binding.calendarView.invalidateDecorators() // 데코레이터 새로고침
        }

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun eventDecorator(context: Context, eventList: List<EventVO>): DayViewDecorator {
        return object : DayViewDecorator {
            private val eventDates = HashSet<CalendarDay>()

            init {
                // 스케줄 목록에서 이벤트가 있는 날짜를 파싱하여 이벤트 날짜 목록에 추가한다.
                eventList.forEach { event ->
                    try {
                        // 날짜와 시간 정보를 포함한 문자열을 LocalDate로 변환
                        val startDate = event.startDate.substringBefore('T')
                        val endDate = event.endDate.substringBefore('T')
                        // 예시. 2024-08-08
                        Log.d("parsed startDate", startDate)
                        Log.d("parsed endDate", endDate)

                        // LocalDate로 변환
                        // 예시. 2024-08-08
                        val startLocalDate = LocalDate.parse(startDate)
                        val endLocalDate = LocalDate.parse(endDate)
                        Log.d("parsed startLocalDate", startLocalDate.toString())
                        Log.d("parsed endLocalDate", endLocalDate.toString())

                        // 날짜 범위 사이의 모든 날짜를 생성
                        val datesInRange = getDateRange(startLocalDate, endLocalDate)
                        eventDates.addAll(datesInRange)
                    } catch (e: Exception) {
                        Log.e("EventDecorator", "Invalid date format for event: ${event.startDate} - ${event.endDate}")
                    }
                }
            }

            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return eventDates.contains(day)
            }

            override fun decorate(view: DayViewFacade) {
                // 이벤트가 있는 날짜에 점을 추가하여 표시한다.
                view.addSpan(DotSpan(10F, ContextCompat.getColor(context, R.color.primary_color)))
            }

            /**
             * 시작 날짜와 종료 날짜 사이의 모든 날짜를 가져오는 함수
             * @param startDate 시작 날짜
             * @param endDate 종료 날짜
             * @return 날짜 범위 목록
             */
            @RequiresApi(Build.VERSION_CODES.O)
            private fun getDateRange(startDate: LocalDate, endDate: LocalDate): List<CalendarDay> {
                val datesInRange = mutableListOf<CalendarDay>()
                var currentDate = startDate
                while (!currentDate.isAfter(endDate)) {
                    datesInRange.add(
                        CalendarDay.from(
                            currentDate.year,
                            currentDate.monthValue,
                            currentDate.dayOfMonth
                        )
                    )
                    currentDate = currentDate.plusDays(1)
                }
                return datesInRange
            }
        }
    }

    private fun fetchEvents() {
        Log.d("fetch ee", "fetch")

        val url = "http://39.114.154.29:8089/IZG/get-all-events"

        val request = @RequiresApi(Build.VERSION_CODES.O)
        object : StringRequest(
            Request.Method.GET,
            url,
            Response.Listener<String> { response ->
                Log.d("all events response", response.toString())
                try {
                    // JSON 배열을 파싱하여 리스트로 변환
                    val jsonArray = JSONArray(response)
                    val gson = Gson()
                    val type = object : TypeToken<List<EventVO>>() {}.type
                    val eventList: List<EventVO> = gson.fromJson(jsonArray.toString(), type)
                    // 이벤트를 처리하는 로직
                    Log.d("Parsed Events", eventList.toString())
                    // 기존의 이벤트 데코레이터를 제거
                    calendar.removeDecorators()
                    // 데코레이터 생성 및 추가
                    val todayDecorator = ToDayDecorator(requireContext())
                    val saturdayDecorator = SaturdayDecorator()
                    val sundayDecorator = SundayDecorator()
                    val selectedDecorator = SelectedDecorator(requireContext())
                    val eventDecorator = eventDecorator(requireContext(), eventList)
                    // Add Decorator
                    calendar.addDecorators(
                        todayDecorator,
                        saturdayDecorator,
                        sundayDecorator,
                        selectedDecorator,
                        eventDecorator
                    )
                    binding.calendarView.invalidateDecorators() // 데코레이터 새로고침
                } catch (e: Exception) {
                    Log.e("Parsing Error", "Failed to parse events: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                Log.e("Volley", "Error fetching events: ${error.message}")
            }
        ) {
            // GET 요청에는 바디가 필요 없으므로 getBodyContentType을 오버라이드할 필요 없음
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }

        // RequestQueue에 요청 추가
        queue.add(request)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        // DB에서 일정 가져오기
        fetchEvents()
    }
}
