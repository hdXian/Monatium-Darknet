
document.addEventListener('click', function(event) {
    if (event.target.matches('.toggle-character-status')) {
        const button = event.target;
        const cardId = button.getAttribute('data-character-id');
        const characterName = button.getAttribute('data-character-name');

        const isActive = button.getAttribute('data-status') === 'ACTIVE';

        const confirmMessage = isActive ? `${characterName} 캐릭터를 비활성화하시겠습니까?` : `${characterName} 캐릭터를 활성화하시겠습니까?`;

        if (!confirm(confirmMessage)) {
            return;
        }

        const requestUrl = isActive ? `/management/characters/disable/${cardId}` : `/management/characters/activate/${cardId}`

        // 요청한 상태를 미리 계산


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
