package com.springboot.api.config.security;

import com.springboot.api.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;  // JWT 관련 유틸리티 클래스

    public JwtAuthenticationProvider(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String jwtToken = (String) authentication.getCredentials();

        // JWT에서 사용자 이름 추출
        String email = jwtUtil.extractUsername(jwtToken);

        // UserDetailsService를 통해 사용자 정보 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // JWT 토큰이 유효한지 검증
        if (jwtUtil.validateToken(jwtToken, userDetails)) {
            // 인증 성공 시 UsernamePasswordAuthenticationToken을 반환
            return new UsernamePasswordAuthenticationToken(userDetails, jwtToken, userDetails.getAuthorities());
        }

        // 유효하지 않은 경우 null을 반환하여 인증 실패 처리
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 지원하는 인증 토큰 타입을 지정 (여기서는 String 타입의 JWT 토큰을 처리)
        return String.class.isAssignableFrom(authentication);
    }
}