package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.MemberModel;

@Repository
public interface MemberRepository extends JpaRepository<MemberModel, Integer> {
	
	MemberModel findByIdAndPw(String id, String pw);
}