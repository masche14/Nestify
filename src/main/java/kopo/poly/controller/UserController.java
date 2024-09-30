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

    @PostMapping("/setReferrer")
    public String setReferrer(HttpSession session, HttpServletRequest request, Model model) {
        log.info("referrer : {}", request.getHeader("referer"));
        session.setAttribute("referrer", request.getHeader("referer"));
        return "redirect:/User/signin";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/User/index";
    }

    // 로그인 페이지 표시 (GET 요청)
    @GetMapping("/signin")
    public String showSigninPage(HttpSession session, Model model) {
        String SS_USER_ID = (String) session.getAttribute("SS_USER_ID");

        if (SS_USER_ID != null){
            return "redirect:index";
        }

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
    public String processSignin(@RequestParam String id, @RequestParam String pwd, Model model, HttpSession session, HttpServletRequest request) {
        // 로그인 처리 로직
        // 사용자 인증, 세션 설정 등
        log.info("{}.loginPoc Start", this.getClass().getName());
        String referrer = (String) session.getAttribute("referrer");
        String ref = referrer.replace("http://localhost:11000", "");

        log.info("ref : {}", ref);

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

                session.removeAttribute("emailResultDTO");

                msg = "로그인에 성공하였습니다.";
                session.setAttribute("SS_USER_ID", id);
                session.setAttribute("SS_USER_NAME", rDTO.getUserName());
                session.setAttribute("SS_USER_NICKNAME", rDTO.getUserNickname());
                session.setAttribute("SS_USER_EMAIL", rDTO.getUserEmail());
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

        if (res == 1) {
            switch (ref){
                case "/Interior/makeNew":
                    return "redirect:/Interior/makeNew";
                default:
                    return "redirect:/User/index";
            }
        } else {
            session.setAttribute("msg", msg);
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
                response.put("checkNick", value);
                log.info("체크 닉 : {}", (String) session.getAttribute("check_nick"));
                response.put("message", "사용 가능한 닉네임입니다.");
            } else if ("input_id".equals(type)) {
                response.put("checkId", value);
                log.info("체크 아이디 : {}", (String) session.getAttribute("check_id"));
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
    public String showEmailVerificationPage(HttpSession session) {
        String SS_USER_ID = (String) session.getAttribute("SS_USER_ID");

        if (SS_USER_ID != null){
            return "redirect:index";
        }
        return "User/email_verification";
    }

    @ResponseBody
    @PostMapping(value="/getEmailExists")
    public UserInfoDTO getEmailExists(@RequestBody Map<String, String> requestData, HttpServletRequest request, HttpSession session) throws Exception {
        log.info("이메일 전송");
        String email = CmmUtil.nvl(requestData.get("email"));
        log.info("email : {}", email);
        session.setAttribute("checkUserEmail", email);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserEmail(EncryptUtil.encAES128CBC(email));

        log.info("pDTO : {}", pDTO);

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserEmailExists(pDTO)).orElseGet(UserInfoDTO::new);
        log.info("이메일 전송 완료");


        session.setAttribute("emailResultDTO", rDTO);
        UserInfoDTO testDTO = (UserInfoDTO) session.getAttribute("emailResultDTO");
        log.info("test : {}", testDTO.getExistsYn());
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

        String checkUserEmail = (String) session.getAttribute("checkUserEmail");
        String errorMsg;

        if (!email.equals(checkUserEmail)) {
            errorMsg = "이메일을 새로 입력하였습니다. 이메일 인증을 다시 진행해주세요.";
            session.setAttribute("errorMsg", errorMsg);
            return "redirect:/User/email_verification";
        }

        session.removeAttribute("checkUserEmail");

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
        String SS_USER_ID = (String) session.getAttribute("SS_USER_ID");

        if (SS_USER_ID != null){
            return "redirect:index";
        }

        UserInfoDTO emailResultDTO = (UserInfoDTO) session.getAttribute("emailResultDTO");
        if (emailResultDTO==null){
            return "redirect:index";
        }

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

        if (res==1){
            session.removeAttribute("check_id");
            session.removeAttribute("check_nick");
            return "redirect:signin";// 회원가입 성공 후 로그인 페이지로 리다이렉트
        }
        else {
            return "redirect:signup_detail";
        }
    }
    // 비밀번호 재설정 페이지 표시 (GET 요청)
    // 비밀번호 재설정 처리 (POST 요청)
    // 이메일 인증 페이지 표시 (GET 요청)

    @GetMapping("/reset_pwd")
    public String showResetPwdPage(HttpSession session, Model model) {
        String SS_USER_ID = (String) session.getAttribute("SS_USER_ID");

        if (SS_USER_ID != null){
            return "redirect:index";
        }

        UserInfoDTO emailResultDTO = (UserInfoDTO) session.getAttribute("emailResultDTO");
        if (emailResultDTO==null){
            return "redirect:index";
        }

        return "User/reset_pwd";
    }

    @PostMapping("/reset_pwd")
    public String processResetPwd(HttpServletRequest request, HttpSession session, Model model) {
        String id = request.getParameter("id");
        log.info("입력한 아이디 : {}",id);

        UserInfoDTO emailResultDTO = (UserInfoDTO) session.getAttribute("emailResultDTO");

        String checkId = emailResultDTO.getUserId();
        log.info("해당 이메일로 가입된 아이디 : {}", checkId);

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

                log.info("업데이트 결과(res) : "+res);

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

        } else{
            log.info("불일치");
            msg = "해당 이메일로 가입된 아이디가 아닙니다. 아이디를 다시 확인하세요.";
            session.setAttribute("deferentId", msg);
            return "redirect:/User/reset_pwd";
        }
    }

    // 아이디 찾기 결과 페이지 표시 (GET 요청)
    @GetMapping("/find_id")
    public String showFindIdPage(HttpSession session, Model model) {
        String SS_USER_ID = (String) session.getAttribute("SS_USER_ID");

        if (SS_USER_ID != null){
            return "redirect:index";
        }

        UserInfoDTO emailResultDTO = (UserInfoDTO) session.getAttribute("emailResultDTO");
        if (emailResultDTO==null){
            return "redirect:index";
        }

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

    @GetMapping("/delOrUpdate")
    public String showDelOrUpdate(HttpSession session) {
        return "/User/delOrUpdate";
    }

    @PostMapping("/delOrUpdate")
    public String procDelOrUpdate(HttpSession session, HttpServletRequest request) {
        log.info("{}.procDelOrUpdate Start", this.getClass().getName());
        String selection = request.getParameter("selection");
        log.info(selection);
        session.setAttribute("selection", selection);
        return "redirect:/User/pwd_verification";
    }

    @GetMapping("/pwd_verification")
    public String showPwdVerificationPage(HttpSession session){
        String SS_USER_ID = (String) session.getAttribute("SS_USER_ID");

        if (SS_USER_ID != null){
            return "User/pwd_verification";
        } else {
            return "redirect:index";
        }
    }
    
    @PostMapping("/pwd_verification")
    public String processPwdVerification(HttpServletRequest request, HttpSession session, Model model) {
        log.info("{}.pwdVerification Start",this.getClass().getName());
        int res = 0;
        String msg = "";
        MsgDTO dto;
        UserInfoDTO pDTO;
        
        String SS_USER_ID = (String) session.getAttribute("SS_USER_ID");
        String selection = (String) session.getAttribute("selection");
        
        String pwd = request.getParameter("pwd");
        try {
            pDTO = new UserInfoDTO();
            pDTO.setUserId(SS_USER_ID);
            pDTO.setPassword(EncryptUtil.encHashSHA256(pwd));

            UserInfoDTO rDTO = userInfoService.getLogin(pDTO);

            if (!CmmUtil.nvl(rDTO.getUserId()).isEmpty()) {
                res = 1;
                msg = "비밀번호 인증에 성공하였습니다.";
            } else {
                msg = "비밀번호가 올바르지 않습니다.";
            }
        } catch (Exception e) {
            msg = "시스템 문제로 인증이 실패하였습니다.";
            res = 2;
            log.info(e.toString());
        } finally {
            log.info("{}.pwdVerification End", this.getClass().getName());
        }

        log.info(msg);

        if (res == 1) {
            session.setAttribute("pwdVerifyResult", "y");
            switch (selection){
                case "update":
                    return "redirect:/User/myPage";
                case "delete":
                    return "redirect:/User/delInfo";
                default:
                    return "redirect:/User/pwd_verification";
            }
        } else {
            session.setAttribute("msg", msg);
            return "redirect:/User/pwd_verification";
        }
    }

    @GetMapping("/delInfo")
    public String showDelInfo(HttpSession session){
        String pwdVerifyResult = (String) session.getAttribute("pwdVerifyResult");

        if (pwdVerifyResult==null) {
            return "redirect:/User/index";
        }

        return "/User/delInfo";
    }

    @PostMapping("/delInfo")
    public String procDelInfo(HttpSession session, HttpServletRequest request){
        String userId = (String) session.getAttribute("SS_USER_ID");
        log.info(userId);

        String delOrNot = request.getParameter("delOrNot");
        log.info(delOrNot);

        int res = 0;
        UserInfoDTO pDTO;


        switch (delOrNot) {
            case "cancel":
                log.info("삭제 취소");
                return "redirect:/User/index";
            case "confirm":
                log.info("삭제 진행");

                try {
                    pDTO = new UserInfoDTO();

                    pDTO.setUserId(userId);

                    res = userInfoService.deleteUserInfo(pDTO);

                    if (res == 1) {
                        log.info("삭제 완료했습니다.");
                        return "redirect:/User/logout";
                    }
                } catch (Exception e) {
                    log.info("삭제에 실패했습니다.");
                }
                return "redirect:/User/delInfo";
            default:
                log.info("예외");
                return "redirect:/User/delInfo";
        }
    }

    @GetMapping("/myPage")
    public String showMyPage(HttpSession session, Model model) {
        String pwdVerifyResult = (String) session.getAttribute("pwdVerifyResult");

        if (pwdVerifyResult==null) {
            return "redirect:/User/index";
        }

        return "User/myPage";
    }

    @PostMapping("/myPage")
    public String processMyPage(HttpServletRequest request, HttpSession session, Model model) {
        UserInfoDTO emailResultDTO = (UserInfoDTO) session.getAttribute("emailResultDTO");
        String checkUserEmail= (String) session.getAttribute("checkUserEmail");

        String errorMsg;

        if (emailResultDTO!=null){

            log.info("checkEmail : "+checkUserEmail);

            if(emailResultDTO.getExistsYn().equals("Y")){
                errorMsg = "이미 존재하는 이메일입니다.";
                log.info(errorMsg);
                session.setAttribute("errorMsg", errorMsg);
                session.removeAttribute("emailResultDTO");
                log.info("test1");
                return "redirect:/User/myPage";
            } else if (checkUserEmail!=null){
                if (!request.getParameter("email").equals(checkUserEmail)) {
                    errorMsg = "이메일을 새로 입력하였습니다. 이메일 인증을 다시 진행해주세요.";
                    log.info(errorMsg);
                    session.removeAttribute("checkUserEmail");
                    session.setAttribute("errorMsg", errorMsg);
                    log.info("test2");
                    return "redirect:/User/myPage";
                }
            }
        }
        session.removeAttribute("pwdVerifyResult");

        UserInfoDTO pDTO;

        int res = 0;
        String msg = "";
        MsgDTO dto;
        // 회원가입 처리 로직
        // 데이터베이스에 사용자 정보 저장 등

        log.info("비밀번호 : {}",request.getParameter("pwd"));
        log.info("이메일 : {}", request.getParameter("email"));

        try {
            String userNickname="";
            String password="";
            String userEmail="";

            if (!request.getParameter("nickname").equals("")){
                userNickname = CmmUtil.nvl(request.getParameter("nickname"));
                session.setAttribute("SS_USER_NICKNAME", userNickname);
            }

            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

            if (!request.getParameter("pwd").equals("")){
                password = CmmUtil.nvl(EncryptUtil.encHashSHA256(request.getParameter("pwd")));
            }

            if (!request.getParameter("email").equals("")){
                userEmail = CmmUtil.nvl(EncryptUtil.encAES128CBC(request.getParameter("email")));
                session.setAttribute("SS_USER_EMAIL", userEmail);
            }

            log.info("userNickname : {}", userNickname);
            log.info("userId : {}", userId);
            log.info("password : {}", password);
            log.info("userEmail : {}", userEmail);

            pDTO = new UserInfoDTO();

            pDTO.setUserEmail(userEmail);
            pDTO.setUserNickname(userNickname);
            pDTO.setUserId(userId);
            pDTO.setPassword(password);

            res = userInfoService.updateUserInfo(pDTO);

            log.info("업데이트 결과(res) : "+res);

            if (res==1){
                msg="업데이트를 성공했습니다.";
                session.setAttribute("userId", userId);
                session.removeAttribute("checkUserEmail");

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

        return "/User/index";
    }

}
