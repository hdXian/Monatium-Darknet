// 특성 입력 란 필드 구성
document.addEventListener('DOMContentLoaded', function() {
    // 어사이드 1단계
    setupDynamicInputFields({
        containerId: 'aside1-attributes-container',
        addButtonClass: 'btn-add-aside1-attribute',
        removeButtonClass: 'btn-remove-aside1-attribute',
        inputNamePrefix: 'asideLv1Attributes'
    });

    // 어사이드 2단계
    setupDynamicInputFields({
        containerId: 'aside2-attributes-container',
        addButtonClass: 'btn-add-aside2-attribute',
        removeButtonClass: 'btn-remove-aside2-attribute',
        inputNamePrefix: 'asideLv2Attributes'
    });

    // 어사이드 3단계
    setupDynamicInputFields({
        containerId: 'aside3-attributes-container',
        addButtonClass: 'btn-add-aside3-attribute',
        removeButtonClass: 'btn-remove-aside3-attribute',
        inputNamePrefix: 'asideLv3Attributes'
    });

});

// 어사이드 여부에 따라 입력 칸 노출, 숨김
document.addEventListener('DOMContentLoaded', function () {
    const radios = document.getElementsByName('enableAside');
    const asideSection = document.getElementById('asideSection');

    if (radios && asideSection) {
        // 초기 상태 설정
        const isEnabled = Array.from(radios).find(radio => radio.checked && radio.value === 'true');
        if (isEnabled) {
            asideSection.classList.add('show'); // Bootstrap의 show 클래스 추가
            asideSection.style.display = 'block';
        } else {
            asideSection.classList.remove('show'); // Bootstrap의 show 클래스 제거
            asideSection.style.display = 'none';
        }


        // 라디오 버튼 변경 시 표시/숨기기
        radios.forEach(radio => {
            radio.addEventListener('change', function () {
                if (radio.value === 'true') {
                    asideSection.classList.add('show'); // show 클래스 추가
                    asideSection.style.display = 'block';
                } else {
                    asideSection.classList.remove('show'); // show 클래스 제거
                    asideSection.style.display = 'none';
                }
            });
        });
    }
});

