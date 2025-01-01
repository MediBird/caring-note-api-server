package com.springboot.api.common.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationProvider jwtAuthProvider;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final SecurityProperties securityProperties;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

            http.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                            .authorizeHttpRequests(auth -> auth
                                            .requestMatchers(
                                                    securityProperties.getPermitAllUrls().toArray(new String[0]))
                                            .permitAll() // 인증 없이 접근 가능
                                            .anyRequest().authenticated() // 나머지 요청은 인증 필요
                            )
                            .sessionManagement(session -> session
                                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않음
                            )
                            .authenticationProvider(jwtAuthProvider) // 커스텀 AuthenticationProvider 사용
                            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                            .headers(headers -> headers
                                            .contentSecurityPolicy(csp -> csp
                                                            .policyDirectives("frame-ancestors 'self'")));

            return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://caringnote.co.kr", "http://localhost:3000","http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization","Access-Token","Uid","Refresh-Token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
