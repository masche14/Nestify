<%--
  Created by IntelliJ IDEA.
  User: data21
  Date: 2024-09-11
  Time: 오후 4:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>비밀번호 재설정</title>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSansNeo.css' rel='stylesheet' type='text/css'>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSans-jp.css' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="/css/userStyles.css">
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // 세션 스토리지에서 저장된 아이디를 가져오기
            const savedId = sessionStorage.getItem('savedId');
            // 저장된 아이디가 존재할 경우, 입력 필드에 자동으로 채워 넣기
            if (savedId) {
                document.getElementById('input_id').value = savedId;
            }

            function saveIdAndGoToSignin() {
                const inputId = document.getElementById('input_id').value;
                if (inputId) {
                    sessionStorage.setItem('savedId', inputId); // 세션 스토리지에 아이디 저장
                }
                window.location.href = "signin"; // 로그인 페이지로 이동
            }

            // 버튼에 클릭 이벤트 리스너 추가
            document.getElementById('goToLogin').addEventListener('click', saveIdAndGoToSignin);
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
        <form method="post" action="reset_pwd" id="resetPwdForm">
            <div class="form_box">
                <label class="label_bold" for="input_id">아이디 / ID</label>
                <div class="input_box">
                    <input type="text" class="input_info" id="input_id" name="id" placeholder="아이디를 입력하세요.">
                </div>
            </div>
            <div class="form_box">
                <label class="label_bold" for="input_pwd">비밀번호 / PASSWORD</label>
                <div class="input_box">
                    <input type="password" class="input_info" id="input_pwd" name="pwd" placeholder="비밀번호를 입력하세요.">
                </div>
            </div>
            <div class="form_box">
                <label class="label_bold" for="chk_pwd">비밀번호 확인 / PASSWORD CHECK</label>
                <div class="input_box">
                    <input type="password" class="input_info" id="chk_pwd" name="pwd" placeholder="비밀번호를 확인하세요.">
                </div>
            </div>
            <div class="button_login_wrap">
                <button type="button" class="btn_reset_pwd" id="goToLogin">비밀번호 재설정</button>
            </div>
        </form>
    </div>
</div>

<footer></footer>
</body>
</html>
