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

// 깡, 맷집, 재주 입력 란
function syncInput(sliderId, numberId) {
    const slider = document.getElementById(sliderId);
    const number = document.getElementById(numberId);

    // 슬라이더 변경 시 숫자 입력 필드에 동기화
    slider.addEventListener('input', function() {
        number.value = slider.value;
    });

    // 숫자 입력 필드 변경 시 슬라이더에 동기화
    number.addEventListener('input', function() {
        if (number.value < 1) number.value = 1;
        if (number.value > 7) number.value = 7;
        slider.value = number.value;
    });
}

// 각 특성의 슬라이더와 숫자 입력 필드 동기화
syncInput('strength-slider', 'strength-number');
syncInput('durability-slider', 'durability-number');
syncInput('trick-slider', 'trick-number');


// 특성 입력 란
document.addEventListener('DOMContentLoaded', function() {
    const subgroups = document.querySelectorAll('.info-subgroup');

    subgroups.forEach(function(subgroup) {
        subgroup.addEventListener('click', function(e) {
            const container = subgroup.querySelector('[id$="-traits-container"]');

            if (e.target.classList.contains('btn-add-trait')) {
                const newTraitInput = document.createElement('div');
                newTraitInput.classList.add('d-flex', 'align-items-center', 'mb-2', 'trait-input-container');
                newTraitInput.innerHTML = `
                    <input type="text" name="attribute-name" class="form-control me-2" placeholder="특성 이름">
                    <input type="text" name="attribute-value" class="form-control me-2" placeholder="특성 수치">
                    <button type="button" class="btn btn-success btn-add-trait">+</button>
                    <button type="button" class="btn btn-danger btn-remove-trait">-</button>
                `;
                container.appendChild(newTraitInput);
            }

            if (e.target.classList.contains('btn-remove-trait')) {
                const traitContainer = e.target.closest('.trait-input-container');
                const allTraitContainers = container.querySelectorAll('.trait-input-container');
                if (allTraitContainers.length > 1) {
                    traitContainer.remove();
                }
            }
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
