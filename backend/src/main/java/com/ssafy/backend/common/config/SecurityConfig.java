package com.ssafy.backend.common.config;

import com.ssafy.backend.auth.service.jwt.JwtAuthenticationFilter;
import com.ssafy.backend.auth.service.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // iframe 오류 방지
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // JWT는 세션 사용 X !
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // URL 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // Swagger 허용
                        .requestMatchers(
                                "/images/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // 로그인/회원가입 허용
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 애니 검색 (Swagger 테스트 허용)
                        .requestMatchers("/api/v1/anime/**").permitAll()

                        // 알림 허용
                        .requestMatchers("/api/v1/notification/**").permitAll()

                        // 굿즈 목록 조회 허용
                        .requestMatchers("/api/v1/goods/list/**").permitAll()

                        // 찜 기준 인기 굿즈 목록 조회 허용
                        .requestMatchers("/api/v1/goods/hot-goods").permitAll()

                        // 애니메이션 초기 bulk 허용
                        .requestMatchers("/api/admin/anime/**").permitAll()

                        // 나머지 API 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터 추가
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
