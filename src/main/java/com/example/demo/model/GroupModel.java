package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_group")
@Data
public class GroupModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "g_idx")
	private int gIdx;
	
	@Column(name = "g_name")
	private String groupName;
	
	@Column(name = "m_id")
	private String mId;
	
	@Column(name = "is_role")
	private String role;
	
	@Column(name = "g_type")
	private String type;
}
