package com.springboot.api.service;

import com.springboot.api.repository.CounselorRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "api.userdetails.implementation", havingValue = "counselorUserDetailsService")
public class CounselorUserDetailsService implements UserDetailsService {

    private final CounselorRepository counselorRepository;

    public CounselorUserDetailsService(CounselorRepository counselorRepository) {
        this.counselorRepository = counselorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return counselorRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
