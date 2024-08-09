package com.example.andhack

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventAdapter(val context: Context, val eventList: ArrayList<EventVO>, private val onItemClick: (EventVO) -> Unit): RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    // View Holder : weatherList 개별 데이터에 뷰에 대응
    // itemView는 weather_item.xml이 객체화된 것
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView
        val tvTitle: TextView

        // 객체 초기화 시 한 번 처음에 실행되는 부분 -> 뷰 가져오기
        init {
            tvTime = itemView.findViewById(R.id.tvTime)
            tvTitle = itemView.findViewById(R.id.tvTitle)
            // 클릭 리스너 설정
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // 클릭된 아이템의 데이터 가져오기
                    val event = eventList[position]
                    // 클릭 리스너 호출
                    onItemClick(event)
                }
            }
        }
    }

    // 뷰홀더 생성
    // 화면에 10개의 뷰가 보인다면 초 기에는 여유있게 13~15개 정도 여유있게 뷰 객체를 생성
    // onCreateViewHolder는 13~15번 호출되고 더 이상 호출되지 않음
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.event_item, null)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTime.text = formatDateToHHMM(eventList.get(position).startDate)
        holder.tvTitle.text = eventList.get(position).title
    }

    // 스프링에서 받은 날짜 데이터는 2024-07-25T01:11:00 형식 -> HH:mm 으로 변환한다.
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateToHHMM(dateTimeString: String): String {
        // 입력 형식 정의
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        // 출력 형식 정의
        val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")

        // 문자열을 LocalDateTime으로 파싱
        val dateTime = LocalDateTime.parse(dateTimeString, inputFormatter)

        // 원하는 형식으로 변환
        return dateTime.format(outputFormatter)
    }
}