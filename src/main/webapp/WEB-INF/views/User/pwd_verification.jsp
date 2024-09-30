<%--
  Created by IntelliJ IDEA.
  User: zskfn
  Date: 2024-09-20
  Time: 오후 7:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>비밀번호 인증</title>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSansNeo.css' rel='stylesheet' type='text/css'>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSans-jp.css' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="/css/userStyles.css">
    <script src="/js/getExists.js" defer></script>
    <script src="/js/setReferrer.js" defer></script>
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

            const error = "<%= (String) session.getAttribute("msg") %>";

            console.log(error);

            if (error && error.trim() !== "null") {
                alert(error);
                <% session.removeAttribute("msg"); %>
            }

            // document.getElementById('sourceField').value = sessionStorage.getItem('source');
        });
    </script>
</head>
<body>
<header></header>
<div class="black">

    <!-- 네비게이션 바 -->
    <div class="navbar">
        <!-- 좌측 로고 -->
        <img src="/nestifyLogo.png" alt="Logo" class="logo">

        <!-- 우측 메뉴 -->
        <div class="menu">
            <a href="/User/index">홈</a>
            <a href="#">인테리어</a>
            <a href="javascript:void(0);" id="loginNav" onclick="setReferrer()">로그인</a>
            <a href="/User/pwd_verification" id="myPageNav" >마이페이지</a>
            <a href="/User/logout" id="logoutNav">로그아웃</a>
        </div>
    </div>

    <div class="content">
        <div class="container">
            <form method="post" action="/User/pwd_verification" id="pwdVerifyForm">
                <div class="form_box">
                    <label class="label_bold" for="input_pwd">비밀번호 / PASSWORD</label>
                    <div class="input_box">
                        <input type="password" class="send_code input_info" id="input_pwd" name="pwd" placeholder="비밀번호를 입력하세요." required>
                        <button type="submit" id="send_code" class="side_btn">인증하기</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<footer></footer>
</body>
</html>