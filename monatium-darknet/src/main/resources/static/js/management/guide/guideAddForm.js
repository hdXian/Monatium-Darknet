
// CSRF 토큰 읽기
const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

// BlotFormatter 모듈 등록
Quill.register('modules/blotFormatter', QuillBlotFormatter2.default);

// Quill 초기화
const quill = new Quill('#editor', {
	theme: 'snow',
	modules: {
		toolbar: {
		    container: [
		        [{ 'header': [1, 2, false] }],
                ['bold', 'italic', 'underline'],
                [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                ['image', 'link']
		    ],
		    handlers: {
            	image: function () {
            		console.log('이미지 업로드 시작');
            		const input = document.createElement('input');
            		input.type = 'file';
            		input.accept = 'image/*';
            		input.onchange = async function () {
            			const file = input.files[0];
            			if (!file) return;

            			const formData = new FormData();
            			formData.append('file', file);

            			try {
            				const response = await fetch('/api/images/upload/tmp', {
            					method: 'POST',
            					headers: {
            						[csrfHeader]: csrfToken
            					},
            					body: formData
            				});

            				if (response.ok) {
            					const imageUrl = await response.text(); // 서버에서 반환한 imageUrl 읽기
            					const range = quill.getSelection();
            					quill.insertEmbed(range.index, 'image', imageUrl);
            				} else {
            					alert('이미지 업로드 실패');
            				}

            			} catch (error) {
            				alert('이미지 업로드 중 오류가 발생했습니다.');
            				console.error(error);
            			}
            		};

            		input.click();
            	}
            }
		},
		blotFormatter: {
		    // 옵션 설정 (선택 사항)
		    align: {
			    allowAligning: true,
			    alignments: ['left', 'center', 'right']
		    },
		    resize: {
			    allowResizing: true,
			    handleStyles: {
			        backgroundColor: 'blue',
			        borderRadius: '50%',
			        width: '12px',
			        height: '12px'
			    },
			    useRelativeSize: true,
			    allowResizeModeChange: true,
			    imageOversizeProtection: true,
			    minimumWidthPx: 50
		    },
		    delete: {
			    allowKeyboardDelete: true
		    },
		    image: {
			    allowAltTitleEdit: true,
			    registerImageTitleBlot: true,
			    allowCompressor: true
		    },
		    overlay: {
			    style: {
			        border: '2px dashed red'
			    },
			    sizeInfoStyle: {
			        backgroundColor: 'rgba(0, 0, 0, 0.7)',
			        color: 'white',
			        borderRadius: '4px',
			        padding: '5px'
			    }
		    },
		    toolbar: {
			    toolbarSize: 'small',
			    toolbarButtonSvg: {
			        alignLeft: '<svg>...</svg>',
			        alignCenter: '<svg>...</svg>',
			        alignRight: '<svg>...</svg>',
			        altTitle: '<svg>...</svg>',
			        compress: '<svg>...</svg>'
			    }
		    }
		}
    }
});


//const quill = new Quill('#editor', {
//    theme: 'snow',
//    placeholder: '가이드 내용을 입력하세요...',
//    modules: {
//        toolbar: {
//            container: [
//                [{ 'header': [1, 2, false] }],
//                ['bold', 'italic', 'underline'],
//                ['link', 'image'],
//                [{ 'list': 'ordered' }, { 'list': 'bullet' }]
//            ],
//            handlers: {
//                image: function () {
//                    console.log('이미지 업로드 시작');
//                    const input = document.createElement('input');
//                    input.type = 'file';
//                    input.accept = 'image/*';
//                    input.onchange = async function () {
//                        const file = input.files[0];
//                        if (!file) return;
//
//                        const formData = new FormData();
//                        formData.append('file', file);
//
//                        try {
//                            const response = await fetch('/api/images/upload/tmp', {
//                                method: 'POST',
//                                headers: {
//                                    [csrfHeader]: csrfToken
//                                },
//                                body: formData
//                            });
//
//                            if (response.ok) {
//                                const imageUrl = await response.text(); // 서버에서 반환한 imageUrl 읽기
//                                const range = quill.getSelection();
//                                quill.insertEmbed(range.index, 'image', imageUrl);
//                            } else {
//                                alert('이미지 업로드 실패');
//                            }
//
//                        } catch (error) {
//                            alert('이미지 업로드 중 오류가 발생했습니다.');
//                            console.error(error);
//                        }
//                    };
//
//                    input.click();
//                }
//            }
//        }
//    }
//});

// Quill 에디터에 서버에서 받은 HTML 본문을 안전하게 삽입
window.onload = function() {
    const content = document.querySelector('#content').value;
    quill.clipboard.dangerouslyPasteHTML(content);
};

// 취소 확인 로직
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

        form.submit();
    }
}

// "취소" 버튼 이벤트 추가
document.querySelector('#btnCancel').addEventListener('click', function () {
    confirmCancel(this);
});


// "임시 저장" 버튼 이벤트 추가
document.querySelector('#btnSave').addEventListener('click', function () {
    // 추가적인 저장 처리 로직
    document.querySelector('#content').value = quill.root.innerHTML;
});


// "작성하기" 버튼 이벤트 추가
document.querySelector('#btnComplete').addEventListener('click', function () {
    // 버튼이 속한 폼을 가져옴
    const form = this.closest('form');

    if (confirm('가이드를 등록하시겠습니까?')) {
        // 추가적인 작성 처리 로직
        document.querySelector('#content').value = quill.root.innerHTML;

        // action 파라미터를 명시적으로 설정
        const hiddenField = document.createElement('input');
        hiddenField.type = 'hidden';
        hiddenField.name = 'action';
        hiddenField.value = 'complete';
        form.appendChild(hiddenField);

        form.submit(); // 폼 제출
    }

});

