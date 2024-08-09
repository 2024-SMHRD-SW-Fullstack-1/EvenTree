package com.example.demo.service;

import java.util.Optional;

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
    
    public Optional<MemberModel> findById(String id){
    	return memberRepository.findById(id);
    }
    
    // 회원 탈퇴
    public void deleteById(String id) {
    	memberRepository.deleteById(id);
    }
    
    // 사용자 ID로 회원 삭제
    public boolean deleteMemberByUserId(String userId) {
        Optional<MemberModel> member = memberRepository.findById(userId);
        if (member.isPresent()) {
            memberRepository.deleteById(member.get().getMIdx());
            return true;
        }
        return false;
    }
    
    // nick 업데이트
    public MemberModel updateNick(String id, String newNick) {
        Optional<MemberModel> memberOpt = memberRepository.findById(id);
        if (memberOpt.isPresent()) {
            MemberModel member = memberOpt.get();
            member.setNick(newNick);
            memberRepository.save(member);
            return member;
        } else {
            throw new RuntimeException("Member not found");
        }
    }
    
    // pw 업데이트
    public void save(MemberModel member) {
        memberRepository.save(member);
    }
}
