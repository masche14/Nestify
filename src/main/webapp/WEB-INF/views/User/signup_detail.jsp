<%--
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
    <title>회원가입</title>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSansNeo.css' rel='stylesheet' type='text/css'>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSans-jp.css' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="/css/userStyles.css">
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script> <!-- JS 경로 수정 -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // 폼 제출 이벤트 리스너 추가
            document.getElementById('signupForm').addEventListener('submit', function(event) {
                event.preventDefault(); // 기본 폼 제출 동작 막기

                // 아이디 값을 세션 스토리지에 저장
                const id = document.getElementById('input_id').value;
                sessionStorage.setItem('savedId', id);

                // 로그인 페이지로 이동
                window.location.href = "signin";
            });
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
    </div>
</div>

<div class="content">
    <div class="container">
        <form method="post" action="signup_detail" id="signupForm">
            <div class="form_box">
                <label class="label_bold" for="input_name">이름 / NAME</label>
                <div class="input_box">
                    <input type="text" class="input_info" id="input_name" name="name" placeholder="이름을 입력하세요." required>
                </div>
            </div>
            <div class="form_box">
                <div class="radio_box">
                    <span>성별</span>
                    <input type="radio" id="male" name="gender" required>
                    <label for="male">남성</label>
                    <input type="radio" id="female" name="gender">
                    <label for="female">여성</label>
                </div>
            </div>
            <div class="form_box">
                <label class="label_bold" for="input_nickname">닉네임 / NICKNAME</label>
                <div class="input_box">
                    <input type="text" class="send_code input_info" id="input_nickname" name="nickname" placeholder="닉네임을 입력하세요."    >
                    <button type="button" class="side_btn">중복확인</button>
                </div>
            </div>
            <div class="form_box">
                <label class="label_bold" for="input_id">아이디 / ID</label>
                <div class="input_box">
                    <input type="text" class="send_code input_info" id="input_id" name="id" placeholder="아이디를 입력하세요."    >
                    <button type="button" class="side_btn">중복확인</button>
                </div>
            </div>
            <div class="form_box">
                <label class="label_bold" for="input_pwd">비밀번호 / PASSWORD</label>
                <div class="input_box">
                    <input type="password" class="input_info" id="input_pwd" name="pwd" placeholder="비밀번호를 입력하세요." required>
                </div>
            </div>
            <div class="form_box">
                <label class="label_bold" for="chk_pwd">비밀번호 확인 / PASSWORD CHECK</label>
                <div class="input_box">
                    <input type="password" class="input_info" id="chk_pwd" name="chk_pwd" placeholder="비밀번호를 확인하세요." required>
                </div>
            </div>
            <div class="form_box">
                <input type="checkbox" class="auto_login_chk" id="confirm_terms" name="auto_login" required>
                <label for="confirm_terms">이용약관에 동의</label>
            </div>
            <div class="button_login_wrap">
                <button type="submit" class="btn_signup" id="goToLogin">회원가입</button>
            </div>
        </form>
    </div>
</div>

<footer></footer>
</body>
</html>

