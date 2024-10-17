<%@ page import="kopo.poly.dto.GRecordDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="kopo.poly.dto.DetailDTO" %>
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
        String jsonResList = (String) session.getAttribute("jsonResList");
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
            const resList = JSON.parse('<%= jsonResList %>');

            console.log(maxNum);

            // 이미지 업데이트 함수
            function updateImage() {
                if (maxNum > 0) {
                    const imgDir = rList[num].generatedImgDir;
                    const imgName = rList[num].generatedImgName;
                    const generateSeq = rList[num].generateSeq;
                    console.log("generateSeq : " + generateSeq);

                    document.getElementById('previewImage').src = '/' + imgDir + '/' + imgName;
                    document.getElementById('pageCounter').innerText = (num + 1) + ' / ' + maxNum;

                    const filteredItems = resList.filter(function (item) {
                        return item.generateSeq === generateSeq;
                    });

                    console.log("필터링된 내용 : ", filteredItems);

                    // 필터링된 내용을 detailModal에 출력
                    const detailWrapper = document.getElementById("detail_wrapper");
                    detailWrapper.innerHTML = ""; // 기존 내용을 초기화
                    filteredItems.forEach(function (item) {
                        // 개별적으로 각 div 요소에 내용을 추가
                        const detailDiv = document.createElement("div");

                        const productInfoDiv = document.createElement("div");
                        productInfoDiv.innerHTML = item.productName + " / " + item.color;

                        const featuresDiv = document.createElement("div");
                        featuresDiv.innerHTML = item.features;

                        detailDiv.appendChild(productInfoDiv);
                        detailDiv.appendChild(featuresDiv);

                        detailWrapper.appendChild(detailDiv);
                    });
                } else {
                    document.getElementById('previewImage').style.display = "none";
                    document.getElementById('ifEmpty').style.display = "flex";
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

            // 이미지 클릭 시 detailModal 띄우기
            document.getElementById('previewImage').addEventListener("click", function () {
                document.getElementById('detailModal').style.display = "flex";
            });

            document.getElementById('ifEmpty').addEventListener("click", function (){
               window.location.href="/Interior/makeNew";
            });

            // 모달 닫기 버튼 클릭 시 모달 닫기
            document.getElementById('closeModal').addEventListener("click", function () {
                document.getElementById('detailModal').style.display = "none";
            });

            // 모달 외부를 클릭했을 때 모달 닫기
            window.addEventListener("click", function(event) {
                const modal = document.getElementById('detailModal');
                if (event.target === modal) {
                    modal.style.display = "none";
                }
            });

            window.addEventListener("keydown", function(event) {
                const modal = document.getElementById('detailModal');
                if (event.key === "Escape" && modal.style.display === "flex") {
                    modal.style.display = "none";
                }
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

<!-- 모달 창 -->
<div id="detailModal" class="modal">
    <div class="modal-content autoWidth">
        <div class="close_wrapper">
            <span id="closeModal" class="close">&times;</span>
        </div>
        <div id="detail_wrapper" class="detail-modal">
            <!-- 여기에 동적으로 필터링된 내용이 들어갑니다 -->
        </div>
    </div>
</div>

<div class="content">
    <div class="top">
        <div class="head-line">이미지를 클릭하여</div>
        <div class="head-line">상세 내용을 확인하세요</div>
    </div>
    <div class="records-content">
        <button type="button" class="page-controll" id="goPrev">&lt;</button>
        <div class="records-container" style="height: auto">
            <div class="main">
                <div id="imageBox">
                    <img class="image-records" id="previewImage" src="" style="cursor: pointer;">
                    <div class="if-empty" id="ifEmpty">
                        <span>인테리어 디자인 생성 기록이 없습니다.</span>
                        <span>디자인을 생성하려면 클릭하세요.</span>
                    </div>
                </div>
                <div id="details" style="display: none"></div>
            </div>
            <div id="pageCounter" class="page-counter"></div>
        </div>
        <button type="button" class="page-controll" id="goNext">&gt;</button>
    </div>
    <div class="bottom"></div>
</div>

<footer></footer>
</body>
</html>
