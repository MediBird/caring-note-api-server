package com.springboot.api.service;

import com.springboot.api.common.exception.DuplicatedEmailException;
import com.springboot.api.domain.Role;
import com.springboot.api.domain.RoleType;
import com.springboot.api.domain.User;
import com.springboot.api.dto.user.AddUserReq;
import com.springboot.api.dto.user.AddUserRes;
import com.springboot.api.repository.RoleRepository;
import com.springboot.api.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;



@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }
    @Transactional
    public AddUserRes addUser(AddUserReq addUserReq) throws RuntimeException {


        // 비밀번호를 암호화하여 저장
        String encodedPassword = passwordEncoder.encode(addUserReq.getPassword());

        User user = User.builder()
                .email(addUserReq.getEmail())
                .username(addUserReq.getUsername())
                .password(encodedPassword)
                .build();
        List<String> roleNames = addUserReq.getRole()
                .stream()
                .map(RoleType::name)
                .toList();


        if(userRepository.existsByEmail(addUserReq.getEmail()))
        {
            throw new DuplicatedEmailException();
        }


        List<Role> roles = roleRepository.findByNameIn(roleNames);

        HashSet<Role> roleHashSet = new HashSet<>(roles);
        user.setRoles(roleHashSet);


        User savedUser = userRepository.save(user);

        return  AddUserRes.builder()
                .id(savedUser.getId())
                .roles(savedUser.getRoles()
                        .stream()
                        .map(Role::getName)
                        .toList())
                .build();
    }
}
