<%--
  Created by IntelliJ IDEA.
  User: data21
  Date: 2024-09-11
  Time: 오후 12:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSansNeo.css' rel='stylesheet' type='text/css'>
    <link href='//spoqa.github.io/spoqa-han-sans/css/SpoqaHanSans-jp.css' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="<c:url value='/resources/static/css/userStyles.css'/>">
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script>
    <script src="<c:url value='/resources/static/js/setReferrer.js'/>" defer></script>
    <script type="text/javascript">
        document.addEventListener('DOMContentLoaded', function() {
            const savedId = '<c:out value="${sessionScope.savedId}" />';
            if (savedId) {
                document.getElementById('input_id').value = savedId;
            }
        });

        function setSourceAndSubmit(source) {
            document.getElementById('source').value = source;
            document.getElementById('signinForm').submit();
        }
    </script>
</head>
<body>
<header></header>
<div class="navbar">
    <img src="<c:url value='/resources/nestifyLogo.png'/>" alt="Logo" class="logo">
    <div class="menu">
        <a href="<c:url value='/K_PaaS/index.jsp'/>">홈</a>
        <a href="<c:url value='/interior.jsp'/>">인테리어</a>
        <a href="<c:url value='/User/signin.jsp'/>">로그인</a>
    </div>
</div>

<div class="content">
    <div class="container">
        <form id="signinForm" method="post" action="<c:url value='/User/email_verification.jsp'/>">
            <input type="hidden" id="source" name="source">
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
                <button type="submit" class="btn_login">로그인</button>
            </div>
            <div class="forgot_login">
                <a href="javascript:void(0);" onclick="setSourceAndSubmit('signup')">회원가입</a>
                <span> / </span>
                <a href="javascript:void(0);" onclick="setSourceAndSubmit('find_id')">아이디 찾기</a>
                <span> / </span>
                <a href="javascript:void(0);" onclick="setSourceAndSubmit('reset_pwd')">비밀번호 재설정</a>
            </div>
        </form>
    </div>
</div>
<footer></footer>
</body>
</html>

