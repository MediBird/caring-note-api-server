package com.springboot.api.common.converter;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.counselor.repository.CounselorRepository;
import com.springboot.enums.CounselorStatus;
import com.springboot.enums.RoleType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomJwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final UserDetailsService userDetailsService;
    private final CounselorRepository counselorRepository;

    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        // JWT에서 사용자 ID나 이메일 추출
        String username = jwt.getClaimAsString("preferred_username");

        if (username == null) {
            Collection<String> grantedAuthoritiesClaim = jwt.getClaimAsStringList("Granted Authorities");
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            if (grantedAuthoritiesClaim != null) {
                for (String authority : grantedAuthoritiesClaim) {
                    authorities.add(new SimpleGrantedAuthority(authority));
                }
            }
            log.info("authorities: {}", authorities);
            return authorities;
        }

        // DB에서 사용자 정보 조회
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            String email = jwt.getClaimAsString("email");

            // 성과 이름을 별도로 가져오기
            String familyName = jwt.getClaimAsString("family_name");
            String givenName = jwt.getClaimAsString("given_name");
            String name = jwt.getClaimAsString("name");

            // 한국식 이름 처리 로직
            String fullName;
            if (familyName != null && givenName != null) {
                // 성과 이름이 모두 있는 경우
                // 한글 문자 범위 체크 (유니코드: AC00-D7A3)
                boolean isKoreanName = (familyName + givenName).chars()
                        .anyMatch(c -> c >= 0xAC00 && c <= 0xD7A3);

                if (isKoreanName) {
                    // 한국식: 성+이름 (공백 없음)
                    fullName = familyName + givenName;
                    log.info("한국어 이름 감지 및 처리: {}", fullName);
                } else {
                    // 서양식: 이름 성
                    fullName = givenName + " " + familyName;
                    log.info("서양식 이름 처리: {}", fullName);
                }
            } else {
                // 분리된 성/이름이 없는 경우 기존 name 클레임 사용
                fullName = name;
                log.info("전체 이름 사용: {}", fullName);
            }

            String phoneNumber = jwt.getClaimAsString("phone_number");
            Counselor newCounselor = new Counselor();
            newCounselor.setUsername(username);
            newCounselor.setName(fullName); // 처리된 전체 이름 사용
            newCounselor.setEmail(email);
            newCounselor.setPhoneNumber(phoneNumber);
            newCounselor.setStatus(CounselorStatus.ACTIVE);
            newCounselor.setRoleType(RoleType.ROLE_NONE);

            counselorRepository.save(newCounselor);
            return new ArrayList<>(newCounselor.getAuthorities());
        }
        // 사용자의 권한 반환
        return new ArrayList<>(userDetails.getAuthorities());
    }
}
