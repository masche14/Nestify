<%--
  Created by IntelliJ IDEA.
  User: data21
  Date: 2024-09-11
  Time: 오후 4:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>이메일 인증</title>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSansNeo.css' rel='stylesheet' type='text/css'>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSans-jp.css' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="/css/userStyles.css">
    <script src="/js/getExists.js" defer></script>
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script> <!-- JS 경로 수정 -->
    <script>
        // DOM이 완전히 로드된 후에 실행되도록 설정
        document.addEventListener('DOMContentLoaded', function() {
            const SS_USER_ID = "<%= (String) session.getAttribute("SS_USER_ID") %>"

            if (SS_USER_ID && SS_USER_ID.trim() !== "null") {
                document.getElementById("loginNav").style.display = "none";
            } else {
                document.getElementById("myPageNav").style.display = "none";
                document.getElementById("logoutNav").style.display = "none";
            }

            const errorMsg = "<%= (String) session.getAttribute("errorMsg") %>";

            <% session.removeAttribute("errorMsg"); %>

            if (errorMsg && errorMsg.trim() !== "null"){
                alert(errorMsg)
            }

            document.getElementById('sourceField').value = sessionStorage.getItem('source');
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
        <a href="signin">로그인</a>
        <a href="#" id="myPageNav" >마이페이지</a>
        <a href="logout" id="logoutNav">로그아웃</a>
    </div>
</div>

<div class="content">
    <div class="container">
        <form method="post" action="email_verification" id="emailVerificationForm">
            <input type="hidden" id="sourceField" name="source">
            <div class="form_box">
                <label class="label_bold" for="input_email">이메일 / E-MAIL</label>
                <div class="input_box">
                    <input type="email" class="send_code input_info" id="input_email" name="email" placeholder="이메일을 입력하세요." required>
                    <button type="button" id="send_code" class="side_btn" onclick="getEmailExists('input_email')">코드전송</button>
                </div>
            </div>
            <div class="form_box">
                <label class="label_bold" for="email_confirm">인증코드 입력</label>
                <div class="input_box">
                    <input type="text" class="confirm_code input_info" id="email_confirm" name="email_confirm" placeholder="인증코드 입력." required>
                    <button type="button" id="check_code" class="side_btn" onclick="approveCode()">인증확인</button>
                </div>
            </div>
            <div class="button_login_wrap">
                <button type="submit" class="btn_next" id="nextButton">다음</button>
            </div>
        </form>
    </div>
</div>

<footer></footer>
</body>
</html>
