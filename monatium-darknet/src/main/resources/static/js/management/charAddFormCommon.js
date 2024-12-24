
// 각 그룹 내용 접기/펼치기
document.addEventListener('DOMContentLoaded', function() {
    const titles = document.querySelectorAll('.info-title');

    titles.forEach(title => {
        const icon = title.querySelector('.toggle-icon');
        const targetId = title.getAttribute('data-bs-target');
        const targetElement = document.querySelector(targetId);

        // 열림 이벤트 (⏴ → ⏷)
        targetElement.addEventListener('shown.bs.collapse', function() {
            icon.classList.remove('fa-angle-left');
            icon.classList.add('fa-angle-down');
        });

        // 닫힘 이벤트 (⏷ → ⏴)
        targetElement.addEventListener('hidden.bs.collapse', function() {
            icon.classList.remove('fa-angle-down');
            icon.classList.add('fa-angle-left');
        });
    });
});


/**
 * 특성 입력 필드 추가/삭제 기능을 추가하는 범용 함수
 * @param {string} containerId - 특성 컨테이너의 ID
 * @param {string} addButtonClass - 추가 버튼의 클래스명
 * @param {string} removeButtonClass - 삭제 버튼의 클래스명
 * @param {string} inputNamePrefix - 동적 추가 필드의 name 속성에 들어갈 접두사
 */
function setupDynamicInputFields({ containerId, addButtonClass, removeButtonClass, inputNamePrefix }) {
    const container = document.getElementById(containerId);

    if (!container) {
        console.warn(`Container with ID "${containerId}" not found.`);
        return;
    }

    // 부모 요소에 이벤트 위임 추가
    container.addEventListener('click', function(event) {
        const target = event.target;

        // + 버튼이 클릭되었을 때
        if (target.classList.contains(addButtonClass)) {
            const currentIndex = container.querySelectorAll('.attribute-input-container').length;

            // 새로운 특성 입력 필드 추가
            const newTraitInput = document.createElement('div');
            newTraitInput.classList.add('d-flex', 'align-items-center', 'mb-2', 'attribute-input-container');

            newTraitInput.innerHTML = `
                <input type="text" name="${inputNamePrefix}[${currentIndex}].attrName"
                       class="form-control me-2 attribute-name" placeholder="특성 이름">
                <input type="text" name="${inputNamePrefix}[${currentIndex}].attrValue"
                       class="form-control me-2 attribute-value" placeholder="특성 수치">
                <button type="button" class="btn btn-success ${addButtonClass}">+</button>
                <button type="button" class="btn btn-danger ${removeButtonClass}">-</button>
            `;

            container.appendChild(newTraitInput);
            updateFieldNames(container, inputNamePrefix); // 인덱스를 다시 정렬
        }

        // - 버튼이 클릭되었을 때
        if (target.classList.contains(removeButtonClass)) {
            const allTraitContainers = container.querySelectorAll('.attribute-input-container');

            // **방식 1 적용: 삭제 전에 검사**
            if (allTraitContainers.length > 1) {
                const traitInputContainer = target.closest('.attribute-input-container');
                traitInputContainer.remove();
                updateFieldNames(container, inputNamePrefix); // 인덱스를 다시 정렬
            } else {
                alert('최소 하나의 특성 입력 필드는 남겨두어야 합니다.');
            }
        }
    });

    /**
     * 모든 특성 입력 필드의 name 속성을 다시 정렬하는 함수
     * @param {HTMLElement} container - 특성 컨테이너 엘리먼트
     * @param {string} inputNamePrefix - 동적 추가 필드의 name 속성에 들어갈 접두사
     */
    function updateFieldNames(container, inputNamePrefix) {
        const traitContainers = container.querySelectorAll('.attribute-input-container');

        // **방식 2 적용: 모든 입력 필드 삭제 후 보장**
        if (traitContainers.length === 0) {
            const newTraitInput = document.createElement('div');
            newTraitInput.classList.add('d-flex', 'align-items-center', 'mb-2', 'attribute-input-container');

            newTraitInput.innerHTML = `
                <input type="text" name="${inputNamePrefix}[0].attrName"
                       class="form-control me-2 attribute-name" placeholder="특성 이름">
                <input type="text" name="${inputNamePrefix}[0].attrValue"
                       class="form-control me-2 attribute-value" placeholder="특성 수치">
                <button type="button" class="btn btn-success ${addButtonClass}">+</button>
                <button type="button" class="btn btn-danger ${removeButtonClass}">-</button>
            `;

            container.appendChild(newTraitInput);
        }

        // 필드의 name 속성을 다시 정렬
        traitContainers.forEach((container, index) => {
            const nameInput = container.querySelector('.attribute-name');
            const valueInput = container.querySelector('.attribute-value');

            nameInput.name = `${inputNamePrefix}[${index}].attrName`;
            valueInput.name = `${inputNamePrefix}[${index}].attrValue`;
        });
    }
}

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


// 이미지 미리보기 기능
// TODO - 이미지 기본 썸네일 src 서버 리소스로 대체 필요
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.image-upload-wrapper img').forEach(img => {
        img.addEventListener('click', function() {
            const input = this.nextElementSibling;
            input.click();
        });
    });
});

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

function confirmCancel() {
    if (confirm("정말 취소하시겠습니까? 현재 작성된 데이터는 저장되지 않습니다.")) {
        // 취소를 확인한 경우, 실제 취소 작업 수행
//        document.querySelector('form').action = '/management/characters/new/cancel';
        document.querySelector('form').submit();
    }
}
