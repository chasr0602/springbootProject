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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String approveMember(@RequestParam Long memberId, RedirectAttributes rttr) {
        memberService.changeRole(memberId, Role.USER);
        rttr.addFlashAttribute("message", "가입 승인되었습니다.");
        return "redirect:/admin/member";
    }


    // 탈퇴 회원 처리 (WITHDRAWN → 삭제 or 유지)
    @PostMapping("/delete")
    public String deleteMember(@RequestParam("memberId") Long memberId, RedirectAttributes rttr) {
        try {
            memberService.deleteIfWithdrawn(memberId);
            rttr.addFlashAttribute("message", "회원이 삭제되었습니다.");
        } catch (IllegalStateException e) {
            rttr.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/admin/member";
    }

    // 기업 리스트 + 등록
    @GetMapping("/company")
    public String showCompanyPage(Model model) {
        model.addAttribute("company", new Company());
        model.addAttribute("companyList", companyService.findAll());
        return "admin/company";
    }

    // 기업 등록, 수정 처리
    @PostMapping("/company")
    public String saveOrUpdateCompany(@ModelAttribute Company company, RedirectAttributes rttr) {
        if (company.getIdx() == null) {
            companyService.addCompany(company);
            rttr.addFlashAttribute("message", "기업이 등록되었습니다.");
        } else {
            companyService.updateCompany(company);
            rttr.addFlashAttribute("message", "기업 정보가 수정되었습니다.");
        }
        return "redirect:/admin/company";
    }

    // 기업 삭제 처리
    @PostMapping("/company/delete")
    public String deleteCompany(@RequestParam Long id, RedirectAttributes rttr) {
        try {
            companyService.deleteCompany(id);
            rttr.addFlashAttribute("message", "기업이 삭제되었습니다.");
        } catch (IllegalStateException e) {
            rttr.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/admin/company";
    }

}