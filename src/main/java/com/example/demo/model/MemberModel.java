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
	private int mIdx;
	
	@Column(name="m_id", length=30, nullable=false, unique=true)
	private String id;
	
	@Column(name="m_pw", length=50, nullable=false)
	private String pw;
	
	@Column(name="m_nick", length=50, nullable=false)
	private String nick;

}
