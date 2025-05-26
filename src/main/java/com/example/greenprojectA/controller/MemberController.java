package com.example.greenprojectA.controller;

import com.example.greenprojectA.dto.MemberDto;
import com.example.greenprojectA.entity.Member;
import com.example.greenprojectA.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    // 로그인 페이지
    @GetMapping("/memberLogin")
    public String memberLoginGet() {
        return "member/memberLogin";
    }

    // 로그인 실패 시
    @GetMapping("/login/error")
    public String loginErrorGet(RedirectAttributes rttr) {
        rttr.addFlashAttribute("loginErrorMsg", "아이디 또는 비밀번호가 일치하지 않습니다.");
        return "redirect:/member/memberLogin";
    }

    // 로그인 성공 후
    @GetMapping("/memberLoginOk")
    public String memberLoginOk(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication,
                                RedirectAttributes rttr) {

        String mid = authentication.getName();  // username 기준
        Member member = memberService.findByMid(mid);
        int level = member.getMemberLevel();

        // 승인대기 or 탈퇴요청 회원은 로그인 차단
        if (level == 1) {  // 가입대기
            rttr.addFlashAttribute("message", "관리자의 승인이 필요합니다.");
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            return "redirect:/member/memberLogin";
        }

        if (level == 99) {  // 탈퇴 요청 회원
            rttr.addFlashAttribute("message", "탈퇴 요청 상태이므로 로그인할 수 없습니다.");
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            return "redirect:/member/memberLogin";
        }

        // 로그인 성공 처리
        rttr.addFlashAttribute("message", member.getUsername() + "님 로그인 되었습니다.");

        HttpSession session = request.getSession();
        session.setAttribute("sName", member.getUsername());

        String strLevel = switch (level) {
            case 0 -> "관리자";
            case 2 -> "일반회원";
            default -> "알 수 없음";
        };
        session.setAttribute("strLevel", strLevel);

        return "redirect:/member/memberMain";
    }

    // 회원 메인페이지
    @GetMapping("/memberMain")
    public String memberMain() {
        return "member/memberMain";
    }

    // 로그아웃
    @GetMapping("/memberLogout")
    public String memberLogout(HttpServletRequest request,
                               HttpServletResponse response,
                               RedirectAttributes rttr) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String mid = authentication.getName();
            Member member = memberService.findByMid(mid);
            rttr.addFlashAttribute("message", member.getUsername() + "님 로그아웃 되었습니다.");
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/member/memberLogin";
    }

    // 회원가입 폼
    @GetMapping("/memberJoin")
    public String memberJoinForm(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        model.addAttribute("companyList", memberService.getCompanyList());
        return "member/memberJoin";
    }

    // 회원가입 처리
    @PostMapping("/memberJoin")
    public String memberJoinSubmit(@Valid @ModelAttribute MemberDto memberDto,
                                   BindingResult bindingResult,
                                   RedirectAttributes rttr,
                                   Model model) {

        // 비밀번호 확인 검증
        if (!memberDto.isPasswordConfirmed()) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "비밀번호가 일치하지 않습니다.");
        }

        // 유효성 검사 실패
        if (bindingResult.hasErrors()) {
            model.addAttribute("companyList", memberService.getCompanyList());
            return "member/memberJoin";
        }

        // 회원 등록 처리
        try {
            memberService.registerMember(memberDto);
            rttr.addFlashAttribute("message", "회원가입 요청이 완료되었습니다. 관리자의 승인을 기다려주세요.");
            return "redirect:/member/memberLogin";
        } catch (IllegalStateException e) {
            rttr.addFlashAttribute("message", "중복된 아이디 또는 이메일입니다.");
            return "redirect:/member/memberJoin";
        }
    }

    // 탈퇴 요청 처리
    @PostMapping("/requestQuit")
    public String requestQuit(Authentication authentication,
                              RedirectAttributes rttr) {
        String mid = authentication.getName();
        memberService.requestQuit(mid);
        rttr.addFlashAttribute("message", "탈퇴 요청이 완료되었습니다. 7일의 유예기간 후 완전 삭제됩니다.");
        return "redirect:/member/memberLogout";
    }

}
