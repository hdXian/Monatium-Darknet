
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

//document.addEventListener("DOMContentLoaded", function() {
//    document.getElementById("btnCloseRemote").addEventListener("click", closeRemoteControl);
//});


// 캐릭터 섹션 이미지 변경 이벤트 등록
document.addEventListener("DOMContentLoaded", function () {
    const characterImg = document.getElementById("characterImg");
    const characterText = document.getElementById("characterText");
    const prevBtn = document.getElementById("prevCharacter");
    const nextBtn = document.getElementById("nextCharacter");

    // 캐릭터 정보 (기본 이미지, 텍스트 이미지, 클릭 시 변경할 이미지)
    const characters = [
        { img: "/imgs/main/ch01.gif", text: "/imgs/main/ch01-text.png", clickImg: "/imgs/main/ch01-on.gif" },
        { img: "/imgs/main/ch02.gif", text: "/imgs/main/ch02-text.png", clickImg: "/imgs/main/ch02-on.gif" },
        { img: "/imgs/main/ch03.gif", text: "/imgs/main/ch03-text.png", clickImg: "/imgs/main/ch03-on.gif" },
        { img: "/imgs/main/ch04.gif", text: "/imgs/main/ch04-text.png", clickImg: "/imgs/main/ch04-on.gif" },
        { img: "/imgs/main/ch05.gif", text: "/imgs/main/ch05-text.png", clickImg: "/imgs/main/ch05-on.gif" },
        { img: "/imgs/main/ch06.gif", text: "/imgs/main/ch06-text.png", clickImg: "/imgs/main/ch06-on.gif" },
        { img: "/imgs/main/ch07.gif", text: "/imgs/main/ch07-text.png", clickImg: "/imgs/main/ch07-on.gif" }
    ];

    let currentIndex = 0;
    let isMouseDown = false;

    function changeCharacter(index) {
        // 이미지 변경 전 fade-out 효과
        characterImg.classList.add("fade-out");
        characterText.classList.add("fade-out");

        setTimeout(() => {
            // 새로운 이미지로 변경
            characterImg.src = characters[index].img;
            characterText.src = characters[index].text;

            // fade-in 효과
            characterImg.classList.remove("fade-out");
            characterText.classList.remove("fade-out");
        }, 200);
    }

    prevBtn.addEventListener("click", function () {
        currentIndex = (currentIndex - 1 + characters.length) % characters.length;
        changeCharacter(currentIndex);
    });

    nextBtn.addEventListener("click", function () {
        currentIndex = (currentIndex + 1) % characters.length;
        changeCharacter(currentIndex);
    });

    // 클릭 시 변경할 이미지 적용
    characterImg.addEventListener("mousedown", function () {
        isMouseDown = true;
        characterImg.src = characters[currentIndex].clickImg;
    });

    characterImg.addEventListener("mouseup", function () {
        isMouseDown = false;
        characterImg.src = characters[currentIndex].img;
    });

    characterImg.addEventListener("mouseleave", function () {
        if (isMouseDown) {
            isMouseDown = false;
            characterImg.src = characters[currentIndex].img;
        }
    });

    // 터치 이벤트도 추가 (모바일 대응)
    characterImg.addEventListener("touchstart", function () {
        isMouseDown = true;
        characterImg.src = characters[currentIndex].clickImg;
    });

    characterImg.addEventListener("touchend", function () {
        isMouseDown = false;
        characterImg.src = characters[currentIndex].img;
    });
});


//document.addEventListener("DOMContentLoaded", function () {
//    const characterImg = document.getElementById("characterImg");
//    const characterText = document.getElementById("characterText");
//    const prevBtn = document.getElementById("prevCharacter");
//    const nextBtn = document.getElementById("nextCharacter");
//
//    const characters = [
//        { img: "/imgs/main/ch01.gif", text: "/imgs/main/ch01-text.png" },
//        { img: "/imgs/main/ch02.gif", text: "/imgs/main/ch02-text.png" },
//        { img: "/imgs/main/ch03.gif", text: "/imgs/main/ch03-text.png" },
//        { img: "/imgs/main/ch04.gif", text: "/imgs/main/ch04-text.png" },
//        { img: "/imgs/main/ch05.gif", text: "/imgs/main/ch05-text.png" },
//        { img: "/imgs/main/ch06.gif", text: "/imgs/main/ch06-text.png" },
//        { img: "/imgs/main/ch07.gif", text: "/imgs/main/ch07-text.png" }
//    ];
//    let currentIndex = 0;
//
//    function changeCharacter(index) {
//        // 이미지 변경 전 fade-out 효과
//        characterImg.classList.add("fade-out");
//        characterText.classList.add("fade-out");
//
//        setTimeout(() => {
//            // 이미지 변경
//            characterImg.src = characters[index].img;
//            characterText.src = characters[index].text;
//
//            // fade-in 효과
//            characterImg.classList.remove("fade-out");
//            characterText.classList.remove("fade-out");
//        }, 200); // 0.1초 후 변경
//    }
//
//    prevBtn.addEventListener("click", function () {
//        currentIndex = (currentIndex - 1 + characters.length) % characters.length;
//        changeCharacter(currentIndex);
//    });
//
//    nextBtn.addEventListener("click", function () {
//        currentIndex = (currentIndex + 1) % characters.length;
//        changeCharacter(currentIndex);
//    });
//});

// 리모컨 섹션
document.addEventListener("DOMContentLoaded", function () {
    const quickMenuToggle = document.getElementById("quickMenuToggle");
    const quickMenu = document.getElementById("quickMenu");
    const btnCloseQuickMenu = document.getElementById("btnCloseQuickMenu");

    // 플로팅 버튼 클릭 시 메뉴 표시
    quickMenuToggle.addEventListener("click", function () {
        quickMenu.style.display = quickMenu.style.display === "flex" ? "none" : "flex";
    });

    // 닫기 버튼 클릭 시 메뉴 숨김
    btnCloseQuickMenu.addEventListener("click", function () {
        quickMenu.style.display = "none";
    });
});

// 이미지 드래그 방지 설정
document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("img").forEach(img => {
        img.setAttribute("draggable", "false");

        // 드래그 이벤트 방지
        img.addEventListener("dragstart", function (event) {
            event.preventDefault();
        });

        // 텍스트 선택 방지 (일부 이미지 클릭 시 선택되는 문제 해결)
        img.addEventListener("selectstart", function (event) {
            event.preventDefault();
        });
    });
});


