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

