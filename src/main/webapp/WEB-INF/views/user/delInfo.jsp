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
    <script src="/js/setReferrer.js" defer></script>
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script> <!-- JS 경로 수정 -->
    <script>
        // DOMContentLoaded 이벤트 리스너를 사용하여 DOM이 완전히 로드된 후에 실행되도록 설정
        document.addEventListener('DOMContentLoaded', function() {
            const SS_USER_ID = "<%= (String) session.getAttribute("SS_USER_ID") %>"

            if (SS_USER_ID && SS_USER_ID.trim() !== "null") {
                document.getElementById("loginNav").style.display = "none";
            } else {
                document.getElementById("myPageNav").style.display = "none";
                document.getElementById("logoutNav").style.display = "none";
            }
        });

        function doOrNot(selection){
            document.getElementById('delOrNot').value=selection;
            document.getElementById('delInfo').submit();
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
        <a href="/user/index">홈</a>
        <a href="/interior/choose">인테리어</a>
        <a href="javascript:void(0);" id="loginNav" onclick="setReferrer()">로그인</a>
        <a href="/user/delOrUpdate" id="myPageNav" >마이페이지</a>
        <a href="/user/logout" id="logoutNav">로그아웃</a>
    </div>
</div>

<div class="content">
    <div class="container">
        <form method="post" action="/user/delInfo" id="delInfo">
            <div class="form_box">
                <div class="noti">
                    <div class="show_info" id="user_info">
                        <span>정말 탈퇴하시겠습니까?</span>
                    </div>
                    <div class="extra_top_margin input_box">
                        <input type="hidden" name="delOrNot" id="delOrNot">
                        <button type="button" class="two_button" id="goBack" onclick="doOrNot('cancel')">
                            <div>취소</div>
                        </button>
                        <button type="button" class="two_button" id="goToDelete" onclick="doOrNot('confirm')">
                            <div>회원탈퇴</div>
                        </button>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<footer></footer>
</body>
</html>
