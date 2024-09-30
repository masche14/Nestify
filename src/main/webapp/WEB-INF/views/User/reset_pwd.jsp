<%@ page import="kopo.poly.dto.UserInfoDTO" %><%--
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
    <script src="/js/setReferrer.js" defer></script>
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script> <!-- JS 경로 수정 -->
    <%
        // 세션에서 userId 값을 가져옴
        String userId = (String) session.getAttribute("userId");
        // 가져온 후 세션에서 해당 값을 제거
        session.removeAttribute("userId");

        UserInfoDTO emailResultDTO = (UserInfoDTO) session.getAttribute("emailResultDTO");

        String exists = emailResultDTO.getExistsYn();
    %>
    <script>
        var userId = "<%= userId %>";
        var exists = "<%= exists %>";

        document.addEventListener('DOMContentLoaded', function() {

            if (exists === "N") {
                alert("해당 이메일로 가입된 계정이 존재하지 않습니다.");
                window.location.href="signup_detail"
            }

            // 세션 스토리지에서 저장된 아이디를 가져오기
            if (userId!=="null"){
                document.getElementById('input_id').value = userId;
            }

            const deferentId = "<%= (String) session.getAttribute("deferentId") %>"

            if (deferentId && deferentId.trim() !== "null") {
                alert(deferentId)
            }

            const SS_USER_ID = "<%= (String) session.getAttribute("SS_USER_ID") %>"

            if (SS_USER_ID && SS_USER_ID.trim() !== "null") {
                document.getElementById("loginNav").style.display = "none";
            } else {
                document.getElementById("myPageNav").style.display = "none";
                document.getElementById("logoutNav").style.display = "none";
            }

            // 폼 제출 전 확인하는 함수
            function validateForm(event) {
                // 중복 확인이 완료되었으면 폼을 제출
                const pwd = document.getElementById("input_pwd").value;
                const pwd2 = document.getElementById("chk_pwd").value;

                if (pwd !== pwd2){
                    event.preventDefault();
                    alert("비밀번호와 비밀번호 확인에 입력된 값이\n일치하지 않습니다.");
                    return false;
                }
                return true;
            }

            // 회원가입 버튼 클릭 시 validateForm 실행
            document.getElementById('goToLogin').addEventListener('click', validateForm);
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
            <a href="/User/delOrUpdate" id="myPageNav" >마이페이지</a>
            <a href="/User/logout" id="logoutNav">로그아웃</a>
        </div>
    </div>

    <div class="content">
        <div class="container">
            <form method="post" action="/User/reset_pwd" id="resetPwdForm">
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
                    <label class="label_bold" for="chk_pwd">비밀번호 확인 / PASSWORD CHECK</label>
                    <div class="input_box">
                        <input type="password" class="input_info" id="chk_pwd" name="pwd2" placeholder="비밀번호를 확인하세요." required>
                    </div>
                </div>
                <div class="button_login_wrap">
                    <button type="submit" class="btn_reset_pwd" id="goToLogin">비밀번호 재설정</button>
                </div>
            </form>
        </div>
    </div>
</div>

<footer></footer>
</body>
</html>
