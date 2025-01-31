document.addEventListener("DOMContentLoaded", function() {
    document.getElementById("languageSelect").addEventListener("change", function() {
        this.form.submit();
    });
});
