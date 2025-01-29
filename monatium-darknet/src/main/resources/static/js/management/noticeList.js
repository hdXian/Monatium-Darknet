document.addEventListener('DOMContentLoaded', function () {
    // 모든 공개/비공개 전환 버튼에 클릭 이벤트 추가
    const toggleButtons = document.querySelectorAll('.btn-toggle-status');

    // CSRF 토큰 읽기
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    toggleButtons.forEach(button => {
        button.addEventListener('click', function () {
            const noticeId = this.getAttribute('data-id'); // 공지사항의 ID
            const currentStatus = this.getAttribute('data-status'); // 현재 상태 (PUBLIC / PRIVATE)
            const title = this.getAttribute('data-title');
            const isPublic = currentStatus === 'PUBLIC';

            const confirmMessage = isPublic
                ? `이 공지사항을 비공개로 전환하시겠습니까?\n제목: "${title}"`
                : `이 공지사항을 공개로 전환하시겠습니까?\n제목: "${title}"`;

            const userConfirmed = confirm(confirmMessage);
            if (!userConfirmed) {
                return; // 사용자가 취소를 누르면 상태 전환을 중단
            }

            const requestUrl = isPublic ? `/management/notices/unpublish/${noticeId}`
                                        : `/management/notices/publish/${noticeId}`;

            fetch(requestUrl, {
                method: 'POST',
                headers: {
                    [csrfHeader]: csrfToken
                }
            })
            .then(response => {
                if (response.ok) {
                    // 성공적으로 처리된 경우 상태 변경
                    const newStatus = isPublic ? 'PRIVATE' : 'PUBLIC';
                    //
                    button.setAttribute('data-status', newStatus);

                    // 클래스 업데이트
                    if (newStatus === 'PUBLIC') {
                        button.classList.add('btn-success');
                        button.classList.remove('btn-secondary');
                    } else {
                        button.classList.add('btn-secondary');
                        button.classList.remove('btn-success');
                    }

                    // 버튼 텍스트 업데이트
                    button.textContent = (newStatus === 'PUBLIC') ? '공개 중' : '비공개 중';

                } else {
                    throw new Error('서버 오류가 발생했습니다.');
                }
            })
            .catch(error => {
                alert('상태 변경에 실패했습니다: ' + error.message);
            });

            // 상태 전환 로직 호출

        });
    });

    // 모든 삭제 버튼에 클릭 이벤트 추가
    const deleteButtons = document.querySelectorAll('.btn-delete');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function () {
            const noticeId = this.getAttribute('data-id'); // 삭제할 공지사항의 ID
            const noticeTitle = this.getAttribute('data-title'); // 공지사항 제목

            if (!confirm(`공지사항을 삭제하시겠습니까?\n제목: ${noticeTitle}`)) {
                return;
            }

            fetch(`/management/notices/${noticeId}`, {
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
