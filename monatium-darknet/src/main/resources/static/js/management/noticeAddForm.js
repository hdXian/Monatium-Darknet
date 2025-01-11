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
                    console.log('이미지 업로드 시작');
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
                                body: formData
                            });
                            const result = await response.json();

                            if (result.success) {
                                console.log('이미지 업로드 성공:', result.imageUrl);

                                const range = quill.getSelection();
                                quill.insertEmbed(range.index, 'image', result.imageUrl);
                            } else {
                                alert('이미지 업로드 실패');
                            }
                        } catch (error) {
                            alert('이미지 업로드 중 오류가 발생했습니다.');
                            console.error(error);
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

// "임시 저장" 버튼 이벤트 추가
document.querySelector('#btnSave').addEventListener('click', function () {
    console.log('임시 저장 버튼 클릭됨');

    // 추가적인 저장 처리 로직
    console.log(quill.root.innerHTML);
    document.querySelector('#content').value = quill.root.innerHTML;
});


// "작성하기" 버튼 이벤트 추가
document.querySelector('#btnComplete').addEventListener('click', function () {
    if (confirm('공지사항을 등록하시겠습니까?')) {
        console.log('작성하기 버튼 클릭됨');
        // 추가적인 작성 처리 로직
        document.querySelector('#content').value = quill.root.innerHTML;
    } else {
        event.preventDefault(); // 기본 동작 중단
    }
});

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

