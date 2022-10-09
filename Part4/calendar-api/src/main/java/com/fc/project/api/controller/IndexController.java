package com.fc.project.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

import static com.fc.project.api.service.LoginService.LOGIN_SESSION_KEY;

/**
 * 홈 화면
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public String index(Model model, HttpSession httpSession, @RequestParam(required = false) String redirect) {
        model.addAttribute("isSignIn", httpSession.getAttribute(LOGIN_SESSION_KEY) != null);
        model.addAttribute("redirect", redirect);

        return "index";
    }
}
