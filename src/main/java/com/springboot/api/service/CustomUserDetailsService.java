package com.springboot.api.service;

import com.springboot.api.common.message.ExceptionMessages;
import com.springboot.api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        long id = Long.parseLong(username);
        return  userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(ExceptionMessages.USER_NOT_FOUND));

    }

}
