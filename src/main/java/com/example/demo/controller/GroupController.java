package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.model.GroupModel;
import com.example.demo.service.GroupService;

@RestController
public class GroupController {
	
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@PostMapping("/group")
    public String groupJoin(@RequestHeader("X-AUTH-TOKEN") String token, @RequestBody GroupModel gv) {
        String id = jwtTokenProvider.getId(token);

        // 그룹 모델에 사용자 ID 설정
        gv.setMId(id);
        System.out.println("token id : " + id);
        gv.getGroupName();
        System.out.println("groupName : " + gv.getGroupName());
        gv.setType(gv.getType());
        System.out.println("groupType : " + gv.getType());
        gv.setRole("y");

        // 그룹 추가 서비스 호출
        groupService.groupAdd(gv);

        return "OK";
    }
	
	@GetMapping("/groups")
    public List<GroupModel> getGroups(@RequestHeader("X-AUTH-TOKEN") String token) {
        String id = jwtTokenProvider.getId(token);
        return groupService.getGroupsByUserId(id);
    }
	
	
	
}