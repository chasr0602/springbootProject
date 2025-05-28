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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.greenprojectA.constant.Role;

import java.util.Map;
import java.util.Random;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final JavaMailSender mailSender;
    private final HttpSession session;

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

        String mid = authentication.getName();
        Member member = memberService.findByMid(mid);

        Role role = member.getRole();  // 이제 enum 사용

        if (role == Role.PENDING) {
            rttr.addFlashAttribute("message", "관리자의 승인이 필요합니다.");
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            return "redirect:/member/memberLogin";
        }

        if (role == Role.WITHDRAWN) {
            rttr.addFlashAttribute("message", "탈퇴 요청 상태이므로 로그인할 수 없습니다.");
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            return "redirect:/member/memberLogin";
        }

        rttr.addFlashAttribute("message", member.getUsername() + "님 로그인 되었습니다.");
        HttpSession session = request.getSession();
        session.setAttribute("sName", member.getUsername());

        String strLevel = switch (role) {
            case ADMIN -> "관리자";
            case USER -> "일반회원";
            default -> "알 수 없음";
        };
        session.setAttribute("strLevel", strLevel);

        return "redirect:/member/memberMain";
    }

    // 회원 메인페이지
    @GetMapping("/memberMain")
    public String memberMain(@ModelAttribute("message") String message) {
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
        MemberDto dto = new MemberDto();
        dto.setCompanyId(null);
        model.addAttribute("memberDto", dto);
        model.addAttribute("companyList", memberService.getCompanyList());
        return "member/memberJoin";
    }

    // 회원가입 처리
    @PostMapping("/memberJoin")
    public String memberJoinSubmit(@Valid @ModelAttribute MemberDto memberDto,
                                   BindingResult bindingResult,
                                   RedirectAttributes rttr,
                                   Model model) {

        // 이메일 인증 확인
        Boolean isVerified = (Boolean) session.getAttribute("emailVerified");
        if (isVerified == null || !isVerified) {
            rttr.addFlashAttribute("message", "이메일 인증을 완료해주세요.");
            return "redirect:/member/memberJoin";
        }

        // 비밀번호 확인 검증
        if (!memberDto.isPasswordConfirmed()) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "비밀번호가 일치하지 않습니다.");
            rttr.addFlashAttribute("message", "비밀번호가 일치하지 않습니다.");
            return "redirect:/member/memberJoin";
        }

        // 유효성 검사 실패
        if (bindingResult.hasErrors()) {
            model.addAttribute("companyList", memberService.getCompanyList());
            return "member/memberJoin";
        }

        try {
            memberService.registerMember(memberDto);
            session.removeAttribute("emailVerified");
            rttr.addFlashAttribute("message", memberDto.getUsername() + "님, 회원가입 요청이 완료되었습니다.\n관리자의 승인을 기다려주세요.");
            return "redirect:/member/memberLogin";
        } catch (IllegalStateException e) {
            e.printStackTrace();
            rttr.addFlashAttribute("message", "중복된 아이디 또는 이메일입니다.");
            return "redirect:/member/memberJoin";
        } catch (Exception e) {
            e.printStackTrace();
            rttr.addFlashAttribute("message", "회원가입에 실패하였습니다. 관리자에게 문의해주세요.");
            return "redirect:/member/memberJoin";
        }
    }

    // 아이디 중복 체크
    @ResponseBody
    @GetMapping("/idCheck")
    public String idCheck(@RequestParam String mid) {
        return memberService.isMidExists(mid) ? "1" : "0";
    }

    // 탈퇴 요청 처리
    @PostMapping("/requestQuit")
    public String requestQuit(Authentication authentication,
                              RedirectAttributes rttr) {
        String mid = authentication.getName();
        memberService.requestQuit(mid);
        rttr.addFlashAttribute("message", "탈퇴 요청이 완료되었습니다. 7일의 유예기간 후 완전히 삭제됩니다.");
        return "redirect:/member/memberLogout";
    }

    // 이메일 인증번호 전송
    @PostMapping("/sendCode")
    @ResponseBody
    public String sendEmailCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = String.valueOf(new Random().nextInt(900000) + 100000);
        session.setAttribute("emailCode", code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[GreenProjectA] 이메일 인증코드");
        message.setText("인증번호: " + code);

        try {
            mailSender.send(message);
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    // 인증번호 확인
    @PostMapping("/verifyCode")
    @ResponseBody
    public String verifyEmailCode(@RequestBody Map<String, String> payload) {
        String inputCode = payload.get("code");
        String savedCode = (String) session.getAttribute("emailCode");

        if (savedCode != null && savedCode.equals(inputCode)) {
            session.setAttribute("emailVerified", true);
            return "success";
        } else {
            return "fail";
        }
    }
}