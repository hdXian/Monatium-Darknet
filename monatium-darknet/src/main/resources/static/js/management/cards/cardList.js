
function filterItems() {
    const searchName = document.getElementById('searchName').value.toLowerCase();

    // 체크박스로 선택된 등급을 가져오기
    const selectedGrades = [];
    if (document.getElementById('legendaryFilter').checked) selectedGrades.push('legendary');
    if (document.getElementById('rareFilter').checked) selectedGrades.push('rare');
    if (document.getElementById('advancedFilter').checked) selectedGrades.push('advanced');
    if (document.getElementById('normalFilter').checked) selectedGrades.push('normal');

    const items = document.querySelectorAll('.spell-item');

    items.forEach(item => {
        const itemGrade = item.getAttribute('data-grade');
        const itemName = item.getAttribute('data-name').toLowerCase();

        const gradeMatch = selectedGrades.length === 0 || selectedGrades.includes(itemGrade);
        const nameMatch = itemName.includes(searchName);

        if (gradeMatch && nameMatch) {
            item.style.display = 'block';
        } else {
            item.style.display = 'none';
        }
    });
}

function handleEnter(event) {
    if (event.keyCode === 13) {
        filterItems();
    }
}

document.addEventListener('click', function(event) {
    if (event.target.matches('.toggle-card-status')) {
        const button = event.target;
        const cardId = button.getAttribute('data-card-id');
        const cardName = button.getAttribute('data-card-name');

        const isActive = button.getAttribute('data-status') === 'ACTIVE';

        const confirmMessage = isActive ? `${cardName} 카드를 비활성화하시겠습니까?` : `${cardName} 카드를 활성화하시겠습니까?`;

        if (!confirm(confirmMessage)) {
            return;
        }

        const requestUrl = isActive ? `/management/cards/disable/${cardId}` : `/management/cards/activate/${cardId}`

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



