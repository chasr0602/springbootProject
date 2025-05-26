package com.example.greenprojectA.service;

import com.example.greenprojectA.dto.MemberDto;
import com.example.greenprojectA.entity.Company;
import com.example.greenprojectA.entity.Member;
import com.example.greenprojectA.repository.CompanyRepository;
import com.example.greenprojectA.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final CompanyRepository companyRepository;
  private final PasswordEncoder passwordEncoder;

  // 회원가입 처리
  public void registerMember(MemberDto dto) {
    // 아이디/이메일 중복 확인
    if (memberRepository.existsByUsername(dto.getUsername())) {
      throw new IllegalStateException("이미 사용 중인 아이디입니다.");
    }

    if (memberRepository.existsByEmail(dto.getEmail())) {
      throw new IllegalStateException("이미 사용 중인 이메일입니다.");
    }

    Company company = companyRepository.findById(dto.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회사 ID입니다."));

    Member member = Member.builder()
            .username(dto.getUsername())
            .password(passwordEncoder.encode(dto.getPassword()))
            .name(dto.getName())
            .email(dto.getEmail())
            .company(company)
            .memberLevel(1)  // 가입대기 상태
            .accountNonLocked(true)
            .build();

    memberRepository.save(member);
  }

  // 로그인 성공 시 사용자 정보 조회
  public Member findByUsername(String username) {
    return memberRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
  }

  // 기업 목록 조회 (회원가입용 드롭다운)
  public List<Company> getCompanyList() {
    return companyRepository.findAll();
  }
}
