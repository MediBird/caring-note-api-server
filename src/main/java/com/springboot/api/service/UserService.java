package com.springboot.api.service;

import com.springboot.api.common.exception.DuplicatedEmailException;
import com.springboot.api.common.exception.InvalidPasswordException;
import com.springboot.api.common.message.ExceptionMessages;
import com.springboot.api.domain.Role;
import com.springboot.api.domain.User;
import com.springboot.api.dto.user.AddUserReq;
import com.springboot.api.dto.user.AddUserRes;
import com.springboot.api.dto.user.LoginReq;
import com.springboot.api.dto.user.LoginRes;
import com.springboot.api.repository.RoleRepository;
import com.springboot.api.repository.UserRepository;
import com.springboot.enums.RoleType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


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

    public LoginRes login(LoginReq loginReq) {

        Optional<User> user = userRepository.findByEmail(loginReq.getEmail());

        if (user.isEmpty()) {
            throw new UsernameNotFoundException(ExceptionMessages.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(loginReq.getPassword(), user.get().getPassword())) {
            throw new InvalidPasswordException();
        }

        return LoginRes.builder()
                .id(user.get().getId())
                .roles(user.get().getRoles()
                        .stream()
                        .map(Role::getName)
                        .toList())
                .build();
    }
}
