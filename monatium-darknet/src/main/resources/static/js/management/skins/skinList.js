
// 활성화/비활성화 버튼 클릭 이벤트
document.addEventListener('click', function(event) {
    if (event.target.matches('.toggle-skin-status')) {
        const button = event.target;
        const skinId = button.getAttribute('data-id');
        const skinName = button.getAttribute('data-name');

        const isActive = button.getAttribute('data-status') === 'ACTIVE';

        const confirmMessage = isActive ? `${skinName} 사복을 비활성화하시겠습니까?` : `${skinName} 사복을 활성화하시겠습니까?`;

        if (!confirm(confirmMessage)) {
            return;
        }

        const requestUrl = isActive ? `/management/skins/disable/${skinId}` : `/management/skins/activate/${skinId}`

        // AJAX 요청
        fetch(requestUrl, { method: 'POST' })
            .then(response => {
                if (response.ok) {

                    // 성공적으로 처리된 경우 상태 변경
                    const newStatus = isActive ? 'DISABLED' : 'ACTIVE';
                    button.setAttribute('data-status', newStatus);

                    // 클래스 업데이트
                    if (newStatus === 'ACTIVE') {
                        button.classList.add('btn-success');
                        button.classList.remove('btn-secondary');
                    } else {
                        button.classList.add('btn-secondary');
                        button.classList.remove('btn-success');
                    }

                    // 버튼 텍스트 업데이트
                    button.textContent = (newStatus === 'ACTIVE') ? '활성화 중' : '비활성화 중';

                } else {
                    throw new Error('서버 오류가 발생했습니다.');
                }
            })
            .catch(error => {
                alert('상태 변경에 실패했습니다: ' + error.message);
            });
    }
});
