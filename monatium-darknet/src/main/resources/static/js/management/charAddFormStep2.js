
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
syncInput('aggressive-slider', 'aggressive-number');
syncInput('endurance-slider', 'endurance-number');
syncInput('trick-slider', 'trick-number');
