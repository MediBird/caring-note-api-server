package com.springboot.api.service;

import com.springboot.api.common.message.ExceptionMessages;
import com.springboot.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@ConditionalOnProperty(name = "api.userdetails.implementation", havingValue = "customUserDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        long id = Long.parseLong(username);
        return  userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(ExceptionMessages.USER_NOT_FOUND));

    }

}
