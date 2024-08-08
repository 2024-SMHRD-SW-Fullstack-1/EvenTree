package com.example.demo.controller;

import java.net.http.HttpHeaders;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.EventModel;
import com.example.demo.model.EventRequestDTO;
import com.example.demo.model.MemberModel;
import com.example.demo.repository.EventRepository;
import com.example.demo.service.EventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class EventController {

	@Autowired
	private EventService eventService;

	// 일정 추가
	@PostMapping("/add-event")
	public String addEvent(@RequestBody EventModel ev) throws JsonMappingException, JsonProcessingException {
		System.out.println(ev);
		eventService.addEvent(ev);
		// 응답데이터 정의
		return "OK";
	}

	// 일정 찾기
	@PostMapping("/get-events")
	public List<EventModel> getEventsByDate(@RequestBody Map<String, String> request) {
        String date = request.get("date");
        System.out.println("received date : " + date);
        List<EventModel> events = eventService.getEventsByDate(date);
        
        // 일정 출력
        for (EventModel event : events) {
        	System.out.println("-----");
            System.out.println("Event ID: " + event.getEIdx());
            System.out.println("Member ID: " + event.getMIdx());
            System.out.println("Start Date: " + event.getStartDate());
            System.out.println("End Date: " + event.getEndDate());
            System.out.println("Title: " + event.getTitle());
            System.out.println("Content: " + event.getContent());
            System.out.println("-----");
        }

        return events;
    }
}
