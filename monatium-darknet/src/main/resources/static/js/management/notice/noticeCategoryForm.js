function confirmCancel(button) {
    // 버튼이 속한 폼을 가져옴
    const form = button.closest('form');

    // 사용자 확인 후 폼 제출
    if (confirm("취소하시겠습니까? 현재 작성된 데이터는 저장되지 않습니다.")) {
        // action 파라미터를 명시적으로 설정
        const hiddenField = document.createElement('input');
        hiddenField.type = 'hidden';
        hiddenField.name = 'action';
        hiddenField.value = 'cancel';
        form.appendChild(hiddenField);

        form.submit(); // 폼을 명시적으로 제출
    }
}

function confirmComplete(button) {
    // 버튼이 속한 폼을 가져옴
    const form = button.closest('form');

    // 사용자 확인 후 폼 제출
    if (confirm("공지사항 카테고리를 저장하시겠습니까?")) {
        // action 파라미터를 명시적으로 설정
        const hiddenField = document.createElement('input');
        hiddenField.type = 'hidden';
        hiddenField.name = 'action';
        hiddenField.value = 'complete';
        form.appendChild(hiddenField);

        form.submit(); // 폼을 명시적으로 제출
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const cancelButton = document.getElementById("cancelBtn");
    const completeButton = document.getElementById("completeBtn");

    if (cancelButton) {
        cancelButton.addEventListener("click", function () {
            confirmCancel(this);
        });
    }

    if (completeButton) {
        completeButton.addEventListener("click", function () {
            confirmComplete(this);
        });
    }

});

