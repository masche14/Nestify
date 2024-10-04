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
    <script type="text/javascript" src="/js/jquery-3.6.0.min.js"></script>
    <script src="/js/attachImage.js" defer></script>
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

            // 전송 버튼 클릭 시
            document.getElementById("submit").addEventListener("click", async function(event) {
                event.preventDefault();

                const fileInput = document.getElementById("fileInput");
                const promptInput = document.getElementById("promptInput");
                const file = fileInput.files[0];
                const prompt = promptInput.value;

                const modal = document.getElementById("termsModal");

                if (!file) {
                    alert("이미지를 첨부해주세요.");
                    return;
                }

                if (!prompt.trim()) {
                    alert("원하는 인테리어 스타일을 입력해주세요.");
                    return;
                }

                // FormData를 사용하여 이미지와 프롬프트를 전송
                const formData = new FormData();
                formData.append("image", file);
                formData.append("prompt", prompt);

                modal.style.display="flex";

                try {
                    // 서버로 이미지와 프롬프트 전송
                    const response = await fetch('/Interior/upload', {
                        method: 'POST',
                        body: formData
                    });

                    if (response.ok) {
                        const result = await response.json();
                        const generatedImageUrl = result.generatedImageUrl; // API에서 생성된 이미지 URL

                        // 미리보기 이미지로 표시
                        document.getElementById("previewImage").src = generatedImageUrl;
                        document.getElementById("previewImage").style.display = 'block';

                        // 프롬프트 입력 폼 숨기고, 선택 컨테이너 표시
                        document.getElementById("prompt_container").style.display = "none";
                        document.getElementById("select_container").style.display = "flex";
                    } else {
                        alert("이미지 생성 요청에 실패했습니다.");
                    }
                } catch (error) {
                    console.error("에러 발생: ", error);
                    alert("요청 처리 중 문제가 발생했습니다.");
                }

                modal.style.display="none";
            });

            // 재생성 버튼 클릭 시
            document.getElementById("remake").addEventListener("click", function () {
                // 프롬프트 입력 창 초기화
                document.getElementById("promptInput").value = "";

                // 다시 입력 폼을 보이고 선택 컨테이너 숨김
                document.getElementById("prompt_container").style.display = "flex";
                document.getElementById("select_container").style.display = "none";

                // 이미지 미리보기는 유지
                const previewImage = document.getElementById("previewImage");
                if (previewImage.src) {
                    previewImage.style.display = 'block'; // 이미지 미리보기를 다시 보여줌
                } else {
                    previewImage.style.display = 'none'; // 이미지가 없으면 숨김
                }
            });

            // 다음 버튼 클릭 시
            document.getElementById("goNext").addEventListener("click", async function () {
                document.getElementById("promptInput").value = "";

                // 세션에 저장된 API로 생성된 이미지 URL 가져오기
                const generatedImageUrl = document.getElementById("previewImage").src;

                // 서버에 API로 생성된 이미지 저장 요청
                try {
                    const response = await fetch('/Interior/saveGeneratedImage', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            imageUrl: generatedImageUrl // API에서 생성된 이미지 URL을 서버로 전송
                        })
                    });

                    if (response.ok) {
                        // 이미지 저장이 완료되면 결과 페이지로 이동
                        window.location.href = "/Interior/result";
                    } else {
                        alert("이미지를 저장하는 데 실패했습니다.");
                    }
                } catch (error) {
                    console.error("에러 발생: ", error);
                    alert("이미지 저장 요청 중 문제가 발생했습니다.");
                }
            });
        });
    </script>
</head>
<body>
<header></header>

<!-- 이미지 생성중 모달 -->
<div id="termsModal" class="modal">
    <div class="modal-content">
        <div class="generate-message">
            <span>요청하신 인테리어 디자인을 생성 중입니다.</span>
        </div>
    </div>
</div>

<!-- 네비게이션 바 -->
<div class="navbar">
    <!-- 좌측 로고 -->
    <img src="/nestifyLogo.png" alt="Logo" class="logo">

    <!-- 우측 메뉴 -->
    <div class="menu">
        <a href="/User/index">홈</a>
        <a href="#">인테리어</a>
        <a href="javascript:void(0);" id="loginNav" onclick="setReferrer()">로그인</a>
        <a href="/User/delOrUpdate" id="myPageNav">마이페이지</a>
        <a href="/User/logout" id="logoutNav">로그아웃</a>
    </div>
</div>

<div class="content set-min-width content-width">
    <div class="top">
        <span class="head-line">방의 사진을 첨부하고</span>
        <span class="head-line">원하는 인테리어 스타일을 입력하세요</span>
    </div>
    <form id="interiorForm" action="/Interior/upload" method="post" enctype="multipart/form-data" class="no-bottom-margin form-center">
        <div class="image-box" id="imageBox">
            <p>이미지를 첨부하려면 클릭하세요</p>
            <img id="previewImage" alt="Image Preview" style="display: none;">
        </div>

        <!-- 파일 입력 필드에 별도의 스타일 적용 -->
        <input type="file" id="fileInput" name="image" class="file-input" accept="image/*">

        <div class="form-container" id="prompt_container">
            <input class="full" type="text" id="promptInput" name="prompt" placeholder="프롬프트를 입력하세요">
            <button class="btn_generate" type="submit" id="submit">전송</button>
        </div>
    </form>
    <div class="extra_top_margin input_box select" id="select_container" style="display: none;">
        <button type="button" class="two_button" id="remake">재생성</button>
        <button type="button" class="two_button" id="goNext">다음</button>
    </div>
    <div class="bottom"></div>
</div>
<footer></footer>

</body>
</html>
