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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.model.EventModel;
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
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	// 일정 추가
	@PostMapping("/add-event")
	public String addEvent(@RequestHeader("Authorization") String token, @RequestBody EventModel ev) throws JsonMappingException, JsonProcessingException {
		// "Bearer " 부분을 제거하고 실제 토큰 값만 추출
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 토큰에서 mId 추출
        int mIdx = jwtTokenProvider.getMIdx(token);
        ev.setMIdx(mIdx);
		System.out.println("received event : " + ev);
	    eventService.addEvent(ev);
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
