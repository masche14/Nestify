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
    <title>신규 인테리어</title>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSansNeo.css' rel='stylesheet' type='text/css'>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSans-jp.css' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="/css/userStyles.css">
    <script src="/js/getExists.js" defer></script>
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script> <!-- JS 경로 수정 -->
    <script src="/js/attachImage.js" defer></script>
    <link rel="stylesheet" href="/css/makeNew.css">
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const SS_USER_ID = "<%= (String) session.getAttribute("SS_USER_ID") %>"

            if (SS_USER_ID && SS_USER_ID.trim() !== "null") {
                document.getElementById("loginNav").style.display = "none";
            } else {
                document.getElementById("myPageNav").style.display = "none";
                document.getElementById("logoutNav").style.display = "none";
            }
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
        <a href="#">인테리어</a>
        <a href="/User/signin">로그인</a>
        <a href="#" id="myPageNav" >마이페이지</a>
        <a href="logout" id="logoutNav">로그아웃</a>
    </div>
</div>

<div class="content">
    <form action="/upload" method="post" enctype="multipart/form-data" class="no-bottom-margin form-center">
        <div class="image-box" id="imageBox">
            <p>이미지를 첨부하려면 클릭하세요</p>
            <img id="previewImage" alt="Image Preview">
        </div>

        <!-- 파일 입력 필드에 별도의 스타일 적용 -->
        <input type="file" id="fileInput" name="image" class="file-input" accept="image/*">

        <div class="form-container">
            <input type="text" id="promptInput" name="prompt" placeholder="프롬프트를 입력하세요">
            <button class="btn_generate" type="submit">전송</button>
        </div>
    </form>
<%--    <div class="bottom"></div>--%>
</div>

<footer></footer>
</body>
</html>
