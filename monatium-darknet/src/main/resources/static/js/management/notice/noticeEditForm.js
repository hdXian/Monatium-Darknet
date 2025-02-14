
// CSRF 토큰 읽기
const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

// Quill 초기화
const quill = new Quill('#editor', {
    theme: 'snow',
    placeholder: '공지사항 내용을 입력하세요...',
    modules: {
        toolbar: {
            container: [
                [{ 'header': [1, 2, false] }],
                ['bold', 'italic', 'underline'],
                ['link', 'image'],
                [{ 'list': 'ordered' }, { 'list': 'bullet' }]
            ],
            handlers: {
                image: function () {
                    const input = document.createElement('input');
                    input.type = 'file';
                    input.accept = 'image/*';
                    input.onchange = async function () {
                        const file = input.files[0];
                        if (!file) return;

                        const formData = new FormData();
                        formData.append('file', file);

                        try {
                            const response = await fetch('/api/images/upload/tmp', {
                                method: 'POST',
                                headers: {
                                    [csrfHeader]: csrfToken
                                },
                                body: formData
                            });

                            if (response.ok) {
                                const imageUrl = await response.text(); // 서버에서 반환한 imageUrl 읽기
                                const range = quill.getSelection();
                                quill.insertEmbed(range.index, 'image', imageUrl);
                            } else {
                                alert('이미지 업로드 실패');
                            }
                        } catch (error) {
                            alert('이미지 업로드 중 오류가 발생했습니다.');
                        }
                    };

                    input.click();
                }
            }
        }
    }
});

// Quill 에디터에 서버에서 받은 HTML 본문을 안전하게 삽입
window.onload = function() {
    const content = document.querySelector('#content').value;
    quill.clipboard.dangerouslyPasteHTML(content);
};


// 취소 확인 로직
function confirmCancel(button) {
    // 버튼이 속한 폼을 가져옴
    const form = button.closest('form');

    // 사용자 확인 후 폼 제출
    if (confirm("취소하시겠습니까? 현재 작성된 데이터는 저장되지 않습니다.")) {
        // action 파라미터를 명시적으로 설정
        const hiddenField = document.createElement('input');
        hiddenField.type = 'hidden';
        hiddenField.name = 'action';
        hiddenField.value = 'cancel';
        form.appendChild(hiddenField);

        form.submit(); // 폼을 명시적으로 제출
    }
}

// 썸네일 이미지 미리보기
function previewImage(input) {
    const preview = document.getElementById('thumbnail-preview');
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function (e) {
            preview.src = e.target.result;
            preview.style.display = 'block';
        };
        reader.readAsDataURL(input.files[0]);
    } else {
        preview.src = '';
        preview.style.display = 'none';
    }
}

// "취소" 버튼 이벤트 추가
document.querySelector('#btnCancel').addEventListener('click', function () {
    confirmCancel(this);
});

// "임시 저장" 버튼 이벤트 추가
document.querySelector('#btnSave').addEventListener('click', function () {
    document.querySelector('#content').value = quill.root.innerHTML;
});


// "작성하기" 버튼 이벤트 추가
document.querySelector('#btnComplete').addEventListener('click', function () {
    // 버튼이 속한 폼을 가져옴
    const form = this.closest('form');

    if (confirm('공지사항을 수정하시겠습니까?')) {
        // 추가적인 작성 처리 로직
        document.querySelector('#content').value = quill.root.innerHTML;

        // action 파라미터를 명시적으로 설정
        const hiddenField = document.createElement('input');
        hiddenField.type = 'hidden';
        hiddenField.name = 'action';
        hiddenField.value = 'complete';
        form.appendChild(hiddenField);

        form.submit(); // 폼 제출
    }

});

// 썸네일 미리보기 이벤트 등록
document.addEventListener("DOMContentLoaded", function () {
    const imageInputs = document.querySelectorAll(".img-input");

    // 모든 이미지 업로드 input 요소에 대해 이벤트 추가
    imageInputs.forEach(input => {
        input.addEventListener("change", function() {
            previewImage(this);
        });
    });

});
