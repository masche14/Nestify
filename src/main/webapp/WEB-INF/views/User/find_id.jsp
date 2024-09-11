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
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script> <!-- JS 경로 수정 -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // 세션 스토리지에서 이메일 정보를 가져오기
            const email = sessionStorage.getItem('userEmail');

            let userName, userId;

            if (email === '2420110173@gspace.kopo.ac.kr') {
                userName = 'USER01';
                userId = 'masche';
            } else {
                alert("해당 이메일로 등록된 회원정보가 없습니다.");
                window.location.href = "signup_detail";
                return; // 이메일이 없으면 함수 종료
            }

            // 세션 스토리지에서 이메일 정보를 삭제
            sessionStorage.removeItem('userEmail');

            // 세션 스토리지에 아이디 정보 저장
            sessionStorage.setItem('savedId', userId);

            // DOM 요소에 사용자 이름과 ID를 표시
            if (userName) {
                document.getElementById('nameDisplay').textContent = userName;
            }

            if (userId) {
                document.getElementById('idDisplay').textContent = userId;
            }

            // 로그인 페이지로 이동하는 함수
            function goToSignin() {
                window.location.href = "signin";
            }

            // 비밀번호 찾기 페이지로 이동하는 함수
            function goToResetPwd() {
                window.location.href = "reset_pwd";
            }

            // 버튼에 이벤트 리스너 추가
            document.getElementById('goToLogin').addEventListener('click', goToSignin);
            document.getElementById('goToResetPwd').addEventListener('click', goToResetPwd);
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
        <form id="findIdForm">
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
                        <button type="button" class="two_button" id="goToLogin">로그인 페이지</button>
                        <button type="button" class="two_button" id="goToResetPwd">비밀번호 재설정</button>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<footer></footer>
</body>
</html>
