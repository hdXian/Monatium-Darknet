
// 좋아하는 것 입력 란
document.addEventListener('DOMContentLoaded', function() {
    const likesContainer = document.getElementById('likes-container');

    // 부모 요소인 likesContainer에 이벤트 위임
    likesContainer.addEventListener('click', function(event) {
        const target = event.target;

        // + 버튼이 클릭되었을 때
        if (target.classList.contains('btn-add-like')) {
            const currentIndex = likesContainer.children.length;

            // 새로운 입력 필드 추가
            const newLikeInput = document.createElement('div');
            newLikeInput.classList.add('d-flex', 'mb-2', 'like-input-container');

            newLikeInput.innerHTML = `
                <input type="text" name="favorites[${currentIndex}]" class="form-control me-2" placeholder="좋아하는 것을 입력하세요">
                <button type="button" class="btn btn-success btn-add-like">+</button>
                <button type="button" class="btn btn-danger btn-remove-like ms-2">-</button>
            `;

            likesContainer.appendChild(newLikeInput);
        }

        // - 버튼이 클릭되었을 때
        if (target.classList.contains('btn-remove-like')) {
            const likeInputContainer = target.closest('.like-input-container');
            likeInputContainer.remove();

            // 삭제 후, 입력 필드가 하나도 없으면 기본 입력 필드 추가
            updateLikeFieldNames();
        }
    });

    // 모든 좋아하는 것 입력 필드의 name 속성을 다시 정렬하는 함수
    function updateLikeFieldNames() {
        const likeInputs = document.querySelectorAll('#likes-container input[name^="favorites"]');

        // 삭제 후, 입력 필드가 하나도 없으면 기본 입력 필드 추가
        if (likeInputs.length === 0) {
            const newLikeInput = document.createElement('div');
            newLikeInput.classList.add('d-flex', 'mb-2', 'like-input-container');
            newLikeInput.innerHTML = `
                <input type="text" name="favorites[0]" class="form-control me-2" placeholder="좋아하는 것을 입력하세요">
                <button type="button" class="btn btn-success btn-add-like">+</button>
                <button type="button" class="btn btn-danger btn-remove-like ms-2">-</button>
            `;
            likesContainer.appendChild(newLikeInput);
        }

        // name 속성 인덱스 다시 정렬
        likeInputs.forEach((input, index) => {
            input.name = `favorites[${index}]`;
        });
    }
});
