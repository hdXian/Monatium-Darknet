
document.addEventListener('DOMContentLoaded', function() {
    const skinRow = document.querySelector('.skin-row');
    let isDown = false;
    let startX;
    let scrollLeft;

    skinRow.addEventListener('mousedown', (e) => {
        isDown = true;
        skinRow.classList.add('active');
        startX = e.pageX - skinRow.offsetLeft;
        scrollLeft = skinRow.scrollLeft;
    });

    skinRow.addEventListener('mouseleave', () => {
        isDown = false;
        skinRow.classList.remove('active');
    });

    skinRow.addEventListener('mouseup', () => {
        isDown = false;
        skinRow.classList.remove('active');
    });

    skinRow.addEventListener('mousemove', (e) => {
        if (!isDown) return;
        e.preventDefault();
        const x = e.pageX - skinRow.offsetLeft;
        const walk = (x - startX) * 2; // *2는 드래그 속도를 조절
        skinRow.scrollLeft = scrollLeft - walk;
    });
});
