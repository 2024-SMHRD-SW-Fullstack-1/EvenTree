package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.EventModel;
import com.example.demo.repository.EventRepository;

@Service
public class EventService {

	@Autowired
	EventRepository eventRepository;

	// 일정 추가
	public void addEvent(EventModel ev) {
		eventRepository.save(ev);
	}
	
	// 선택된 하루의 일정 찾기
	public List<EventModel> getEventsByDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay(); // 날짜 00:00:00
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX); // 날짜 23:59:59
        System.out.println("parsed startOfDay : " + startOfDay.toString());
        System.out.println("parsed endOfDay : " + endOfDay.toString());

        return eventRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endOfDay, startOfDay);
    }
	
	// 일정 모두 찾기
	public List<EventModel> getAllEvents() {
        return eventRepository.findAll();
    }
}
