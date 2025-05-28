package com.example.greenprojectA.service;

import com.example.greenprojectA.constant.Role;
import com.example.greenprojectA.dto.MemberDto;
import com.example.greenprojectA.entity.Company;
import com.example.greenprojectA.entity.Member;
import com.example.greenprojectA.repository.CompanyRepository;
import com.example.greenprojectA.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final CompanyRepository companyRepository;
  private final PasswordEncoder passwordEncoder;

  // 아이디 중복 확인
  public boolean isMidExists(String mid) {
    return memberRepository.existsByMid(mid);
  }

  // 회원가입 처리
  public void registerMember(MemberDto dto) {
    if (memberRepository.existsByMid(dto.getMid())) {
      throw new IllegalStateException("이미 사용 중인 아이디입니다.");
    }

    Company company = companyRepository.findById(dto.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회사 ID입니다."));

    Member member = Member.builder()
            .mid(dto.getMid())
            .username(dto.getUsername())
            .password(passwordEncoder.encode(dto.getPassword()))
            .email(dto.getEmail())
            .tel(dto.getTel())
            .address((dto.getAddress()))
            .company(company)
            .role(Role.PENDING)  // 가입대기 상태
            .build();

    memberRepository.save(member);
  }

  // 로그인 성공 시 사용자 정보 조회
  public Member findByMid(String mid) {
    return memberRepository.findByMid(mid)
            .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
  }

  // 기업 목록 조회 (회원가입용 드롭다운)
  public List<Company> getCompanyList() {
    return companyRepository.findAll();
  }

  // 가입대기 회원 조회 (관리자 페이지)
  public List<Member> getPendingMembers() {
    return memberRepository.findByRole(Role.PENDING);
  }

  // 관리자 승인 처리
  public void approveMember(Long memberId) {
    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
    member.setRole(Role.USER);
    memberRepository.save(member);
  }

  // 탈퇴 요청 처리
  public void requestQuit(String mid) {
    Member member = memberRepository.findByMid(mid)
            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));
    member.setRole(Role.WITHDRAWN);
    member.setQuitRequestedAt(LocalDateTime.now());
    memberRepository.save(member);
  }

}
