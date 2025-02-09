package com.springboot.api.common.converter;

import com.springboot.api.domain.Counselor;
import com.springboot.api.repository.CounselorRepository;
import com.springboot.enums.CounselorStatus;
import com.springboot.enums.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

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
            return authorities;
        }

        // DB에서 사용자 정보 조회
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            String email = jwt.getClaimAsString("email");
            String name = jwt.getClaimAsString("name");
            String phoneNumber = jwt.getClaimAsString("phone_number");
            Counselor newCounselor = new Counselor();
            newCounselor.setUsername(username);
            newCounselor.setName(name);
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
