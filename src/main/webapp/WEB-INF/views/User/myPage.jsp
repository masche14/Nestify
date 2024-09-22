<%@ page import="kopo.poly.dto.UserInfoDTO" %>
<%@ page import="kopo.poly.util.EncryptUtil" %><%--
  Created by IntelliJ IDEA.
  User: data21
  Date: 2024-09-11
  Time: 오후 4:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>마이페이지</title>
  <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSansNeo.css' rel='stylesheet' type='text/css'>
  <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSans-jp.css' rel='stylesheet' type='text/css'>
  <link rel="stylesheet" href="/css/userStyles.css">
  <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script> <!-- JS 경로 수정 -->
  <script src="/js/getExists.js" defer></script>
  <script src="/js/checkDuplicate.js" defer></script>
<%--  <%--%>
<%--    String SS_USER_NAME = (String) session.getAttribute("SS_USER_NAME");--%>
<%--    String SS_USER_NICKNAME = (String) session.getAttribute("SS_USER_NICKNAME");--%>
<%--    String SS_USER_EMAIL = (String) session.getAttribute("SS_USER_EMAIL");--%>
<%--  %>--%>
  <script>
    document.addEventListener('DOMContentLoaded', function() {
      const SS_USER_ID = "<%= (String) session.getAttribute("SS_USER_ID") %>"
      const SS_USER_NAME = "<%= (String) session.getAttribute("SS_USER_NAME") %>"
      const SS_USER_NICKNAME = "<%= (String) session.getAttribute("SS_USER_NICKNAME") %>"
      const SS_USER_EMAIL = "<%= EncryptUtil.decAES128CBC((String) session.getAttribute("SS_USER_EMAIL")) %>"
      const errorMsg = "<%= (String) session.getAttribute("errorMsg") %>";

      <% session.removeAttribute("errorMsg"); %>

      if (SS_USER_ID && SS_USER_ID.trim() !== "null") {
        document.getElementById("loginNav").style.display = "none";
      } else {
        document.getElementById("myPageNav").style.display = "none";
        document.getElementById("logoutNav").style.display = "none";
      }

      if (errorMsg && errorMsg.trim() !== "null"){
        alert(errorMsg)
      }

      document.getElementById("nameDisplay").textContent = SS_USER_NAME;
      document.getElementById("nicknameDisplay").textContent = SS_USER_NICKNAME;
      document.getElementById("emailDisplay").textContent = SS_USER_EMAIL;

      // 폼 제출 전 확인하는 함수
      function validateForm(event) {
        if (document.getElementById("input_nickname").value){
          if (!nicknameChecked) {
            event.preventDefault();  // 폼 제출을 막음
            alert("닉네임 중복 여부를 확인해주세요.");
            return false;
          }
        }
        // 중복 확인이 완료되었으면 폼을 제출
        const pwd = document.getElementById("input_pwd").value;
        const pwd2 = document.getElementById("chk_pwd").value;

        if (pwd !== pwd2){
          event.preventDefault();
          alert("비밀번호와 비밀번호 확인에 입력된 값이\n일치하지 않습니다.");
          return false;
        }

        const emailInput = document.getElementById("input_email").value.trim();

        // 이메일이 존재하고, approveResult가 'y'가 아닐 경우 경고창 표시
        if (emailInput && approveResult !== 'y') {
          event.preventDefault();
          alert("이메일 인증을 완료해야 합니다.");
          return false; // 이메일 인증이 완료되지 않았으므로 폼 제출을 막음
        }

        // 모든 조건이 만족되었을 경우 폼 제출 허용
        return true;
      }

      // 회원가입 버튼 클릭 시 validateForm 실행
      document.getElementById('goToMain').addEventListener('click', validateForm);
    });
  </script>

</head>
<body>
<header></header>

<!-- 네비게이션 바 -->
<div class="navbar">
  <!-- 좌측 로고 -->
  <img src="/nestifyLogo.png" alt="Logo" class="logo">

  <!-- 우측 메뉴 -->
  <div class="menu">
    <a href="index">홈</a>
    <a href="#">인테리어</a>
    <a href="signin" id="loginNav">로그인</a>
    <a href="pwd_verification" id="myPageNav" >마이페이지</a>
    <a href="logout" id="logoutNav">로그아웃</a>
  </div>
</div>

<div class="content">
  <div class="container myPage">
    <div class="info_wrap">
      <div class="img_wrap">
        <div class="img_box"></div>
      </div>
      <div class="info_box_container">
        <div class="info_box">
          <div><span>이름</span></div>
          <div>
            <span id="nameDisplay"></span>
          </div>
        </div>
        <div class="info_box">
          <div><span>닉네임</span></div>
          <div>
            <span id="nicknameDisplay"></span>
          </div>
        </div>
        <div class="info_box">
          <div><span>이메일</span></div>
          <div>
            <span id="emailDisplay"></span>
          </div>
        </div>
      </div>
    </div>
    <div class="form_wrap">
      <form method="post" action="myPage" id="signupForm">
        <%--      <div class="form_box">--%>
        <%--        <div class="radio_box">--%>
        <%--          <span class="margin_left_4px">성별</span>--%>
        <%--          <input type="radio" id="male" name="gender" value="남성" required>--%>
        <%--          <label class="margin_lef_zero" for="male">남성</label>--%>
        <%--          <input type="radio" id="female" name="gender" value="여성">--%>
        <%--          <label class="margin_lef_zero" for="female">여성</label>--%>
        <%--        </div>--%>
        <%--      </div>--%>
        <div class="form_box">
          <label class="label_bold" for="input_nickname">닉네임 변경 / CHG_NICKNAME</label>
          <div class="input_box">
            <input type="text" class="send_code input_info" id="input_nickname" name="nickname" placeholder="닉네임을 입력하세요.">
            <button type="button" class="side_btn" id="nick_check" onclick="checkDuplicate('input_nickname')">중복확인</button>
          </div>
          <p id="input_nicknameMessage"></p> <!-- 닉네임 중복 확인 결과 표시 -->
        </div>
        <div class="form_box">
          <label class="label_bold" for="input_pwd">비밀번호 변경 / CHG_PASSWORD</label>
          <div class="input_box">
            <input type="password" class="input_info" id="input_pwd" name="pwd" placeholder="비밀번호를 입력하세요.">
          </div>
        </div>
        <div class="form_box">
          <label class="label_bold" for="chk_pwd">비밀번호 확인 / PASSWORD CHECK</label>
          <div class="input_box">
            <input type="password" class="input_info" id="chk_pwd" name="chk_pwd" placeholder="비밀번호를 확인하세요.">
          </div>
        </div>
        <div class="form_box">
          <label class="label_bold" for="input_email">이메일 / E-MAIL</label>
          <div class="input_box">
            <input type="email" class="send_code input_info" id="input_email" name="email" placeholder="이메일을 입력하세요.">
            <button type="button" id="send_code" class="side_btn" onclick="getEmailExists('input_email')">코드전송</button>
          </div>
        </div>
        <div class="form_box">
          <label class="label_bold" for="email_confirm">인증코드 입력</label>
          <div class="input_box">
            <input type="text" class="confirm_code input_info" id="email_confirm" name="email_confirm" placeholder="인증코드 입력.">
            <button type="button" id="check_code" class="side_btn" onclick="approveCode()">인증확인</button>
          </div>
        </div>
        <div class="button_login_wrap">
          <button type="submit" class="btn_signup" id="goToMain">회원정보저장</button>
        </div>
      </form>
    </div>
  </div>
</div>

<footer></footer>
</body>
</html>

