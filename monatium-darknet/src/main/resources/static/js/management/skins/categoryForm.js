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
