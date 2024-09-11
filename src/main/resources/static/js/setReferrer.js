function setReferrer() {
    // 현재 페이지의 URL을 로컬 스토리지에 저장
    sessionStorage.setItem('referrerUrl', window.location.href);

    window.location.href = "signin";
}

function goToReferrer() {
    const referrerUrl = sessionStorage.getItem('referrerUrl');
    if (referrerUrl) {
        window.location.href = referrerUrl;
    }
    // else {
    //     window.location.href = "/K_PaaS/index.html";
    // }
}