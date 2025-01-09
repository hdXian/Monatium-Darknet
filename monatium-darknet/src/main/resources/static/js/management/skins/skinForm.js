
function previewImage(input) {
    const file = input.files[0];
    const preview = input.previousElementSibling; // 바로 이전의 img 요소
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
}

// 이미지 미리보기 기능
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.image-upload-wrapper img').forEach(img => {
        img.addEventListener('click', function() {
            const input = this.nextElementSibling;
            input.click();
        });
    });
});

// textarea 크기 자동 조절
document.addEventListener('DOMContentLoaded', function() {
    const autoResizeTextareas = document.querySelectorAll('textarea.auto-resize');

    autoResizeTextareas.forEach(textarea => {
        textarea.addEventListener('input', function() {
            this.style.height = 'auto'; // 높이 초기화 (초기화하지 않으면 height가 누적됨)
            this.style.height = this.scrollHeight + 'px'; // 스크롤 높이에 맞춰 자동으로 높이 설정
        });
    });
});

// 카테고리 추가, 삭제
function addCategory() {
    const container = document.getElementById('categoryContainer');
    const template = document.getElementById('categoryItemTemplate');

    // 템플릿 복사
    const newCategoryItem = template.content.cloneNode(true);

    // 동적으로 인덱스 설정
    const selectElement = newCategoryItem.querySelector('select');
    const index = container.children.length;
    selectElement.name = `categoryIds[${index}]`;

    container.appendChild(newCategoryItem);
}

function removeCategory(button) {
    const container = document.getElementById('categoryContainer');

    // 현재 카테고리 필드 수 확인
    const categoryItems = container.getElementsByClassName('category-item');
    if (categoryItems.length > 1) {
        button.parentElement.remove();

        // 인덱스 재정렬
        reorderCategoryIndices();
    } else {
        alert("최소 하나의 카테고리는 남겨두어야 합니다.");
    }

}

function reorderCategoryIndices() {
    const container = document.getElementById('categoryContainer');
    const categoryItems = container.getElementsByClassName('category-item');

    Array.from(categoryItems).forEach((item, index) => {
        const selectElement = item.querySelector('select');
        selectElement.name = `categoryIds[${index}]`;
    });
}
