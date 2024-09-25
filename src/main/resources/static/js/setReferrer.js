function setReferrer() {
    // 현재 페이지의 URL을 로컬 스토리지에 저장
    sessionStorage.setItem('referrerUrl', window.location.href);

    window.location.href = "signin";
}