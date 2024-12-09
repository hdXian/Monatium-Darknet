
function updateStatus(noticeId) {
// 버튼 찾기
const button = document.querySelector(`button[data-id="${noticeId}"]`);
if (!button) {
    console.error(`버튼을 찾을 수 없습니다. Notice ID: ${noticeId}`);
    return;
}

// 현재 상태 가져오기
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

