package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.MemberModel;
import com.example.demo.repository.MemberRepository;

@Service
public class MemberService {

	@Autowired
	MemberRepository memberRepository;

	public void join(MemberModel mv) {
		memberRepository.save(mv);
	}


	// 로그인
    public MemberModel findByIdAndPw(String id, String pw) {
        return memberRepository.findByIdAndPw(id, pw);
    }
}
