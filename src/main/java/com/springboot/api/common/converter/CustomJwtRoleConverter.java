package com.springboot.api.common.converter;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.springboot.api.domain.Counselor;
import com.springboot.api.repository.CounselorRepository;
import com.springboot.enums.CounselorStatus;
import com.springboot.enums.RoleType;

import io.micrometer.common.lang.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomJwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final UserDetailsService userDetailsService;
    private final CounselorRepository counselorRepository;

    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        // JWT에서 사용자 ID나 이메일 추출
        String username = jwt.getClaimAsString("preferred_username");

        // DB에서 사용자 정보 조회
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            String email = jwt.getClaimAsString("email");
            String name = jwt.getClaimAsString("name");
            String phoneNumber = jwt.getClaimAsString("phone_number");
            Counselor newCounselor = new Counselor();
            newCounselor.setName(name);
            newCounselor.setEmail(email);
            newCounselor.setPhoneNumber(phoneNumber);
            newCounselor.setStatus(CounselorStatus.ACTIVE);
            newCounselor.setRoleType(RoleType.ROLE_ADMIN);
            // 필요한 다른 필드 설정
            counselorRepository.save(newCounselor); // 저장 로직 추가
            return new ArrayList<>(newCounselor.getAuthorities());
        }

        // 사용자의 권한 반환
        return new ArrayList<>(userDetails.getAuthorities());
    }
}
