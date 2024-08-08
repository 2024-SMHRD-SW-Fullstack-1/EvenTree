package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.MemberModel;
import com.example.demo.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
public class MemberController {

	@Autowired
	private MemberService memberService;

	// 회원가입
	@PostMapping("/join")
	public String join(@RequestBody MemberModel mv) throws JsonMappingException, JsonProcessingException {
		System.out.println(mv);
		memberService.join(mv);
		// 응답데이터 정의
		return "OK";
	}

	@PostMapping("/login")
	public String login(@RequestBody MemberModel mv) {
		MemberModel member = memberService.findByIdAndPw(mv.getId(), mv.getPw());
		
		if (member != null) {
			System.out.println(member);
			return "success";
		} else {
			return "Login failed";
		}
	}
}
