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
    <script src="/js/setReferrer.js" defer></script>
    <script src="/js/checkDuplicate.js" defer></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // JSP에서 세션 에러 메시지를 자바스크립트 변수에 직접 전달
            const error = "<%= (String)session.getAttribute("error") %>";
            if (error && error.trim() !== "null") {
                alert(error); // 알림 표시
                document.getElementById('redirectForm').submit(); // 폼을 통해 페이지 이동
                return; // 에러가 있으면 이후 코드 실행 안 함
            }

            const SS_USER_ID = "<%= (String) session.getAttribute("SS_USER_ID") %>"

            if (SS_USER_ID && SS_USER_ID.trim() !== "null") {
                document.getElementById("loginNav").style.display = "none";
            } else {
                document.getElementById("myPageNav").style.display = "none";
                document.getElementById("logoutNav").style.display = "none";
            }

            // 모달 요소와 버튼, 체크박스 참조
            const modal = document.getElementById("termsModal");
            const closeButton = document.getElementsByClassName("close")[0];
            const agreeButton = document.getElementById("agreeButton");
            const termsCheckbox = document.getElementById("confirm_terms");
            const termsLabel = document.querySelector("label[for='confirm_terms']");
            let isAgreed = false;  // 동의 여부를 저장할 변수

            // 라벨 클릭 시 모달 열기
            termsLabel.addEventListener('click', function (event) {
                event.preventDefault();  // 기본 동작 막기
                modal.style.display = "flex";
                isAgreed = true;
            });

            // 모달 닫기 버튼 클릭 시 모달 닫기
            closeButton.addEventListener('click', function () {
                modal.style.display = "none";
            });

            // 동의 버튼 클릭 시 체크박스 체크하고 모달 닫기
            agreeButton.addEventListener('click', function () {
                termsCheckbox.checked = true;
                modal.style.display = "none";
            });

            // 모달 외부 클릭 시 모달 닫기
            window.addEventListener('click', function (event) {
                if (event.target === modal) {
                    modal.style.display = "none";
                }
            });

            termsCheckbox.addEventListener('click', function (event) {
                if (!isAgreed) {
                    event.preventDefault();  // 기본 체크/해제 동작 막기
                    alert("이용약관을 먼저 확인하고 동의해야 합니다.");
                }
            });

            // 폼 제출 전 확인하는 함수
            function validateForm(event) {
                if (!idChecked || !nicknameChecked) {
                    event.preventDefault();  // 폼 제출을 막음
                    alert("아이디와 닉네임 중복 여부를 확인해주세요.");
                    return false;
                }

                console.log(check_id);
                console.log(check_nick);

                console.log(document.getElementById("input_id").value);

                if (document.getElementById("input_id").value !== check_id.trim()){
                    event.preventDefault();
                    alert("아이디 중복확인을 다시 진행해주세요.")
                    return false;
                }

                if (document.getElementById("input_nickname").value !== check_nick.trim()){
                    event.preventDefault();
                    alert("닉네임 중복확인을 다시 진행해주세요.");
                    return false;
                }

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

<%
    // 에러 메시지를 출력한 후 세션에서 제거
    if (session.getAttribute("error") != "null") {
        session.removeAttribute("error");
    }
%>

<!-- 이용약관 모달 -->
<div id="termsModal" class="modal">
    <div class="modal-content">
        <div class="close-area">
            <span class="close">&times;</span>
            <p style="text-align: center; font-weight: bolder; font-size: larger">이용약관</p>
        </div>
        <div class="terms-content">
            <p>회원가입을 위해 아래와 같이 개인정보를 수집하고 있습니다.</p>
            <p>
                - 수집 항목: 이름, 이메일 주소, 성별
                <br>- 수집 목적: 회원 식별 및 서비스 제공
                <br>- 보유 기간: 회원 탈퇴 시까지
            </p>
            <p>개인정보 수집 및 이용에 동의합니다.</p>
        </div>
        <button class="agree-btn" id="agreeButton">동의</button>
    </div>
</div>

<!-- 리다이렉트 폼 -->
<form id="redirectForm" action="/user/find_id" method="get" style="display: none;"></form>

<!-- 네비게이션 바 -->
<div class="navbar">
    <!-- 좌측 로고 -->
    <img src="/nestifyLogo.png" alt="Logo" class="logo">

    <!-- 우측 메뉴 -->
    <div class="menu">
        <a href="/user/index">홈</a>
        <a href="/interior/choose">인테리어</a>
        <a href="javascript:void(0);" id="loginNav" onclick="setReferrer()">로그인</a>
        <a href="/user/delOrUpdate" id="myPageNav" >마이페이지</a>
        <a href="/user/logout" id="logoutNav">로그아웃</a>
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
                    <span class="margin_left_4px">성별</span>
                    <input type="radio" id="male" name="gender" value="남성" required>
                    <label class="margin_lef_zero" for="male">남성</label>
                    <input type="radio" id="female" name="gender" value="여성">
                    <label class="margin_lef_zero" for="female">여성</label>
                </div>
            </div>
            <div class="form_box">
                <label class="label_bold" for="input_id">아이디 / ID</label>
                <div class="input_box">
                    <input type="text" class="send_code input_info" id="input_id" name="id" placeholder="아이디를 입력하세요." required>
                    <button type="button" class="side_btn" id="id_check" onclick="checkDuplicate('input_id')">중복확인</button>
                </div>
                <p id="input_idMessage"></p> <!-- 아이디 중복 확인 결과 표시 -->
            </div>
            <div class="form_box">
                <label class="label_bold" for="input_nickname">닉네임 / NICKNAME</label>
                <div class="input_box">
                    <input type="text" class="send_code input_info" id="input_nickname" name="nickname" placeholder="닉네임을 입력하세요." required >
                    <button type="button" class="side_btn" id="nick_check" onclick="checkDuplicate('input_nickname')">중복확인</button>
                </div>
                <p id="input_nicknameMessage"></p> <!-- 닉네임 중복 확인 결과 표시 -->
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

