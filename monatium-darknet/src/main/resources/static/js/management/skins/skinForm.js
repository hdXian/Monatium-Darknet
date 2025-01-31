
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

function confirmCancel(button) {
    // 버튼이 속한 폼을 가져옴
    const form = button.closest('form');

    // 사용자 확인 후 폼 제출
    if (confirm("취소하시겠습니까? 현재 작성된 데이터는 저장되지 않습니다.")) {
        // action 파라미터를 명시적으로 설정
        const hiddenField = document.createElement('input');
        hiddenField.type = 'hidden';
        hiddenField.name = 'action';
        hiddenField.value = 'cancel';
        form.appendChild(hiddenField);

        form.submit(); // 폼을 명시적으로 제출
    }
}

function confirmComplete(button) {
    // 버튼이 속한 폼을 가져옴
    const form = button.closest('form');

    // 사용자 확인 후 폼 제출
    if (confirm("사복을 저장하시겠습니까?")) {
        // action 파라미터를 명시적으로 설정
        const hiddenField = document.createElement('input');
        hiddenField.type = 'hidden';
        hiddenField.name = 'action';
        hiddenField.value = 'complete';
        form.appendChild(hiddenField);

        form.submit(); // 폼을 명시적으로 제출
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


document.addEventListener("DOMContentLoaded", function () {
    const imageInputs = document.querySelectorAll(".img-input");
    const addCategoryButton = document.getElementById("addCategoryBtn");
    const categoryContainer = document.getElementById("categoryContainer"); // 버튼들이 추가될 부모 요소

    const cancelButton = document.getElementById("cancelBtn");
    const completeButton = document.getElementById("completeBtn");

    imageInputs.forEach(input => {
        input.addEventListener("change", function() {
            previewImage(this);
        });
    });

    addCategoryButton.addEventListener("click", function() {
        addCategory();
    });

    // skinsContainer에 이벤트 위임 (이벤트 핸들러를 컨테이너에 등록)
    categoryContainer.addEventListener("click", function(event) {
        if (event.target.classList.contains("remove-category-btn")) {
            removeCategory(event.target);
        }
    });

    // 취소 버튼 클릭 시 confirmCancel() 실행
    cancelButton.addEventListener("click", function(event) {
        confirmCancel(event.target);
    });

    // 완료 버튼 클릭 시 confirmComplete() 실행
    completeButton.addEventListener("click", function(event) {
        confirmComplete(event.target);
    });

});
