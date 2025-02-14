document.addEventListener('DOMContentLoaded', function () {
    // CSRF 토큰 읽기
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // 모든 삭제 버튼에 클릭 이벤트 추가
    const deleteButtons = document.querySelectorAll('.btn-delete');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function () {
            const noticeId = this.getAttribute('data-id'); // 삭제할 공지사항의 ID
            const noticeTitle = this.getAttribute('data-title'); // 공지사항 제목

            if (!confirm(`공지사항을 삭제하시겠습니까?\n제목: ${noticeTitle}`)) {
                return;
            }

            fetch(`/management/guides/${noticeId}`, {
                method: 'DELETE',
                headers: {
                    [csrfHeader]: csrfToken
                }
            })
            .then(response => {
                if (response.ok) {
                    alert('삭제가 완료되었습니다.');
                    location.reload(); // 페이지 새로고침
                    // this.closest('tr').remove(); // 또는 테이블에서 해당 행 삭제
                } else {
                    alert('삭제에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('삭제 중 오류 발생:', error);
                alert('삭제에 실패했습니다.');
            });
        });
    });
});
