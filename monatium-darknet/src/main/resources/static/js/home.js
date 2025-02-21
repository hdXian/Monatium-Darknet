
AOS.init();

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



function closeRemoteControl() {
    const remoteControl = document.querySelector('.remote-control');
    remoteControl.style.display = 'none'; // 리모컨 섹션 숨김
}

document.addEventListener("DOMContentLoaded", function() {
    document.getElementById("btnCloseRemote").addEventListener("click", closeRemoteControl);
});


// 캐릭터 섹션 이미지 변경 이벤트 등록
document.addEventListener("DOMContentLoaded", function () {
    const characterImg = document.getElementById("characterImg");
    const characterText = document.getElementById("characterText");
    const prevBtn = document.getElementById("prevCharacter");
    const nextBtn = document.getElementById("nextCharacter");

    const characters = [
        { img: "/imgs/main/ch01.gif", text: "/imgs/main/ch01-text.png" },
        { img: "/imgs/main/ch02.gif", text: "/imgs/main/ch02-text.png" },
        { img: "/imgs/main/ch03.gif", text: "/imgs/main/ch03-text.png" },
        { img: "/imgs/main/ch04.gif", text: "/imgs/main/ch04-text.png" },
        { img: "/imgs/main/ch05.gif", text: "/imgs/main/ch05-text.png" },
        { img: "/imgs/main/ch06.gif", text: "/imgs/main/ch06-text.png" },
        { img: "/imgs/main/ch07.gif", text: "/imgs/main/ch07-text.png" }
    ];
    let currentIndex = 0;

    function changeCharacter(index) {
        // 이미지 변경 전 fade-out 효과
        characterImg.classList.add("fade-out");
        characterText.classList.add("fade-out");

        setTimeout(() => {
            // 이미지 변경
            characterImg.src = characters[index].img;
            characterText.src = characters[index].text;

            // fade-in 효과
            characterImg.classList.remove("fade-out");
            characterText.classList.remove("fade-out");
        }, 200); // 0.1초 후 변경
    }

    prevBtn.addEventListener("click", function () {
        currentIndex = (currentIndex - 1 + characters.length) % characters.length;
        changeCharacter(currentIndex);
    });

    nextBtn.addEventListener("click", function () {
        currentIndex = (currentIndex + 1) % characters.length;
        changeCharacter(currentIndex);
    });
});



