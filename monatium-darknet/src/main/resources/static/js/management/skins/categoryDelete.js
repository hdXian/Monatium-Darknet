
document.querySelectorAll('.delCategory-btn').forEach(button => {
    button.addEventListener('click', function (event) {

        const categoryName = event.target.getAttribute('data-name');

        // 확인 메시지
        const message = `정말 ${categoryName} 카테고리를 삭제하시겠습니까?`

        if (confirm(message)) {
            const categoryId = event.target.getAttribute('data-id');
            const deleteUrl = `/management/skins/categories/del/${categoryId}`;

            fetch(deleteUrl, {
                method: 'POST'
            })
            .then(response => {
                if (response.redirected) {
                    // 서버에서 리다이렉트된 URL로 이동
                    window.location.href = response.url;
                } else if (!response.ok) {
                    // 에러 처리
                    alert('삭제 요청 처리에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('오류가 발생했습니다. 관리자에게 문의하세요.');
            });
        }
    });
});