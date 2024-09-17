package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/User")
public class UserController {

    private final IUserInfoService userInfoService;

    // 로그인 페이지 표시 (GET 요청)
    @GetMapping("/signin")
    public String showSigninPage(HttpSession session, Model model) {
        String userId= (String) session.getAttribute("userId");

        if (userId != null){
            model.addAttribute("userId", userId);
        } else {
            model.addAttribute("userId", "");
        }

        return "User/signin";
    }

    // 로그인 처리 (POST 요청)
    @PostMapping("/signin")
    public String processSignin(@RequestParam String id, @RequestParam String pwd, Model model, HttpSession session) {
        // 로그인 처리 로직
        // 사용자 인증, 세션 설정 등
//        session.setAttribute("userId", id);

        log.info(id);
        log.info(pwd);

        String chk_id = "masche";
        String chk_pwd = "1234";

        if (id.equals(chk_id)) {
            if (pwd.equals(chk_pwd)) {
                return "redirect:/User/index";
            } else {
                session.setAttribute("error", "비밀번호가 일치하지 않습니다.");
            }
        } else {
            session.setAttribute("error", "존재하지 않는 아이디입니다.");
        }

        return "User/signin";
    }

    // 회원가입 페이지 표시 (GET 요청)
    // 중복확인

    @PostMapping("/check-duplicate")
    @ResponseBody
    public Map<String, String> checkDuplicate(@RequestParam("type") String type, @RequestParam("value") String value, HttpSession session, Model model) {
        log.info("중복확인 실행");
        log.info(type);
        log.info(value);
        Map<String, String> response = new HashMap<>();
        boolean isDuplicate = false;

        String EXISTING_NICKNAME = "abc";
        String EXISTING_ID = "abc";

        // 테스트용으로 특정 문자열과 비교
        if ("input_nickname".equals(type)) {
            // nickname 중복 확인
            isDuplicate = EXISTING_NICKNAME.equals(value);
            log.info(String.valueOf(isDuplicate));
        } else if ("input_id".equals(type)) {
            // id 중복 확인
            isDuplicate = EXISTING_ID.equals(value);
            log.info(String.valueOf(isDuplicate));
        }

        // 결과에 따라 메시지 설정
        if (isDuplicate) {
            if ("input_nickname".equals(type)) {
                response.put("message", "닉네임이 이미 존재합니다.");
            } else if ("input_id".equals(type)) {
                response.put("message", "아이디가 이미 존재합니다.");
            }
        } else {
            if ("input_nickname".equals(type)) {
                response.put("message", "사용 가능한 닉네임입니다.");
            } else if ("input_id".equals(type)) {
                response.put("message", "사용 가능한 아이디입니다.");
            }
        }

        session.setAttribute(type+"_duplicate", isDuplicate);

//        String test;
//        if (!(Boolean) session.getAttribute("input_id_duplicate")&&!(Boolean) session.getAttribute("input_nickname_duplicate")) {
//             test = "가입 가능";
//        } else{test="가입 불가능";}
//
//        log.info(test);

        log.info(response.toString());

        return response;
    }

    @GetMapping("/email_verification")
    public String showEmailVerificationPage() {
        return "User/email_verification";
    }

    @ResponseBody
    @PostMapping(value="/getEmailExists")
    public UserInfoDTO getEmailExists(@RequestBody Map<String, String> requestData, HttpServletRequest request, HttpSession session) throws Exception {
        log.info("이메일 전송");
        String email = CmmUtil.nvl(requestData.get("email"));
        log.info("email : {}", email);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserEmail(EncryptUtil.encAES128CBC(email));

        log.info("pDTO : {}", pDTO);

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserEmailExists(pDTO)).orElseGet(UserInfoDTO::new);
        log.info("이메일 전송 완료");

        session.setAttribute("emailResultDTO", rDTO);

        return rDTO;
    }

    // 이메일 인증 처리 (POST 요청)
    @PostMapping("/email_verification")
    public String processEmailVerification(@RequestParam String email, @RequestParam String source, HttpServletRequest request, HttpSession session, Model model) {
        // 이메일 인증 로직
        // 이메일 확인 후 처리 등
        log.info(email);
        session.setAttribute("email", email);
        log.info(source);

        switch (source) {
            case "signup":
                return "redirect:signup_detail";
            case "find_id":
                return "redirect:find_id";
            case "reset_pwd":
                return "redirect:reset_pwd";
            default:
                return "redirect:email_verification";
        }
    }

    @GetMapping("/signup_detail")
    public String showSignupPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String userEmail = (String) session.getAttribute("email");

        UserInfoDTO emailResultDTO = (UserInfoDTO) session.getAttribute("emailResultDTO");

        if (emailResultDTO.getExistsYn().equals("Y")) {
            String alert = "해당 이메일로 가입된 계정이 이미 존재합니다.";
            session.setAttribute("error", alert);
        }

        return "User/signup_detail";
    }
    // 회원가입 처리 (POST 요청)

    @PostMapping("/signup_detail")
    public String processSignup(@RequestParam String name, @RequestParam String gender, @RequestParam String nickname, @RequestParam String id, @RequestParam String pwd, HttpSession session, Model model) {
        // 회원가입 처리 로직
        // 데이터베이스에 사용자 정보 저장 등
        log.info(gender);
        session.setAttribute("userId", id);

        return "redirect:signin"; // 회원가입 성공 후 로그인 페이지로 리다이렉트
    }
    // 비밀번호 재설정 페이지 표시 (GET 요청)
    // 비밀번호 재설정 처리 (POST 요청)
    // 이메일 인증 페이지 표시 (GET 요청)

    @GetMapping("/reset_pwd")
    public String showResetPwdPage(HttpSession session, Model model) {
        return "User/reset_pwd";
    }

    @PostMapping("/reset_pwd")
    public String processResetPwd(@RequestParam String id, @RequestParam String pwd, @RequestParam String pwd2, HttpSession session, Model model) {
        log.info(id);
        session.setAttribute("userId", id);
        return "redirect:/User/signin"; // 비밀번호 재설정 후 로그인 페이지로 리다이렉트
    }

    // 아이디 찾기 결과 페이지 표시 (GET 요청)
    @GetMapping("/find_id")
    public String showFindIdPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");

        String chk_email = "2420110173@gspace.kopo.ac.kr";
        String userName = "User01";
        String userId = "masche";

        if (email.equals(chk_email)) {
            model.addAttribute("userName", userName);
            model.addAttribute("userId", userId);
            session.setAttribute("userEmail", email);
            session.setAttribute("userId", userId);
        } else {
            // 이메일이 일치하지 않는 경우 빈 문자열을 명시적으로 전달
            model.addAttribute("userName", "");
            model.addAttribute("userId", "");
        }
        return "User/find_id";
    }

    // 아이디 찾기 처리 (POST 요청)
    @PostMapping("/find_id")
    public String processFindId(@RequestParam String findIdSource,HttpSession session,Model model) {
        log.info("소스값:"+findIdSource);
        session.setAttribute("findIdSource", findIdSource);

        switch (findIdSource) {
            case "signin":
                return "redirect:signin";
            case "reset_pwd":
                return "redirect:reset_pwd";
            default:
                return "redirect:find_id";
        }
         // 찾은 아이디 결과 페이지로 이동
    }
        // 홈 페이지 (index.jsp로 설정)
    @GetMapping("/index")
    public String showHomePage() {
        return "User/index"; // /WEB-INF/views/index.jsp
    }
}
