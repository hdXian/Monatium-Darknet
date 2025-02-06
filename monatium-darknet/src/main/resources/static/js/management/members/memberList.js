
document.addEventListener("DOMContentLoaded", function () {
    // 활성화/비활성화 버튼 클릭 이벤트
    document.querySelectorAll(".member-status-btn").forEach(button => {
        button.addEventListener("click", function () {
            const memberId = this.getAttribute("data-id");
            const nickName = this.getAttribute("data-nickName");

            const isActive = button.getAttribute('data-status') === 'ACTIVE';

            const confirmMessage = isActive ? `${nickName} 사용자를 비활성화하시겠습니까?` : `${nickName} 사용자를 활성화하시겠습니까?`;

            if (!confirm(confirmMessage)) {
                return;
            }

            const requestUrl = isActive ? `/management/members/deactivate/${memberId}`
                                        : `/management/members/activate/${memberId}`;

            sendRequest(requestUrl);
        });
    });

    // 사용자 로그아웃 클릭 이벤트
    document.querySelectorAll(".member-logout-btn").forEach(button => {
        button.addEventListener("click", function () {
            const memberId = this.getAttribute("data-id");
            const nickName = this.getAttribute("data-nickName");

            const confirmMessage = `${nickName} 사용자를 로그아웃 처리하시겠습니까?`;

            if (!confirm(confirmMessage)) {
                return;
            }

            const requestUrl = `/management/members/disconnect/${memberId}`;

            sendRequest(requestUrl);
        });
    });

    // AJAX 요청 보내는 함수
    function sendRequest(url) {

        // CSRF 토큰 읽기
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        fetch(url, {
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken
            }
        }).then(response => {
            if (response.ok) {
                location.reload(); // 성공 시 페이지 새로고침
            } else {
                alert("요청 처리 중 오류가 발생했습니다.");
            }
        }).catch(error => console.error("Error:", error));
    }

});

