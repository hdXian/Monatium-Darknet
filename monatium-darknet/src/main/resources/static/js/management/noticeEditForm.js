
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
                            const response = await fetch('/api/images/upload?t=tmp', {
                                method: 'POST',
                                body: formData
                            });
                            const result = await response.json();

                            if (result.success) {
                                const range = quill.getSelection();
                                quill.insertEmbed(range.index, 'image', result.imageUrl);
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

// 폼 제출 전 에디터의 HTML을 숨겨진 textarea에 복사
document.querySelector('form').addEventListener('submit', function() {
    document.querySelector('#content').value = quill.root.innerHTML;
});
