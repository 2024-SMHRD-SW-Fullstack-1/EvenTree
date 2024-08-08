package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.MemberModel;

@Repository
<<<<<<< HEAD
public interface MemberRepository extends JpaRepository<MemberModel, Long> {
=======
public interface MemberRepository extends JpaRepository<MemberModel, Integer> {
>>>>>>> e34ea5f4036e902017a854ae7d2a07109d4daf27
	
	MemberModel findByIdAndPw(String id, String pw);
}