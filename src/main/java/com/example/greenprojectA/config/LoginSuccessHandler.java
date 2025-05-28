package com.example.greenprojectA.config;

import jakarta.servlet.ServletException;
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
        LoginFailHandler.failCountMap.remove(username);
        LoginFailHandler.lockUntilMap.remove(username);

        response.sendRedirect("/member/memberLoginOk");
    }
}
