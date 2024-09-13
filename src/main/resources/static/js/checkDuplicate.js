function checkDuplicate(type) {
    const value = document.getElementById(type).value;
    const messageElement = document.getElementById(type + 'Message');

    fetch('/check-duplicate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({ type: type, value: value }) // type을 input 박스의 id로 설정
    })
        .then(response => response.json())
        .then(data => {
            messageElement.textContent = data.message;
        })
        .catch(error => console.error('Error:', error));
}