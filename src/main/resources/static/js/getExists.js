// 중복 확인 함수
function getEmailExists(type) {
    // 입력된 값과 메시지를 표시할 요소를 가져옵니다.
    const inputElement = document.getElementById(type);
    const value = inputElement.value;
    // const messageElement = document.getElementById(type + 'Message');
    // 폼 데이터를 가져와 FormData 객체로 생성
    const formData = new FormData(document.querySelector("#emailVerificationForm"));
    const jsonData = Object.fromEntries(formData.entries());
    console.log(jsonData); // jsonData가 이메일 값을 포함하는지 확인


    if (!value.trim()) {
        alert("이메일을 입력해주세요");
        return
    }

    // fetch API를 사용하여 POST 요청
    fetch('getEmailExists', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json', // 요청을 JSON 형식으로 설정
        },
        body: JSON.stringify(jsonData) // JSON 데이터 전송
    })
        .then(response => response.json()) // JSON 응답을 파싱
        .then(json => {
            alert("이메일로 인증번호가 발송되었습니다. \n받은 메일의 인증번호를 입력하기 바랍니다.");
            const emailAuthNumber = json.authNumber;
            console.log(emailAuthNumber);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('서버 요청 중 오류가 발생했습니다.');
        });
}