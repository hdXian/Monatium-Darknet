// HTML 요소 선택
const portraits = document.querySelectorAll('.character-portrait');
const mainCharacterImg = document.getElementById('main-character-img');
const characterInfos = document.querySelectorAll('.character-info');

// 초상화 클릭 이벤트 등록
portraits.forEach(portrait => {
    portrait.addEventListener('click', () => {
        const targetId = portrait.dataset.target; // 타겟 정보 ID
        const newImgSrc = portrait.dataset.img; // 메인 캐릭터 이미지 경로

        // 모든 캐릭터 정보를 숨김 처리
        characterInfos.forEach(info => info.classList.add('d-none'));

        // 선택된 캐릭터 정보만 표시
        const targetInfo = document.getElementById(targetId);
        targetInfo.classList.remove('d-none');

        // 메인 캐릭터 이미지 변경
        mainCharacterImg.src = newImgSrc;

    });
});
