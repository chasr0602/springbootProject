package com.example.greenprojectA.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    // 사용자 정의 로그인 폼 설정
    http.formLogin(form -> form
            .loginPage("/member/memberLogin")
            .defaultSuccessUrl("/member/memberLoginOk", true)
            .failureUrl("/member/login/error")
            .usernameParameter("username") // email → username 으로 변경 (엔티티 기준)
            .permitAll()
    );

    // 요청 URL별 접근 권한 설정
    http.authorizeHttpRequests(request -> request
            .requestMatchers("/css/**", "/images/**", "/guest/**").permitAll()
            .requestMatchers("/member/memberLogin", "/member/login/error").permitAll()
            .requestMatchers("/admin/**").authenticated() // 관리자 권한은 member_level로 추후 필터링
            .requestMatchers("/member/memberMain").authenticated()
            .anyRequest().authenticated()
    );

    // 권한 예외 처리
    http.exceptionHandling(exception -> exception
            .accessDeniedPage("/error/accessDenied")
    );

    // 로그아웃 기본 처리
    http.logout(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
