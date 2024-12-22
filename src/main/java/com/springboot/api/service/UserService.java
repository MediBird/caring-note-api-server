package com.springboot.api.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.springboot.api.common.exception.DuplicatedEmailException;
import com.springboot.api.domain.Role;
import com.springboot.api.domain.User;
import com.springboot.api.dto.user.AddUserReq;
import com.springboot.api.dto.user.AddUserRes;
import com.springboot.api.repository.RoleRepository;
import com.springboot.api.repository.UserRepository;
import com.springboot.enums.RoleType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public AddUserRes addUser(AddUserReq addUserReq) throws RuntimeException {

        User user = User.builder()
                .email(addUserReq.getEmail())
                .username(addUserReq.getUsername())
                .password("encodedPassword")
                .build();
        List<String> roleNames = addUserReq.getRole()
                .stream()
                .map(RoleType::name)
                .toList();

        if (userRepository.existsByEmail(addUserReq.getEmail())) {
            throw new DuplicatedEmailException();
        }

        List<Role> roles = roleRepository.findByNameIn(roleNames);

        HashSet<Role> roleHashSet = new HashSet<>(roles);
        user.setRoles(roleHashSet);

        User savedUser = userRepository.save(user);

        return AddUserRes.builder()
                .id(savedUser.getId())
                .roles(savedUser.getRoles()
                        .stream()
                        .map(Role::getName)
                        .toList())
                .build();
    }
}
