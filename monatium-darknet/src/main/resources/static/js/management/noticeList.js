document.addEventListener('DOMContentLoaded', function () {
    // 🔥 모든 공개/비공개 전환 버튼에 클릭 이벤트 추가
    const toggleButtons = document.querySelectorAll('.btn-toggle-status');

    toggleButtons.forEach(button => {
        button.addEventListener('click', function () {
            const noticeId = this.getAttribute('data-id'); // 공지사항의 ID
            const currentStatus = this.getAttribute('data-status'); // 현재 상태 (PUBLIC / PRIVATE)
            const isPublic = currentStatus === 'PUBLIC';

            const confirmMessage = isPublic
                ? '이 공지사항을 비공개로 전환하시겠습니까?'
                : '이 공지사항을 공개로 전환하시겠습니까?';

            const userConfirmed = confirm(confirmMessage);
            if (!userConfirmed) {
                return; // 사용자가 취소를 누르면 상태 전환을 중단합니다.
            }

            // 상태 전환 로직 호출
            updateStatus(noticeId);
        });
    });

    // 🔥 모든 삭제 버튼에 클릭 이벤트 추가
    const deleteButtons = document.querySelectorAll('.btn-delete');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function () {
            const noticeId = this.getAttribute('data-id'); // 삭제할 공지사항의 ID
            const noticeTitle = this.getAttribute('data-title'); // 공지사항 제목

            if (!confirm(`공지사항을 삭제하시겠습니까?\n제목: ${noticeTitle}`)) {
                return;
            }

            fetch(`/management/notices/${noticeId}`, {
                method: 'DELETE'
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

// 🔥 상태 전환 함수 (변경 사항 없음)
function updateStatus(noticeId) {
    const button = document.querySelector(`button[data-id="${noticeId}"]`);
    if (!button) {
        console.error(`버튼을 찾을 수 없습니다. Notice ID: ${noticeId}`);
        return;
    }

    const currentStatus = button.getAttribute('data-status');
    const newStatus = (currentStatus === 'PUBLIC') ? 'PRIVATE' : 'PUBLIC';

    // 서버에 상태 변경 요청
    fetch(`/management/notices/${noticeId}/update-status?t=${newStatus}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // 버튼 텍스트 업데이트
            button.textContent = (data.newStatus === 'PUBLIC') ? '공개 중' : '비공개';

            // 버튼 클래스 업데이트
            button.classList.remove('btn-success', 'btn-secondary');
            button.classList.add(data.newStatus === 'PUBLIC' ? 'btn-success' : 'btn-secondary');

            // 버튼의 data-status 속성 업데이트
            button.setAttribute('data-status', data.newStatus);
        } else {
            alert('상태 변경에 실패했습니다.');
        }
    })
    .catch(error => console.error('Error:', error));
}
