const carousel = document.querySelector('#noticeCarousel');
let isDragging = false, startPos = 0, currentPos = 0;

// 드래그와 터치 이벤트 등록
carousel.addEventListener('mousedown', startDrag);
carousel.addEventListener('touchstart', startDrag);

carousel.addEventListener('mousemove', drag);
carousel.addEventListener('touchmove', drag);

carousel.addEventListener('mouseup', endDrag);
carousel.addEventListener('touchend', endDrag);
carousel.addEventListener('mouseleave', endDrag);

function startDrag(event) {
    isDragging = true;
    startPos = getPositionX(event); // 시작 위치 저장
    carousel.classList.add('dragging'); // CSS 효과 적용
}

function drag(event) {
    if (!isDragging) return; // 드래그 상태가 아니면 리턴
    const currentX = getPositionX(event);
    const diff = currentX - startPos;

    // 사용자가 충분히 움직이면 슬라이드 이동
    if (diff > 100) {
        bootstrap.Carousel.getInstance(carousel).prev();
        isDragging = false; // 이동 후 드래그 종료
    } else if (diff < -100) {
        bootstrap.Carousel.getInstance(carousel).next();
        isDragging = false; // 이동 후 드래그 종료
    }
}

function endDrag() {
    isDragging = false; // 드래그 종료
    carousel.classList.remove('dragging'); // CSS 효과 제거
}

function getPositionX(event) {
    // 마우스 또는 터치 위치 반환
    return event.type.includes('mouse') ? event.pageX : event.touches[0].clientX;
}
