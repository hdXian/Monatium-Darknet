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
        const inputElement = item.querySelector('input');
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
