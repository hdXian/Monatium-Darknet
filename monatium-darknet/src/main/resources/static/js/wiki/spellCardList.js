
function filterItems() {
    const searchName = document.getElementById('searchName').value.toLowerCase();

    // 체크박스로 선택된 등급을 가져오기
    const selectedGrades = [];
    if (document.getElementById('legendaryFilter').checked) selectedGrades.push('legendary');
    if (document.getElementById('rareFilter').checked) selectedGrades.push('rare');
    if (document.getElementById('advancedFilter').checked) selectedGrades.push('advanced');
    if (document.getElementById('normalFilter').checked) selectedGrades.push('normal');

    const items = document.querySelectorAll('.spell-item');

    items.forEach(item => {
        const itemGrade = item.getAttribute('data-grade');
        const itemName = item.getAttribute('data-name').toLowerCase();

        const gradeMatch = selectedGrades.length === 0 || selectedGrades.includes(itemGrade);
        const nameMatch = itemName.includes(searchName);

        if (gradeMatch && nameMatch) {
            item.style.display = 'block';
        } else {
            item.style.display = 'none';
        }
    });
}

function handleEnter(event) {
    if (event.keyCode === 13) {
        filterItems();
    }
}
