// 스킨 추가
function addSkin() {
    const container = document.getElementById('skinsContainer');
    const template = document.getElementById('skinItemTemplate');

    // 템플릿 복사
    const newSkinItem = template.content.cloneNode(true);

    // 동적으로 인덱스 설정
    const inputElement = newSkinItem.querySelector('select');
    const index = container.children.length;
    inputElement.name = `skinIds[${index}]`;

    container.appendChild(newSkinItem);
}

// 스킨 삭제
function removeSkin(button) {
    const container = document.getElementById('skinsContainer');

    button.parentElement.remove();

    // 인덱스 재정렬
    reorderSkinIndices();
}

// 인덱스 재정렬
function reorderSkinIndices() {
    const container = document.getElementById('skinsContainer');
    const skinItems = container.getElementsByClassName('skin-item');

    Array.from(skinItems).forEach((item, index) => {
        const inputElement = item.querySelector('select');
        inputElement.name = `skins[${index}]`;
    });
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
    if (confirm("사복 카테고리를 저장하시겠습니까?")) {
        // action 파라미터를 명시적으로 설정
        const hiddenField = document.createElement('input');
        hiddenField.type = 'hidden';
        hiddenField.name = 'action';
        hiddenField.value = 'complete';
        form.appendChild(hiddenField);

        form.submit(); // 폼을 명시적으로 제출
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const addSkinButton = document.getElementById("addSkinBtn");
    const skinsContainer = document.getElementById("skinsContainer"); // 버튼들이 추가될 부모 요소

    const cancelButton = document.getElementById("cancelBtn");
    const completeButton = document.getElementById("completeBtn");

    addSkinButton.addEventListener("click", function() {
        addSkin();
    });

    // skinsContainer에 이벤트 위임 (이벤트 핸들러를 컨테이너에 등록)
    skinsContainer.addEventListener("click", function(event) {
        if (event.target.classList.contains("remove-skin-btn")) {
            removeSkin(event.target);
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
