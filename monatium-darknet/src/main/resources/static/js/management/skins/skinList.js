
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

// 스킨 검색 기능
document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchInput');
    const searchButton = document.getElementById('searchButton');
    const skinItems = document.querySelectorAll('.skin-item');

    // 검색 로직
    const filterSkins = () => {
        const query = searchInput.value.toLowerCase().trim();

        skinItems.forEach(item => {
            const title = item.querySelector('.card-title span').textContent.toLowerCase();

            // 검색어가 제목에 포함되면 표시, 그렇지 않으면 숨김
            if (title.includes(query)) {
                item.style.display = ''; // 기본 표시
            } else {
                item.style.display = 'none'; // 숨김
            }
        });
    };

    // 검색 버튼 클릭 시 검색 실행
    searchButton.addEventListener('click', filterSkins);

    // 검색창에서 Enter 키로 검색 실행
    searchInput.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            event.preventDefault(); // 기본 Enter 동작(폼 제출) 방지
            filterSkins();
        }
    });
});

