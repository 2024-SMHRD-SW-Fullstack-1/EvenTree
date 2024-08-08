package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
	
	// 일정 찾기
	public List<EventModel> getEventsByDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime dateTime = localDate.atStartOfDay(); // 주어진 날짜의 00:00:00
        System.out.println("parsed dateTime : " + dateTime.toString());

        return eventRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(dateTime, dateTime);
    }
}
