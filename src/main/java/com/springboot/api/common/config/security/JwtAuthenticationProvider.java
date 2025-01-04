package com.springboot.api.common.config.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.springboot.api.domain.Counselor;
import com.springboot.api.repository.CounselorRepository;
import com.springboot.api.service.CounselorUserDetailsService;
import com.springboot.enums.CounselorStatus;
import com.springboot.enums.RoleType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtDecoder jwtDecoder;
    private final CounselorUserDetailsService counselorUserDetailsService;
    private final CounselorRepository counselorRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String username = jwt.getClaimAsString("preferred_username");

            // DB에서 사용자 정보 조회
            UserDetails userDetails = counselorUserDetailsService.loadUserByUsername(username);
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
                return new JwtAuthenticationToken(jwt, newCounselor.getAuthorities());
            } else {
                return new JwtAuthenticationToken(jwt, userDetails.getAuthorities());
            }
        } catch (JwtException e) {
            throw new AuthenticationException("JWT 검증 실패", e) {
            };
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
