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
                            const response = await fetch('/api/images/upload?t=tmp', {
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

// 폼 제출 시 데이터 전송
document.querySelector('form').addEventListener('submit', function (event) {
    document.querySelector('#content').value = quill.root.innerHTML;
});
