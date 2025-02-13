//
//document.addEventListener("DOMContentLoaded", function() {
//    const selectElement = document.getElementById("languageSelect");
//
//    // 마우스를 드롭다운 위에 올리면 자동으로 펼쳐지도록 focus 적용
//    selectElement.addEventListener("mouseenter", function() {
//        this.focus(); // 드롭다운 자동으로 펼침
//    });
//
//    // 마우스가 벗어나면 드롭다운 닫기
//    selectElement.addEventListener("mouseleave", function() {
//        this.blur(); // 드롭다운 자동 닫기
//    });
//
//    // 옵션 선택 시 폼 자동 제출
//    selectElement.addEventListener("change", function() {
//        this.form.submit();
//    });
//
//});

document.addEventListener("DOMContentLoaded", function() {
    const dropdown = document.getElementById("customLanguageSelect");
    const selectedLanguage = document.getElementById("selectedLanguage");
    const optionsList = document.getElementById("customOptions");
    const languageInput = document.getElementById("languageInput");
    const languageForm = document.getElementById("languageForm");

    // 드롭다운 열고 닫기
    dropdown.addEventListener("click", function() {
        this.classList.toggle("open");
    });

    // 옵션 선택 시 적용
    optionsList.querySelectorAll("li").forEach(option => {
        option.addEventListener("click", function() {
            const selectedLang = this.getAttribute("data-lang");
            selectedLanguage.textContent = this.textContent;
            languageInput.value = selectedLang; // hidden input 업데이트

            languageForm.submit(); // 폼 제출
        });
    });

    // 드롭다운 외부 클릭 시 닫기
    document.addEventListener("click", function(event) {
        if (!dropdown.contains(event.target)) {
            dropdown.classList.remove("open");
        }
    });

});

