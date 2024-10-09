<%--
  Created by IntelliJ IDEA.
  User: data21
  Date: 2024-09-11
  Time: 오후 4:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>서비스선택</title>
    <link rel="stylesheet" href="/css/indexStyles.css">
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSansNeo.css' rel='stylesheet' type='text/css'>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSans-jp.css' rel='stylesheet' type='text/css'>
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script> <!-- JS 경로 수정 -->
    <script src="/js/setReferrer.js" defer></script>
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            <% session.removeAttribute("imageCount"); %>

            const SS_USER_ID = "<%= (String) session.getAttribute("SS_USER_ID") %>";
            console.log(SS_USER_ID);

            if (!SS_USER_ID || SS_USER_ID.trim() === "null") {
                alert("로그인 후 이용해주세요.");
                setReferrer();
            }

            if (SS_USER_ID && SS_USER_ID.trim() !== "null") {
                document.getElementById("loginNav").style.display = "none";
            } else {
                document.getElementById("myPageNav").style.display = "none";
                document.getElementById("logoutNav").style.display = "none";
            }

            document.querySelectorAll('.service').forEach(box => {
                box.addEventListener('mouseover', (event) => {
                    document.querySelectorAll('.service').forEach(otherBox => {
                        if (otherBox !== event.target) {
                            otherBox.classList.add('highlight');
                        }
                    });
                });

                box.addEventListener('mouseout', () => {
                    document.querySelectorAll('.service').forEach(otherBox => {
                        otherBox.classList.remove('highlight');
                    });
                });
            });
        });
    </script>
</head>
<body>

<header></header>

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
    <div class="top">
        <div class="head-line">AI를 활용하여</div>
        <div class="head-line">당신의 자취방을 꾸며보세요</div>
    </div>
    <div class="container no-margin">
        <a href="/Interior/makeNew" class="service-box">
            <div class="service">새로운 인테리어 생성하기</div>
        </a>
        <a href="/Interior/records" class="service-box">
            <div class="service">이전 인테리어 확인하기</div>
        </a>
    </div>
    <div class="bottom"></div>
</div>

<footer></footer>
</body>
</html>
