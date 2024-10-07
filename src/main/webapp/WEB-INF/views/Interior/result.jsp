<%--
  Created by IntelliJ IDEA.
  User: data21
  Date: 2024-09-24
  Time: 오전 9:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>신규 인테리어 분석</title>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSansNeo.css' rel='stylesheet' type='text/css'>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSans-jp.css' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="/css/userStyles.css">
    <link rel="stylesheet" href="/css/makeNewStyles.css">
    <script src="/js/setReferrer.js" defer></script>
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script> <!-- JS 경로 수정 -->
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const SS_USER_ID = "<%= (String) session.getAttribute("SS_USER_ID") %>"
            console.log(SS_USER_ID);

            if (SS_USER_ID && SS_USER_ID.trim()==="null"){
                alert("로그인 후 이용해주세요.");
                setReferrer();
                // window.location.href="/User/signin";
            }

            if (SS_USER_ID && SS_USER_ID.trim() !== "null") {
                document.getElementById("loginNav").style.display = "none";
            } else {
                document.getElementById("myPageNav").style.display = "none";
                document.getElementById("logoutNav").style.display = "none";
            }

            function validateForm(event) {
                const image = document.getElementById("fileInput").value;
                const prompt = document.getElementById("promptInput").value;

                if (!image) {
                    event.preventDefault();
                    alert("이미지를 첨부해주세요.")
                    return false;
                }

                if(!prompt.trim()) {
                    event.preventDefault();
                    alert("원하는 인테리어 스타일을 입력해주세요.");
                    return false;
                }

                return true;
            }

            document.getElementById("submit").addEventListener("click", validateForm)
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
        <a href="/User/index">홈</a>
        <a href="/Interior/choose">인테리어</a>
        <a href="javascript:void(0);" id="loginNav" onclick="setReferrer()">로그인</a>
        <a href="/User/delOrUpdate" id="myPageNav" >마이페이지</a>
        <a href="/User/logout" id="logoutNav">로그아웃</a>
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
                <div class="form_box">
                    <label class="label_bold" for="input_nickname">닉네임 변경 / CHG_NICKNAME</label>
                    <div class="input_box">
                        <input type="text" class="send_code input_info" id="input_nickname" name="nickname" placeholder="닉네임을 입력하세요." disabled>
                        <button type="button" class="side_btn" id="nick_check" onclick="checkDuplicate('input_nickname')">중복확인</button>
                    </div>
                    <p id="input_nicknameMessage"></p> <!-- 닉네임 중복 확인 결과 표시 -->
                </div>
                <div class="form_box">
                    <label class="label_bold" for="input_pwd">비밀번호 변경 / CHG_PASSWORD</label>
                    <div class="input_box">
                        <input type="password" class="input_info" id="input_pwd" name="pwd" placeholder="비밀번호를 입력하세요." disabled>
                    </div>
                </div>
                <div class="form_box">
                    <label class="label_bold" for="chk_pwd">비밀번호 확인 / PASSWORD CHECK</label>
                    <div class="input_box">
                        <input type="password" class="input_info" id="chk_pwd" name="chk_pwd" placeholder="비밀번호를 확인하세요." disabled>
                    </div>
                </div>
                <div class="form_box">
                    <label class="label_bold" for="input_email">이메일 / E-MAIL</label>
                    <div class="input_box">
                        <input type="email" class="send_code input_info" id="input_email" name="email" placeholder="이메일을 입력하세요." disabled>
                        <button type="button" id="send_code" class="side_btn" onclick="getEmailExists('input_email')">코드전송</button>
                    </div>
                </div>
                <div class="form_box">
                    <label class="label_bold" for="email_confirm">인증코드 입력</label>
                    <div class="input_box">
                        <input type="text" class="confirm_code input_info" id="email_confirm" name="email_confirm" placeholder="인증코드 입력." disabled>
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
