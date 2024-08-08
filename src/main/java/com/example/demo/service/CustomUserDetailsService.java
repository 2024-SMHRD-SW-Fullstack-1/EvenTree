package com.example.demo.service;

import com.example.demo.model.MemberModel;
import com.example.demo.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        MemberModel member = memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + id));
        
        return org.springframework.security.core.userdetails.User
                .withUsername(member.getId())
                .password(member.getPw())
                .authorities("USER")  // 역할이 있으면 여기서 설정
                .build();
    }
}
