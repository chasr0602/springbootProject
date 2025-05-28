package com.example.greenprojectA.controller;

import com.example.greenprojectA.constant.Role;
import com.example.greenprojectA.entity.Member;
import com.example.greenprojectA.entity.Company;
import com.example.greenprojectA.service.MemberService;
import com.example.greenprojectA.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;
    private final CompanyService companyService;

    // 회원 리스트 + 검색
    @GetMapping("/member")
    public String showMemberList(@RequestParam(required = false) String keyword, Model model) {
        List<Member> memberList;

        if (keyword != null && !keyword.isEmpty()) {
            memberList = memberService.searchByKeyword(keyword);
        } else {
            memberList = memberService.findAll();
        }

        model.addAttribute("memberList", memberList);
        return "admin/memberList";
    }

    // 회원가입 승인 (PENDING → USER)
    @PostMapping("/approve")
    public String approveMember(@RequestParam Long memberId) {
        memberService.changeRole(memberId, Role.USER);
        return "redirect:/admin/member";
    }

    // 탈퇴 회원 처리 (WITHDRAWN → 삭제 or 유지)
    @PostMapping("/delete")
    public String deleteWithdrawnMember(@RequestParam Long memberId) {
        memberService.deleteIfWithdrawn(memberId);
        return "redirect:/admin/member";
    }

    // 기업 리스트 + 등록
    @GetMapping("/company")
    public String showCompanyPage(Model model) {
        model.addAttribute("company", new Company());
        model.addAttribute("companyList", companyService.findAll());
        return "admin/company";
    }

    // 기업 추가 처리
    @PostMapping("/company")
    public String addCompany(@ModelAttribute Company company) {
        companyService.addCompany(company);
        return "redirect:/admin/company";
    }
}