package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.model.MemberModel;
import com.example.demo.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
public class MemberController {

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	// 회원가입
	@PostMapping("/join")
	public String join(@RequestBody MemberModel mv) throws JsonMappingException, JsonProcessingException {
		System.out.println(mv);
		memberService.join(mv);
		// 응답데이터 정의
		return "OK";
	}

	@PostMapping("/login")
	public String login(@RequestBody MemberModel mv, String password) {
		MemberModel member = memberService.findByIdAndPw(mv.getId(), mv.getPw());
		
		
		if (member != null && member.getPw().equals(mv.getPw())) {
			String token = jwtTokenProvider.createToken(member.getId(), member.getMIdx());
			System.out.println(token);
			return token;
		} else {
			System.out.println("실패");
			return "fail";
		}
	}
}
