package com.example.greenprojectA.config;

import com.example.greenprojectA.entity.Member;
import com.example.greenprojectA.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberLoginService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String mid) throws UsernameNotFoundException {
        Member member = memberRepository.findByMid(mid)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디입니다."));

        // Role 기준 권한 설정
        String role = switch (member.getRole()) {
            case ADMIN -> "ROLE_ADMIN";
            case USER -> "ROLE_USER";
            case PENDING -> throw new LockedException("관리자 승인 대기 중입니다.");
            case WITHDRAWN -> throw new DisabledException("탈퇴 요청 회원입니다.");
            default -> throw new UsernameNotFoundException("유효하지 않은 회원 상태입니다.");
        };

        return User.builder()
                .username(member.getMid()) // 로그인에 사용되는 ID
                .password(member.getPassword()) // 암호화된 비밀번호
                .authorities(List.of(new SimpleGrantedAuthority(role)))
                .build();
    }
}
