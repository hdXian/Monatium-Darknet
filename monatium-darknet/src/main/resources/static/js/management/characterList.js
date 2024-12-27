
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', function (event) {
            const isActive = this.querySelector('button').classList.contains('btn-success');
            const confirmMessage = isActive
                ? this.dataset.confirmActive
                : this.dataset.confirmInactive;

            if (!confirm(confirmMessage)) {
                event.preventDefault();
            }
        });
    });
});
