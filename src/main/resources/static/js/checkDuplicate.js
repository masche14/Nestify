document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM 로드 완료');

    // 중복 확인 함수
    function checkDuplicate(type) {
        const value = document.getElementById(type).value;
        const messageElement = document.getElementById(type + 'Message');

        // 요소가 제대로 선택되었는지 확인
        console.log('선택된 메시지 요소:', messageElement);

        if (!messageElement) {
            console.error('해당 ID를 가진 요소를 찾을 수 없습니다:', type + 'Message');
            return;
        }

        fetch('/User/check-duplicate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({ type: type, value: value })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log('서버 응답 데이터:', data); // 응답 확인을 위해 콘솔 출력
                messageElement.textContent = data.message || '응답 없음';
                messageElement.style.color = isDuplicateMessage(data.message) ? 'red' : 'green';
                messageElement.style.display = 'block'; // 메시지가 있을 때만 표시
            })
            .catch(error => {
                console.error('에러 발생:', error); // 에러 메시지 출력
                messageElement.textContent = '오류가 발생했습니다. 다시 시도해주세요.';
                messageElement.style.color = 'red';
                messageElement.style.display = 'block'; // 메시지가 있을 때만 표시
            });
    }

    // 중복 여부 메시지를 확인하여 스타일링하는 함수 (선택 사항)
    function isDuplicateMessage(message) {
        return message.includes('이미 존재합니다');
    }

    // 이벤트 리스너를 설정하여 HTML 요소와의 연결을 확인
    document.querySelectorAll('.side_btn').forEach(button => {
        button.addEventListener('click', (event) => {
            const type = event.target.previousElementSibling.id;
            checkDuplicate(type);
        });
    });
});
