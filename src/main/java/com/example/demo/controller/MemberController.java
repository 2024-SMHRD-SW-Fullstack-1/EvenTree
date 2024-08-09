package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

	// 로그인
	@PostMapping("/login")
	public String login(@RequestBody MemberModel mv, String password) {
		MemberModel member = memberService.findByIdAndPw(mv.getId(), mv.getPw());
		
		
		if (member != null && member.getPw().equals(mv.getPw())) {
			String token = jwtTokenProvider.createToken(member.getId(), member.getMIdx(), member.getNick());
			System.out.println(token);
			return token;
		} else {
			System.out.println("실패");
			return "fail";
		}
	}
	
	@PostMapping("/delete")
	public ResponseEntity<String> delete(@RequestHeader("Authorization") String token) {
		// 토큰에서 Bearer 부분 제거
        String authToken = token.substring(7);
        
        // 토큰을 정제하여 사용
		String id = jwtTokenProvider.getId(authToken);
		
        if (id == null) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        boolean isDeleted = memberService.deleteMemberByUserId(id);

        if (isDeleted) {
            return new ResponseEntity<>("Member deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Member not found", HttpStatus.NOT_FOUND);
        }
	}
	
	@PostMapping("/updateNick")
    public ResponseEntity<Map<String, String>> updateNick(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> request) {
        try {
            String authToken = token.substring(7); // Bearer 부분 제거
            String id = jwtTokenProvider.getId(authToken);

            if (id == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            String newNick = request.get("newNick");
            if (newNick == null || newNick.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // 닉네임 업데이트
            MemberModel updatedMember = memberService.updateNick(id, newNick);

            // 새로운 토큰 생성
            String newToken = jwtTokenProvider.createToken(updatedMember.getId(), updatedMember.getMIdx(), updatedMember.getNick());

            Map<String, String> response = new HashMap<>();
            response.put("newToken", newToken);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	// 비밀번호 업데이트
    @PostMapping("/updatePw")
    public ResponseEntity<String> updatePw(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> request) {
        try {
            String authToken = token.substring(7); // Bearer 부분 제거
            String id = jwtTokenProvider.getId(authToken);
            System.out.println(request);
            if (id == null) {
                return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
            }

            String currentPw = request.get("currentPw");
            String newPw = request.get("newPw");

            // 현재 비밀번호 검증
            MemberModel member = memberService.findByIdAndPw(id, currentPw);
            if (member == null) {
                return new ResponseEntity<>("Current password is incorrect", HttpStatus.UNAUTHORIZED);
            }

            // 새로운 비밀번호 설정
            member.setPw(newPw);
            memberService.save(member);

            // 새로운 토큰 생성
            String newToken = jwtTokenProvider.createToken(member.getId(), member.getMIdx(), member.getNick());

            return ResponseEntity.ok(newToken);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
