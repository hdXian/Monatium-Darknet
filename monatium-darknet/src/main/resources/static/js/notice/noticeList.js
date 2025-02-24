
document.addEventListener("DOMContentLoaded", function () {
    const categorySelect = document.getElementById("categorySelect");

    if (categorySelect) {
        categorySelect.addEventListener("change", function () {
            const lang = categorySelect.dataset.lang || "en"; // data-lang 속성에서 언어 정보 가져오기
            const categoryId = categorySelect.value;
            const url = categoryId ? `/${lang}/notices?category=${categoryId}` : `/${lang}/notices`;
            window.location.href = url;
        });
    }
});
