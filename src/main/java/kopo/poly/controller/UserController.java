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

    // 로그인 페이지
    @GetMapping("/signin")
    public String showLoginPage(HttpSession session, Model model) {
        String savedId = (String) session.getAttribute("savedId");
        if (savedId != null) {
            model.addAttribute("savedId", savedId);
        }
        return "User/signin"; // 정확한 경로로 수정
    }

    // 이메일 인증 페이지로 이동
//    @PostMapping("/email_verification")
//    public String verifyEmail(
//            @RequestParam("id") String id,
//            @RequestParam("pwd") String pwd,
//            @RequestParam("source") String source,
//            HttpSession session) {
//        session.setAttribute("savedId", id); // 아이디 저장
//        session.setAttribute("source", source); // source 저장
//        return "redirect:User/email_verification"; // 이메일 인증 페이지로 이동
//    }

    // 이메일 인증 페이지
    @GetMapping("/email_verification")
    public String showEmailVerificationPage() {
        return "User/email_verification"; // /WEB-INF/views/email_verification.jsp
    }

    // 이메일 인증 처리
//    @PostMapping("/processEmail")
//    public String processEmail(
//            @RequestParam("email") String email,
//            HttpSession session) {
//        System.out.println("저장할 이메일: " + email);
//        session.setAttribute("userEmail", email); // 이메일 저장
//        String source = (String) session.getAttribute("source");
//
//        switch (source) {
//            case "signup":
//                return "redirect:User/signup_detail"; // 회원가입 상세 페이지로 이동
//            case "find_id":
//                return "redirect:User/find_id"; // 아이디 찾기 결과 페이지로 이동
//            case "reset_pwd":
//                return "redirect:User/reset_pwd"; // 비밀번호 재설정 페이지로 이동
//            default:
//                return "redirect:User/index"; // 기본적으로 홈 페이지로 이동
//        }
//    }

    // 회원가입 상세 페이지
    @GetMapping("/signup_detail")
    public String showSignupDetailPage() {
        return "User/signup_detail"; // /WEB-INF/views/signup_detail.jsp
    }

    // 아이디 찾기 결과 페이지
    @GetMapping("/find_id")
    public String showFindIdResultPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");
        String userId = null;

        if ("2420110173@gspace.kopo.ac.kr".equals(email)) {
            model.addAttribute("userName", "USER01");
            userId = "masche";
        } else {
            return "redirect:signup_detail"; // 회원가입 상세 페이지로 이동
        }

        session.setAttribute("savedId", userId); // 아이디 저장
        model.addAttribute("userId", userId);
        return "User/find_id"; // /WEB-INF/views/find_id.jsp
    }

    // 비밀번호 재설정 페이지
    @GetMapping("/reset_pwd")
    public String showResetPwdPage(HttpSession session, Model model) {
        String savedId = (String) session.getAttribute("savedId");
        if (savedId != null) {
            model.addAttribute("savedId", savedId);
        }
        return "User/reset_pwd"; // /WEB-INF/views/reset_pwd.jsp
    }

    // 회원가입 처리
    @PostMapping("/processSignup")
    public String processSignup(
            @RequestParam("id") String id,
            HttpSession session) {
        session.setAttribute("savedId", id); // 가입된 아이디 저장
        return "redirect:User/signin"; // 로그인 페이지로 리다이렉트
    }

    // 홈 페이지 (index.jsp로 설정)
    @GetMapping("/index")
    public String showHomePage() {
        return "User/index"; // /WEB-INF/views/index.jsp
    }

    // 인테리어 페이지
    @GetMapping("/interior")
    public String showInteriorPage() {
        return "User/interior"; // /WEB-INF/views/interior.jsp
    }
}
