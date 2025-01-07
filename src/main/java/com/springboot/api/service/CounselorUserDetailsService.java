package com.springboot.api.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.springboot.api.repository.CounselorRepository;

import lombok.RequiredArgsConstructor;

@Service
@ConditionalOnProperty(name = "api.userdetails.implementation", havingValue = "counselorUserDetailsService")
@RequiredArgsConstructor
public class CounselorUserDetailsService implements UserDetailsService {

    private final CounselorRepository counselorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return counselorRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
