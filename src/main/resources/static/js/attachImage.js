const imageBox = document.getElementById('imageBox');
const fileInput = document.getElementById('fileInput');
const previewImage = document.getElementById('previewImage');

// 이미지 박스를 클릭하면 파일 입력창을 연다
imageBox.addEventListener('click', () => {
    fileInput.click();
});

// 파일이 선택되면 이미지 미리보기를 업데이트
fileInput.addEventListener('change', function(event) {
    const file = event.target.files[0];
    if (file && file.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.onload = function (e) {
            previewImage.src = e.target.result;
            previewImage.style.display = 'block';
            imageBox.querySelector('p').style.display = 'none';
        };
        reader.readAsDataURL(file);
    }
});