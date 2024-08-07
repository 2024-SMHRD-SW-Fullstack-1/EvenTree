package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity // JPA 관리
@Table(name="member")
@Data
public class MemberModel {
	
	@Id //primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
	@Column(name="m_idx")
	private Long mIdx;
	
	@Column(name="m_id", length=30)
	private String id;
	
	@Column(name="m_pw", length=50)
	private String pw;
	
	@Column(name="m_email", length=100)
	private String email;

}
