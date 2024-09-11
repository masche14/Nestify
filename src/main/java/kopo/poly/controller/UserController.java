package kopo.poly.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/User")
public class UserController {

    // 로그인 페이지 표시 (GET 요청)
    @GetMapping("/signin")
    public String showSigninPage() {
        return "User/signin";
    }

    // 로그인 처리 (POST 요청)
    @PostMapping("/signin")
    public String processSignin(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        // 로그인 처리 로직
        // 사용자 인증, 세션 설정 등
        session.setAttribute("username", username);
        return "redirect:/home"; // 성공 시 리다이렉트
    }

    // 회원가입 페이지 표시 (GET 요청)
    @GetMapping("/signup_detail")
    public String showSignupPage() {
        return "User/signup_detail";
    }

    // 회원가입 처리 (POST 요청)
    @PostMapping("/signup_detail")
    public String processSignup(@RequestParam String username, @RequestParam String password, @RequestParam String email, Model model) {
        // 회원가입 처리 로직
        // 데이터베이스에 사용자 정보 저장 등
        return "redirect:/User/signin"; // 회원가입 성공 후 로그인 페이지로 리다이렉트
    }

    // 비밀번호 재설정 페이지 표시 (GET 요청)
    @GetMapping("/reset_pwd")
    public String showResetPwdPage() {
        return "User/reset_pwd";
    }

    // 비밀번호 재설정 처리 (POST 요청)
    @PostMapping("/reset_pwd")
    public String processResetPwd(@RequestParam String email, @RequestParam String newPassword, Model model) {
        // 비밀번호 재설정 로직
        // 데이터베이스에서 비밀번호 변경 등
        return "redirect:/User/signin"; // 비밀번호 재설정 후 로그인 페이지로 리다이렉트
    }

    // 이메일 인증 페이지 표시 (GET 요청)
    @GetMapping("/email_verification")
    public String showEmailVerificationPage() {
        return "User/email_verification";
    }

    // 이메일 인증 처리 (POST 요청)
    @PostMapping("/email_verification")
    public String processEmailVerification(@RequestParam String email, Model model) {
        // 이메일 인증 로직
        // 이메일 확인 후 처리 등
        return "redirect:/User/signup_detail"; // 이메일 인증 후 회원가입 페이지로 리다이렉트
    }

    // 아이디 찾기 결과 페이지 표시 (GET 요청)
    @GetMapping("/find_id")
    public String showFindIdPage() {
        return "User/find_id";
    }

    // 아이디 찾기 처리 (POST 요청)
    @PostMapping("/find_id")
    public String processFindId(@RequestParam String email, Model model) {
        // 아이디 찾기 로직
        // 이메일로 사용자 아이디 찾기 등
        return "User/find_id_result"; // 찾은 아이디 결과 페이지로 이동
    }
        // 홈 페이지 (index.jsp로 설정)
    @GetMapping("/index")
    public String showHomePage() {
        return "User/index"; // /WEB-INF/views/index.jsp
    }
}
