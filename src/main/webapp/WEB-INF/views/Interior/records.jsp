<%@ page import="kopo.poly.dto.GRecordDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>인테리어 기록</title>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSansNeo.css' rel='stylesheet' type='text/css'>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSans-jp.css' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="/css/userStyles.css">
    <link rel="stylesheet" href="/css/makeNewStyles.css">
    <script src="/js/setReferrer.js" defer></script>
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script>
    <%
        String jsonRList = (String) session.getAttribute("jsonRList");  // 세션에서 jsonRList 가져오기
        List<GRecordDTO> rList = (List<GRecordDTO>) session.getAttribute("rList");
        int maxNum = rList.size();  // rList에서 크기 계산
    %>
    <script>
        document.addEventListener("DOMContentLoaded", function () {
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

            let num = 0;
            const maxNum = <%= maxNum %>;
            const rList = <%= jsonRList %>;  // 세션에서 가져온 jsonRList 사용

            console.log(maxNum);

            // 이미지 업데이트 함수
            function updateImage() {
                if (maxNum>0) {
                    const imgDir = rList[num].generatedImgDir;
                    const imgName = rList[num].generatedImgName;
                    document.getElementById('previewImage').src = '/' + imgDir + '/' + imgName;
                    // 페이지 카운터 업데이트
                    document.getElementById('pageCounter').innerText = (num + 1) + ' / ' + maxNum;
                } else {
                    document.getElementById('previewImage').style.display="none";
                    document.getElementById('ifEmpty').style.display="flex";
                }
            }

            // 이전 버튼 클릭 시
            document.getElementById('goPrev').addEventListener("click", function () {
                num--;
                if (num < 0) {
                    num = maxNum - 1;
                }
                updateImage();
            });

            // 다음 버튼 클릭 시
            document.getElementById('goNext').addEventListener("click", function () {
                num++;
                if (num >= maxNum) {
                    num = 0;
                }
                updateImage();
            });

            // 페이지 로드 시 첫 이미지 설정
            updateImage();
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
        <a href="/User/delOrUpdate" id="myPageNav">마이페이지</a>
        <a href="/User/logout" id="logoutNav">로그아웃</a>
    </div>
</div>

<div class="content">
    <div class="top"></div>
    <div class="records-content">
        <%--    <div class="top"></div>--%>
        <button type="button" class="page-controll" id="goPrev">&lt;</button>
        <div class="records-container" style="height: auto">
            <div class="main">
                <div id="imageBox">
                    <img class="image-records" id="previewImage" src="">
                    <div class="if-empty" id="ifEmpty">
                        인테리어 디자인 생성 기록이 없습니다.
                    </div>
                </div>
            </div>
            <div id="pageCounter" class="page-counter"></div>
        </div>
        <button type="button" class="page-controll" id="goNext">&gt;</button>
        <%--    <div class="bottom"></div>--%>
    </div>
    <div class="bottom"></div>
</div>

<footer></footer>
</body>
</html>
