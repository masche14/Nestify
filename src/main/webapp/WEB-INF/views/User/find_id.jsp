<%--
  Created by IntelliJ IDEA.
  User: data21
  Date: 2024-09-11
  Time: 오후 4:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>아이디 찾기 결과</title>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSansNeo.css' rel='stylesheet' type='text/css'>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSans-jp.css' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="/css/userStyles.css">
    <script src="/js/setSource.js" defer></script>
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script> <!-- JS 경로 수정 -->
    <script>
        const userName = "<%= (String)request.getAttribute("userName") %>";
        const userId = "<%= (String)request.getAttribute("userId") %>";

        // DOMContentLoaded 이벤트 리스너를 사용하여 DOM이 완전히 로드된 후에 실행되도록 설정
        document.addEventListener('DOMContentLoaded', function() {

            if (userName && userName.trim() !== "") {
                if (userId && userId.trim() !== "") {
                    document.getElementById('nameDisplay').textContent = userName;
                    document.getElementById('idDisplay').textContent = userId;
                }
            } else {
                alert("해당 이메일로 가입된 계정이 존재하지 않습니다.");
                window.location.href="signup_detail"
            }

            const SS_USER_ID = "<%= (String) session.getAttribute("SS_USER_ID") %>"

            if (SS_USER_ID && SS_USER_ID.trim() !== "null") {
                document.getElementById("loginNav").style.display = "none";
            } else {
                document.getElementById("myPageNav").style.display = "none";
                document.getElementById("logoutNav").style.display = "none";
            }
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
        <form method="post" action="find_id" id="findIdForm">
            <div class="form_box">
                <div class="noti">
                    <div class="show_info" id="user_info">
                        <span id="nameDisplay"></span>
                        <span>님의 아이디는</span>
                    </div>
                    <div class="show_info" id="id_info">
                        <span id="idDisplay"></span>
                        <span>입니다.</span>
                    </div>
                    <div class="extra_top_margin input_box">
                        <input type="hidden" name="findIdSource" id="findIdSource">
                        <button type="button" class="two_button" id="goToLogin" onclick="setSource('signin')">로그인 페이지</button>
                        <button type="button" class="two_button" id="goToResetPwd" onclick="setSource('reset_pwd')">비밀번호 재설정</button>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<footer></footer>
</body>
</html>
