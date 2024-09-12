<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
    <link rel="stylesheet" href="/css/userStyles.css"/> <!-- CSS 경로 수정 -->
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script> <!-- JS 경로 수정 -->
    <script src="/js/setReferrer.js" defer></script>
    <%
        String error = (String) session.getAttribute("error");
        session.removeAttribute("error");
        // 세션에서 userId 값을 가져옴
        String userId = (String) session.getAttribute("userId");
        // 가져온 후 세션에서 해당 값을 제거
        session.removeAttribute("userId");
    %>
    <script>
        var userId = "<%= userId %>";
        var error = "<%= error %>"

        // DOMContentLoaded 이벤트 리스너를 사용하여 DOM이 완전히 로드된 후에 실행되도록 설정
        document.addEventListener('DOMContentLoaded', function() {
            if (error!=="null"){
                alert(error);
            }
            // 첫 번째 스크립트: savedId가 있으면 input_id 요소에 값을 설정
            if (userId!=="null"){
                document.getElementById('input_id').value = userId;
            } else {
                document.getElementById('input_id').value = "";
            }
        });

        // source를 설정하고 폼을 생성하여 제출하는 함수
        function setSourceAndSubmit(source) {
            sessionStorage.setItem('source', source);
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = 'email_verification';
            document.body.appendChild(form);
            form.submit();
        }
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
        <a href="javascript:void(0);" onclick="setReferrer()">로그인</a>
    </div>
</div>
<div class="content">
    <div class="container">
        <form method="post" action="signin" id="signinForm">
            <div class="form_box">
                <label class="label_bold" for="input_id">아이디 / ID</label>
                <div class="input_box">
                    <input type="text" class="input_info" id="input_id" name="id" placeholder="아이디를 입력하세요." required>
                </div>
            </div>
            <div class="form_box">
                <label class="label_bold" for="input_pwd">비밀번호 / PASSWORD</label>
                <div class="input_box">
                    <input type="password" class="input_info" id="input_pwd" name="pwd" placeholder="비밀번호를 입력하세요." required>
                </div>
            </div>
            <div class="form_box">
                <input type="checkbox" class="auto_login_chk" id="auto_login" name="auto_login">
                <label for="auto_login">자동 로그인</label>
            </div>
            <div class="button_login_wrap">
                <button type="submit" class="btn_login">로그인</button>
            </div>
            <div class="forgot_login">
                <a href="email_verification" onclick="setSourceAndSubmit('signup')">회원가입</a>
                <span> / </span>
                <a href="email_verification" onclick="setSourceAndSubmit('find_id')">아이디 찾기</a>
                <span> / </span>
                <a href="email_verification" onclick="setSourceAndSubmit('reset_pwd')">비밀번호 재설정</a>
            </div>
        </form>
    </div>
</div>
<footer></footer>
</body>
</html>