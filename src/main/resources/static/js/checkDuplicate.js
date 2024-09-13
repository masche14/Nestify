// 중복 확인 함수
function checkDuplicate(type) {
    // 입력된 값과 메시지를 표시할 요소를 가져옵니다.
    const value = document.getElementById(type).value;
    const messageElement = document.getElementById(type + 'Message');

    // 요소가 제대로 선택되었는지 확인
    console.log('선택된 메시지 요소:', messageElement);

    if (!messageElement) {
        console.error('해당 ID를 가진 요소를 찾을 수 없습니다:', type + 'Message');
        return;
    }

    fetch('check-duplicate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({ type: type, value: value }) // type을 input 박스의 id로 설정
    })
        .then(response => response.json())
        .then(data => {
            console.log('서버 응답 데이터:', data);
            // 서버로부터 받은 메시지를 해당 HTML 요소에 표시합니다.
            messageElement.textContent = data.message;
            // 스타일을 적용하거나 추가적으로 처리할 수 있습니다.
            messageElement.style.color = isDuplicateMessage(data.message) ? 'red' : 'green';
            messageElement.style.display = 'block'; // 메시지가 있을 때만 표시
        })
        .catch(error => {
            console.error('Error:', error);
            messageElement.textContent = '오류가 발생했습니다. 다시 시도해주세요.';
            messageElement.style.color = 'red';
            messageElement.style.display = 'block'; // 메시지가 있을 때만 표시
        });
}

// 중복 여부 메시지를 확인하여 스타일링하는 함수 (선택 사항)
function isDuplicateMessage(message) {
    return message.includes('이미 존재합니다');
}