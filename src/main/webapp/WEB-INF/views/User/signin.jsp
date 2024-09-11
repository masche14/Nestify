<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
    <link rel="stylesheet" href="/css/userStyles.css"/> <!-- CSS 경로 수정 -->
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script> <!-- JS 경로 수정 -->
    <script type="text/javascript">
        // HTML 로딩이 완료된 후 실행됨
        $(document).ready(function () {
            // 로그인 버튼 클릭 이벤트
            $("#btnLogin").on("click", function () {
                doLogin(); // 로그인 처리 함수 실행
            });
        });

        // 로그인 처리 함수
        function doLogin() {
            let f = document.getElementById("signinForm"); // form 태그

            // 아이디와 비밀번호 입력 체크
            if (f.id.value === "") {
                alert("아이디를 입력하시기 바랍니다.");
                f.id.focus();
                return;
            }
            if (f.pwd.value === "") {
                alert("비밀번호를 입력하시기 바랍니다.");
                f.pwd.focus();
                return;
            }

            // Ajax 호출로 로그인 처리 요청
            $.ajax({
                url: "/User/signin", // 로그인 요청 URL
                type: "post", // 전송 방식은 POST
                dataType: "JSON", // 응답은 JSON 형식으로 받음
                data: $("#signinForm").serialize(), // form 데이터를 자동으로 직렬화하여 전송
                success: function (json) { // 성공 시 실행
                    alert(json.msg); // 서버로부터 받은 메시지 출력
                    if (json.success) {
                        location.href = "/index"; // 로그인 성공 시 홈으로 이동
                    }
                },
                error: function () { // 실패 시 실행
                    alert("로그인 중 오류가 발생했습니다. 다시 시도해 주세요.");
                }
            });
        }
    </script>
    //

<%--    <script>--%>
<%--        // DOMContentLoaded 이벤트 리스너를 사용하여 DOM이 완전히 로드된 후에 실행되도록 설정--%>
<%--        document.addEventListener('DOMContentLoaded', function() {--%>
<%--            // 첫 번째 스크립트: savedId가 있으면 input_id 요소에 값을 설정--%>
<%--            const savedId = sessionStorage.getItem('savedId');--%>
<%--            if (savedId) {--%>
<%--                document.getElementById('input_id').value = savedId;--%>
<%--                sessionStorage.removeItem('savedId');--%>
<%--            }--%>
<%--        });--%>

<%--        // source를 설정하고 폼을 생성하여 제출하는 함수--%>
<%--        function setSourceAndSubmit(source) {--%>
<%--            sessionStorage.setItem('source', source);--%>
<%--            const form = document.createElement('form');--%>
<%--            form.method = 'POST';--%>
<%--            form.action = 'email_verification.html';--%>
<%--            document.body.appendChild(form);--%>
<%--            form.submit();--%>
<%--        }--%>
<%--    </script>--%>
</head>
<body>
<header></header>
<!-- 네비게이션 바 -->
<div class="navbar">
    <!-- 좌측 로고 -->
    <img src="/nestifyLogo.png" alt="Logo" class="logo">

    <!-- 우측 메뉴 -->
    <div class="menu">
        <a href="/K_PaaS/index.html">홈</a>
        <a href="#">인테리어</a>
        <a href="javascript:void(0);" onclick="setReferrer()">로그인</a>
    </div>
</div>

<div class="content">
    <div class="container">
        <form id="signinForm">
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
                <input type="checkbox" class="auto_login_chk" id="auto_login" name="auto_login">
                <label for="auto_login">자동 로그인</label>
            </div>
            <div class="button_login_wrap">
                <button type="button" class="btn_login" onclick="goToReferrer()">로그인</button>
            </div>
            <div class="forgot_login">
                <a href="./email_verification.html" onclick="setSourceAndSubmit('signup')">회원가입</a>
                <span> / </span>
                <a href="email_verification.html" onclick="setSourceAndSubmit('find_id')">아이디 찾기</a>
                <span> / </span>
                <a href="email_verification.html" onclick="setSourceAndSubmit('reset_pwd')">비밀번호 재설정</a>
            </div>
        </form>
    </div>
</div>
<footer></footer>
</body>
</html>