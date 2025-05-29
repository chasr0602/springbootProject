package com.example.greenprojectA.repository;

import com.example.greenprojectA.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.greenprojectA.constant.Role;

import java.util.Optional;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByMid(String mid);
  Optional<Member> findByUsernameAndEmail(String username, String email);
  Optional<Member> findByMidAndEmail(String mid, String email);

  boolean existsByMid(String mid);
  boolean existsByEmail(String email);
  boolean existsByCompany_Idx(Long companyId);

  // 관리자 승인 기능을 위한 가입대기 회원 조회
  List<Member> findByRole(Role role);

  // 회원 검색
  List<Member> findByMidContainingOrUsernameContainingOrCompany_NameContaining(String mid, String username, String companyName);
}
