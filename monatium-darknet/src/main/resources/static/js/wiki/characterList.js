function filterCharacters() {
    const selectedClasses = [];

    // 성격 필터
    const personalityClasses = [];
    if (document.getElementById('personalityPure').checked) personalityClasses.push('pure');
    if (document.getElementById('personalityCool').checked) personalityClasses.push('cool');
    if (document.getElementById('personalityMadness').checked) personalityClasses.push('madness');
    if (document.getElementById('personalityLively').checked) personalityClasses.push('lively');
    if (document.getElementById('personalityDepressed').checked) personalityClasses.push('depressed');

    // 역할군 필터
    const roleClasses = [];
    if (document.getElementById('roleDealer').checked) roleClasses.push('dealer');
    if (document.getElementById('roleTanker').checked) roleClasses.push('tanker');
    if (document.getElementById('roleSupporter').checked) roleClasses.push('supporter');

    // 배치 열 필터
    const positionClasses = [];
    if (document.getElementById('positionFront').checked) positionClasses.push('front');
    if (document.getElementById('positionMiddle').checked) positionClasses.push('middle');
    if (document.getElementById('positionBack').checked) positionClasses.push('back');

    // 종족 필터
    const raceClasses = [];
    if (document.getElementById('raceFairy').checked) raceClasses.push('fairy');
    if (document.getElementById('raceBeast').checked) raceClasses.push('beast');
    if (document.getElementById('raceElf').checked) raceClasses.push('elf');
    if (document.getElementById('raceSpirit').checked) raceClasses.push('spirit');
    if (document.getElementById('raceGhost').checked) raceClasses.push('ghost');
    if (document.getElementById('raceDragon').checked) raceClasses.push('dragon');
    if (document.getElementById('raceWitch').checked) raceClasses.push('witch');

    // 공격 타입 필터
    const attackClasses = [];
    if (document.getElementById('attackPhysical').checked) attackClasses.push('physical');
    if (document.getElementById('attackMagical').checked) attackClasses.push('magical');

    const characters = document.querySelectorAll('.character-item');

    // 표시할 캐릭터를 선택
    characters.forEach(character => {
        let personality_match = false;
        let role_match = false;
        let position_match = false;
        let race_match = false;
        let attack_match = false;

        // 성격에 필터링 걸려있으면
        personality_match = checkCondition(personalityClasses, character);
        role_match = checkCondition(roleClasses, character);
        position_match = checkCondition(positionClasses, character);
        race_match = checkCondition(raceClasses, character);
        attack_match = checkCondition(attackClasses, character);

        shouldShow = (personality_match && role_match && position_match && race_match && attack_match);

        if (shouldShow) {
            character.style.display = 'block';
        } else {
            character.style.display = 'none';
        }
    });
}

function checkCondition(classes, character) {
    if (classes.length > 0) {

        const hasClass = classes.some(cond => {
            return character.classList.contains(cond);
        });

        return hasClass;
    }
    else { // 조건 없으면 true 리턴
        return true;
    }

}

function resetFilters() {
    // 모든 체크박스 해제
    const checkboxes = document.querySelectorAll('.form-check-input');
    checkboxes.forEach(checkbox => {
        checkbox.checked = false;
    });

    // 필터링을 초기화하고 모든 캐릭터를 다시 보여줌
    filterCharacters();
}

document.addEventListener("DOMContentLoaded", function() {
    document.getElementById("chSearchBtn").addEventListener("click", filterCharacters);
    document.getElementById("chSearchClearBtn").addEventListener("click", resetFilters);
});
