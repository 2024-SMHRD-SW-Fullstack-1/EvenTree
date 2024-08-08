package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.GroupModel;
import com.example.demo.repository.GroupRepository;

@Service
public class GroupService {
	
	@Autowired
	GroupRepository groupRepository;
	
	public void groupAdd(GroupModel gv) {
        groupRepository.save(gv);
    }

	public List<GroupModel> getGroupsByUserId(String id) {
        return groupRepository.findBymId(id);
    }
}
