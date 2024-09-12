function setSource(source) {
    sessionStorage.setItem('findIdSource', source);
    document.getElementsByClassName('form')[0].submit();
}