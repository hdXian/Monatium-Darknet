
document.addEventListener("DOMContentLoaded", function() {
    const selectElement = document.getElementById("languageSelect");

    // 마우스를 드롭다운 위에 올리면 자동으로 펼쳐지도록 focus 적용
    selectElement.addEventListener("mouseenter", function() {
        this.focus(); // 드롭다운 자동으로 펼침
    });

    // 마우스가 벗어나면 드롭다운 닫기
    selectElement.addEventListener("mouseleave", function() {
        this.blur(); // 드롭다운 자동 닫기
    });

    // 옵션 선택 시 폼 자동 제출
    selectElement.addEventListener("change", function() {
        this.form.submit();
    });

    // 각 언어 선택 항목(option) hover 효과 추가
    const observer = new MutationObserver(() => {
        const options = selectElement.querySelectorAll("option");
        options.forEach(option => {
            option.addEventListener("mouseenter", function() {
                this.style.backgroundColor = "#5B9BD5"; // hover 시 배경색 변경
                this.style.color = "#FFFFFF";
            });

            option.addEventListener("mouseleave", function() {
                this.style.backgroundColor = ""; // 원래 색상으로 복귀
                this.style.color = "";
            });
        });
    });

    // 드롭다운이 열릴 때마다 옵션 감지하여 hover 스타일 적용
    observer.observe(selectElement, { childList: true });
});


