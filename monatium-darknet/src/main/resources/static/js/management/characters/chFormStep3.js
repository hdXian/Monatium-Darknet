// 특성 입력 란 필드 구성
document.addEventListener('DOMContentLoaded', function() {
    // 기본 공격 특성
    setupDynamicInputFields({
        containerId: 'normalAttack-attributes-container',
        addButtonClass: 'btn-add-normal-attribute',
        removeButtonClass: 'btn-remove-normal-attribute',
        inputNamePrefix: 'normalAttributes'
    });

    // 강화 공격 특성
    setupDynamicInputFields({
        containerId: 'enhancedAttack-attributes-container',
        addButtonClass: 'btn-add-enhanced-attribute',
        removeButtonClass: 'btn-remove-enhanced-attribute',
        inputNamePrefix: 'enhancedAttributes'
    });

    // 저학년 스킬 특성
    setupDynamicInputFields({
        containerId: 'lowSkill-attributes-container',
        addButtonClass: 'btn-add-lowSkill-attribute',
        removeButtonClass: 'btn-remove-lowSkill-attribute',
        inputNamePrefix: 'lowSkillAttributes'
    });

    // 고학년 스킬 특성
    setupDynamicInputFields({
        containerId: 'highSkill-attributes-container',
        addButtonClass: 'btn-add-highSkill-attribute',
        removeButtonClass: 'btn-remove-highSkill-attribute',
        inputNamePrefix: 'highSkillAttributes'
    });
});

// 강화 공격 여부에 따라 입력 칸 노출, 숨김
document.addEventListener('DOMContentLoaded', function () {
    const radios = document.getElementsByName('enableEnhancedAttack');
    const section = document.getElementById('enhancedAttackSection');

    if (section && radios.length > 0) {
        // 초기 상태 설정
        section.style.display = radios[0].checked ? 'block' : 'none';

        // 라디오 버튼 변경 시 표시/숨기기
        radios.forEach(radio => {
            radio.addEventListener('change', function () {
                section.style.display = radio.value === 'true' && radio.checked ? 'block' : 'none';
            });
        });
    }
});

document.addEventListener("DOMContentLoaded", function () {
    const cooldownInput = document.getElementById("highSkillCooldown");
    if (cooldownInput) {
        cooldownInput.addEventListener("input", function () {
            this.value = this.value.replace(/[^0-9]/g, '').replace(/^0+/, '');
        });
    }
});


