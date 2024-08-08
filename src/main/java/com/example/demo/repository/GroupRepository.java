package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.GroupModel;

@Repository
public interface GroupRepository extends JpaRepository<GroupModel, Integer> {
	List<GroupModel> findBymId(String mId);
}
