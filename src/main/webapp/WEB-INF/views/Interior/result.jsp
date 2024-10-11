<%@ page import="kopo.poly.dto.DetailDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="kopo.poly.util.CmmUtil" %><%--
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
    <% List<DetailDTO> rList = (List<DetailDTO>) session.getAttribute("analysisResult"); %>
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const SS_USER_ID = "<%= (String) session.getAttribute("SS_USER_ID") %>"
            console.log(SS_USER_ID);

            if (SS_USER_ID && SS_USER_ID.trim()==="null"){
                alert("로그인 후 이용해주세요.");
                setReferrer();
            }

            if (SS_USER_ID && SS_USER_ID.trim() !== "null") {
                document.getElementById("loginNav").style.display = "none";
            } else {
                document.getElementById("myPageNav").style.display = "none";
                document.getElementById("logoutNav").style.display = "none";
            }

            const generatedImgUrl = "<%=(String) session.getAttribute("generatedImgUrl") %>";
            console.log("generatedUrl : "+generatedImgUrl);
            document.getElementById('generatedImg').src = generatedImgUrl;

            // function validateForm(event) {
            //     return true;
            // }

            // document.getElementById("submit").addEventListener("click", validateForm)
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
                <img src="" id="generatedImg" alt="generatedimage" class="image-records"></img>
            </div>
            <div class="detail_wrap">
                <%
                    for (DetailDTO dto : rList) {
                %>
                <div>
                    <div><%=CmmUtil.nvl(dto.getProductName())%> /
                        <%=CmmUtil.nvl(dto.getCategory())%> /
                        <%=CmmUtil.nvl(dto.getColor())%>
                    </div>
                    <div><%=CmmUtil.nvl(dto.getFeatures())%></div>
                </div>
                <%
                    }
                %>
            </div>
        </div>
        <div class="recommend_wrap">
            <%
                for (DetailDTO dto : rList) {
            %>
            <div class="recommend">
                <div><%=CmmUtil.nvl(dto.getProductName())%> /
                    <%=CmmUtil.nvl(dto.getCategory())%> /
                    <%=CmmUtil.nvl(dto.getColor())%>
                </div>
                <div><%=CmmUtil.nvl(dto.getFeatures())%></div>
            </div>
            <%
                }
            %>
        </div>
    </div>
</div>


<footer></footer>
</body>
</html>
