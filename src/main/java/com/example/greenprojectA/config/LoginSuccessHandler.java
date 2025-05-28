package com.example.greenprojectA.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)
            throws ServletException, IOException {
        String username = authentication.getName();

        // 로그인 실패 관련 캐시 초기화
        LoginFailHandler.failCountMap.remove(username);
        LoginFailHandler.lockUntilMap.remove(username);

        // 아이디 기억하기 쿠키 저장
        String remember = request.getParameter("rememberMe");
        if ("on".equals(remember)) {
            Cookie cookie = new Cookie("rememberId", username);
            cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
            cookie.setPath("/");
            response.addCookie(cookie);
        } else {
            Cookie cookie = new Cookie("rememberId", null);
            cookie.setMaxAge(0); // 즉시 삭제
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        response.sendRedirect("/member/memberLoginOk");
    }
}
