package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
        log.info("{}.loginPoc Start", this.getClass().getName());

        int res = 0;
        String msg = "";
        MsgDTO dto;
        UserInfoDTO pDTO;
        try {
            pDTO = new UserInfoDTO();
            pDTO.setUserId(id);
            pDTO.setPassword(EncryptUtil.encHashSHA256(pwd));

            UserInfoDTO rDTO = userInfoService.getLogin(pDTO);

            if (!CmmUtil.nvl(rDTO.getUserId()).isEmpty()) {
                res = 1;

                msg = "로그인에 성공하였습니다.";
                session.setAttribute("SS_USER_ID", id);
                session.setAttribute("SS_USER_NAME", rDTO.getUserName());
            } else {
                msg = "아이디와 비밀번호가 올바르지 않습니다.";
            }
        } catch (Exception e) {
            msg = "시스템 문제로 로그인이 실패하였습니다.";
            res = 2;
            log.info(e.toString());
        } finally {
            log.info("{}.loginProc End", this.getClass().getName());
        }

        session.setAttribute("msg", msg);

        if (res == 1) {
            return "redirect:/User/index";
        } else {
            return "redirect:/User/signin";
        }
    }

    // 회원가입 페이지 표시 (GET 요청)
    // 중복확인

    @PostMapping("/check-duplicate")
    @ResponseBody
    public Map<String, String> checkDuplicate(@RequestParam("type") String type, @RequestParam("value") String value, HttpSession session, Model model) throws Exception {
        log.info("중복확인 실행");
        log.info(type);
        log.info(value);
        Map<String, String> response = new HashMap<>();
        String isDuplicate = "";

        // 테스트용으로 특정 문자열과 비교
        if ("input_nickname".equals(type)) {
            // nickname 중복 확인
            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUserNickname(value);
            UserInfoDTO rDTO = userInfoService.getUserNicknameExists(pDTO);
            log.info("닉네임 중복여부 : {}",rDTO.getExistsYn());
            isDuplicate = rDTO.getExistsYn();
        } else if ("input_id".equals(type)) {
            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUserId(value);
            UserInfoDTO rDTO = userInfoService.getUserIdExists(pDTO);
            // id 중복 확인
            log.info("아이디 중복여부 : {}",rDTO.getExistsYn());
            isDuplicate = rDTO.getExistsYn();;
        }

        // 결과에 따라 메시지 설정
        if (isDuplicate.equals("Y")) {
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

        if (type.equals("input_id")){
            session.setAttribute("idResult", isDuplicate);
        } else if (type.equals("input_nickname")){
            session.setAttribute("nickResult", isDuplicate);
        }

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
        UserInfoDTO testDTO = (UserInfoDTO) session.getAttribute("emailResultDTO");
        log.info("test : {}",testDTO.getExistsYn());
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

        UserInfoDTO emailResultDTO = (UserInfoDTO) session.getAttribute("emailResultDTO");

        log.info("test : {}",emailResultDTO.getExistsYn());

        if (emailResultDTO.getExistsYn().equals("Y")) {
            String alert = "해당 이메일로 가입된 계정이 이미 존재합니다.";
            session.setAttribute("error", alert);
        }

        return "User/signup_detail";
    }
    // 회원가입 처리 (POST 요청)

    @PostMapping("/signup_detail")
    public String processSignup(HttpServletRequest request, HttpSession session, Model model) {

        UserInfoDTO pDTO;
        int res = 0;
        String msg = "";
        MsgDTO dto;
        // 회원가입 처리 로직
        // 데이터베이스에 사용자 정보 저장 등
        try {
            String userEmail = CmmUtil.nvl((String) session.getAttribute("email"));
            String userName = CmmUtil.nvl(request.getParameter("name"));
            String gender = CmmUtil.nvl(request.getParameter("gender"));
            String userNickname = CmmUtil.nvl(request.getParameter("nickname"));
            String userId = CmmUtil.nvl(request.getParameter("id"));
            String password = CmmUtil.nvl(EncryptUtil.encHashSHA256(request.getParameter("pwd")));

            log.info("userName : {}", userName);
            log.info("gender : {}", gender);
            log.info("userNickname : {}", userNickname);
            log.info("userId : {}", userId);
            log.info("password : {}", password);
            log.info("userEmail : {}", userEmail);

            pDTO = new UserInfoDTO();

            pDTO.setUserEmail(EncryptUtil.encAES128CBC(userEmail));
            pDTO.setUserName(userName);
            pDTO.setGender(gender);
            pDTO.setUserNickname(userNickname);
            pDTO.setUserId(userId);
            pDTO.setPassword(password);

            res = userInfoService.insertUserInfo(pDTO);

            log.info("회원가입 결과(res) : "+res);

            if (res==1){
                msg="회원가입되었습니다.";
                session.setAttribute("userId", userId);
            } else if (res==2){
                msg="이미 가입된 아이디 입니다.";
            } else {
                msg="오류로 인해 회원가입이 실패하였습니다.";
            }

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            log.info(e.toString());
        } finally {
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);
        }
        session.setAttribute("signinResultDTO", dto);

        if (msg.equals("회원가입되었습니다."))
            return "redirect:signin";// 회원가입 성공 후 로그인 페이지로 리다이렉트
        else return "redirect:signup_detail";
    }
    // 비밀번호 재설정 페이지 표시 (GET 요청)
    // 비밀번호 재설정 처리 (POST 요청)
    // 이메일 인증 페이지 표시 (GET 요청)

    @GetMapping("/reset_pwd")
    public String showResetPwdPage(HttpSession session, Model model) {
        return "User/reset_pwd";
    }

    @PostMapping("/reset_pwd")
    public String processResetPwd(HttpServletRequest request, HttpSession session, Model model) {
        String id = request.getParameter("id");
        log.info(id);

        UserInfoDTO emailResultDTO = (UserInfoDTO) session.getAttribute("emailResultDTO");

        String checkId = emailResultDTO.getUserId();
        log.info(checkId);

        UserInfoDTO pDTO;

        int res = 0;
        String msg = "";
        MsgDTO dto;
        // 회원가입 처리 로직
        // 데이터베이스에 사용자 정보 저장 등

        if (checkId.equals(id)) {
            log.info("일치");

            try {

//                String userName = CmmUtil.nvl(request.getParameter("name"));
//                String gender = CmmUtil.nvl(request.getParameter("gender"));
//                String userNickname = CmmUtil.nvl(request.getParameter("nickname"));
                String userId = CmmUtil.nvl(request.getParameter("id"));
                String password = CmmUtil.nvl(EncryptUtil.encHashSHA256(request.getParameter("pwd")));

//                log.info("userName : {}", userName);
//                log.info("gender : {}", gender);
//                log.info("userNickname : {}", userNickname);
                log.info("userId : {}", userId);
                log.info("password : {}", password);
//                log.info("userEmail : {}", userEmail);

                pDTO = new UserInfoDTO();

//                pDTO.setUserEmail(EncryptUtil.encAES128CBC(userEmail));
//                pDTO.setUserName(userName);
//                pDTO.setGender(gender);
//                pDTO.setUserNickname(userNickname);
                pDTO.setUserId(userId);
                pDTO.setPassword(password);

                res = userInfoService.updateUserInfo(pDTO);

                log.info("회원가입 결과(res) : "+res);

                if (res==1){
                    msg="업데이트를 성공했습니다.";
                    session.setAttribute("userId", userId);
                } else {
                    msg="오류로 인해 업데이트를 실패하였습니다.";
                }
            } catch (Exception e) {
                msg = "실패하였습니다. : " + e;
                log.info(e.toString());
            } finally {
                dto = new MsgDTO();
                dto.setResult(res);
                dto.setMsg(msg);
            }
            session.setAttribute("signinResultDTO", dto);
            return "redirect:/User/signin";

        } else
            return "redirect:/User/reset_pwd";
    }

    // 아이디 찾기 결과 페이지 표시 (GET 요청)
    @GetMapping("/find_id")
    public String showFindIdPage(HttpSession session, Model model) {
        UserInfoDTO emailResultDTO = (UserInfoDTO) session.getAttribute("emailResultDTO");

        if (emailResultDTO.getExistsYn().equals("Y")) {
            model.addAttribute("userName", emailResultDTO.getUserName());
            model.addAttribute("userId", emailResultDTO.getUserId());
            session.setAttribute("userEmail", emailResultDTO.getUserEmail());
            session.setAttribute("userId", emailResultDTO.getUserId());
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
