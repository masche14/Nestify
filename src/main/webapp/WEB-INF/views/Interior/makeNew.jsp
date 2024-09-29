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
    <link rel="stylesheet" href="/css/makeNewStyles.css">
    <script src="/js/setReferrer.js" defer></script>
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script> <!-- JS 경로 수정 -->
    <script src="/js/attachImage.js" defer></script>
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
                document.getElementById("prompt_container").style.display="none";
                document.getElementById("select_container").style.display="flex";

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

            document.getElementById("submit").addEventListener("click", validateForm);

            document.getElementById("remake").addEventListener("click", function () {
                document.getElementById("promptInput").value="";
                document.getElementById("prompt_container").style.display="flex";
                document.getElementById("select_container").style.display="none";
            });

            document.getElementById("goNext").addEventListener("click", function (){
                window.location.href="/Interior/result";
            })
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
            <a href="/User/pwd_verification" id="myPageNav" >마이페이지</a>
            <a href="/User/logout" id="logoutNav">로그아웃</a>
        </div>
    </div>


    <div class="content set-min-width content-width">
        <div class="top">
            <span class="head-line">방의 사진을 첨부하고</span>
            <span class="head-line">원하는 인테리어 스타일을 입력하세요</span>
        </div>
        <form action="upload" method="post" enctype="multipart/form-data" class="no-bottom-margin form-center">
            <div class="image-box" id="imageBox">
                <p>이미지를 첨부하려면 클릭하세요</p>
                <img id="previewImage" alt="Image Preview">
            </div>

            <!-- 파일 입력 필드에 별도의 스타일 적용 -->
            <input type="file" id="fileInput" name="image" class="file-input" accept="image/*">

            <div class="form-container" id="prompt_container">
                <input class="full" type="text" id="promptInput" name="prompt" placeholder="프롬프트를 입력하세요">
                <button class="btn_generate" type="button" id="submit">전송</button>
            </div>
        </form>
        <div class="extra_top_margin input_box select" id="select_container">
            <button type="button" class="two_button" id="remake">재생성</button>
            <button type="button" class="two_button" id="goNext">다음</button>
        </div>
        <div class="bottom"></div>
    </div>
</div>
<footer></footer>

</body>
</html>
