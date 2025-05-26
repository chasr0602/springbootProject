package com.example.greenprojectA.controller;

import com.example.greenprojectA.entity.Member;
import com.example.greenprojectA.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;

    // 가입대기 회원 목록
    @GetMapping("/memberList")
    public String memberList(Model model) {
        model.addAttribute("pendingMembers", memberService.getPendingMembers());
        return "admin/memberList";
    }

    // 승인 처리 (AJAX 또는 폼으로 호출 가능)
    @PostMapping("/approve/{memberId}")
    public String approveMember(@PathVariable Long memberId) {
        memberService.approveMember(memberId);
        return "redirect:/admin/memberList";
    }
}
