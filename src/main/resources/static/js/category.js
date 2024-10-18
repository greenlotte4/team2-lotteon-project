function open1(menuId) {

    // 클릭된 메뉴 열기/닫기
    const selectedMenu = document.getElementById(menuId);
    if (selectedMenu.classList.contains('open')) {
        selectedMenu.classList.remove('open');
        selectedMenu.style.transition = 'none';  // 닫을 때는 transition을 제거
        selectedMenu.style.maxHeight = '0';
        selectedMenu.style.padding = '0';
    } else {
        selectedMenu.classList.add('open');
        // 열릴 때 transition을 다시 추가해서 부드럽게 열리도록 함
        setTimeout(() => {
            selectedMenu.style.transition = 'max-height 0.5s ease-out, padding 0.5s ease-out';
            selectedMenu.style.maxHeight = 'none';  // 부드럽게 열기
            selectedMenu.style.padding = '10px 0';
        }, 10);  // 약간의 지연을 줘서 transition이 적용되도록
    }}

function addThd(e) {
    console.log(e);
    const thirdCategory = document.getElementById(e);

    // 이미 input이 있으면 더 이상 생성되지 않게
    if (thirdCategory.querySelector('input')) {
        alert("카테고리 입력창이 이미 있습니다.");
        return;
    }

    // 입력 필드와 추가 버튼을 감싸는 div 생성
    const inputContainer = document.createElement('div');
    const inputField = document.createElement('input');
    const addButton = document.createElement('button');

    inputField.type = 'text';
    inputField.placeholder = '새 카테고리 이름 입력';
    addButton.textContent = '카테고리 추가';

    // input과 버튼을 div에 추가
    inputContainer.appendChild(inputField);
    inputContainer.appendChild(addButton);

    // 새로운 카테고리 추가 영역을 기존 버튼 앞에 삽입
    thirdCategory.insertBefore(inputContainer, thirdCategory.querySelector('.addThd'));

    // 추가 버튼 클릭 이벤트
    addButton.addEventListener('click', function() {
        const categoryName = inputField.value.trim();

        // 카테고리 이름이 비어있지 않은 경우에만 추가
        if (categoryName) {
            const newCategory = document.createElement('div');
            newCategory.innerHTML = `
                - ${categoryName} <button class="delBtn">삭제</button>
            `;

            // 새로운 카테고리를 추가 버튼 위에 삽입
            thirdCategory.insertBefore(newCategory, thirdCategory.querySelector('.addThd'));

            // 입력 필드 제거
            inputContainer.remove();

            // 삭제 버튼 클릭 이벤트
            newCategory.querySelector('.delBtn').addEventListener('click', function() {
                if (confirm("삭제하시겠습니까?")) {
                    newCategory.remove();
                }
            });
        } else {
            alert("카테고리 이름을 입력하세요.");
        }
    });
}

let num = 1;
let numMain = 1;
function addSub(e) {
    const subCate = document.getElementById(e); // 서브 카테고리가 추가될 영역

    // 이미 input이 있으면 더 이상 생성되지 않게
    if (subCate.querySelector('input')) {
        alert("서브 카테고리 입력창이 이미 있습니다.");
        return;
    }

    // 입력 필드와 추가 버튼을 감싸는 div 생성
    const inputContainer = document.createElement('div');
    const inputField = document.createElement('input');
    const addButton = document.createElement('button');

    inputField.type = 'text';
    inputField.placeholder = '새 서브 카테고리 이름 입력';
    addButton.textContent = '서브 카테고리 추가';

    // input과 버튼을 div에 추가
    inputContainer.appendChild(inputField);
    inputContainer.appendChild(addButton);

    // 새로운 서브 카테고리 추가 영역을 subCate에 삽입
    subCate.insertAdjacentElement('beforeend', inputContainer);

    // 추가 버튼 클릭 이벤트
    addButton.addEventListener('click', function() {
        const subCategoryName = inputField.value.trim();

        // 서브 카테고리 이름이 비어있지 않은 경우에만 추가
        if (subCategoryName) {
            // 새로운 서브 카테고리 생성
            const newSubCategory = document.createElement('li');
            newSubCategory.innerHTML = `
                ${subCategoryName} <button class="delBtn parent">삭제</button>
            `;

            // li 요소 다음에 추가할 div 요소 생성
            const thdCateDiv = document.createElement('div');
            thdCateDiv.classList.add('thdCate');

            // div 요소의 innerHTML 설정
            thdCateDiv.innerHTML = `
                <button class="addThd">
                    <div class="icon">+</div>
                    <div>카테고리 추가하기</div>
                </button>
            `;

            // div 요소에 id 동적 설정

            thdCateDiv.id = 'third' + num;
            num++;

            const addSub = document.getElementsByClassName('.addSub')[0];

            // 필요한 위치에 요소를 추가 (li 뒤에 div 추가)
            const subAdd = document.getElementsByClassName('addSub')[0];
            subCate.insertBefore(newSubCategory, subAdd);
            newSubCategory.insertAdjacentElement('afterend', thdCateDiv);

            newSubCategory.onclick = function() {
                open1(thdCateDiv.id); // 클릭할 때만 함수 호출
            };
            console.log(thdCateDiv.id);


            // 새로운 서브 카테고리를 subCate 영역에 추가

            // 입력 필드 제거
            inputContainer.remove();

            // 삭제 버튼 클릭 이벤트
            newSubCategory.querySelector('.delBtn').addEventListener('click', function() {
                if (confirm("삭제하시겠습니까?")) {
                    newSubCategory.remove();
                    thdCateDiv.remove();
                }
            });

            // 3차 카테고리 추가 버튼 이벤트
            thdCateDiv.querySelector('.addThd').addEventListener('click', function() {
                console.log(thdCateDiv.id);
                addThd(thdCateDiv.id);
            });

        } else {
            alert("서브 카테고리 이름을 입력하세요.");
        }
    });
}

document.querySelector('.addMain').addEventListener('click', function() {
    const mainCate = document.getElementById('mainCate'); // 서브 카테고리가 추가될 영역

    // 이미 input이 있으면 더 이상 생성되지 않게
    if (mainCate.querySelector('input')) {
        alert("메인 카테고리 입력창이 이미 있습니다.");
        return;
    }

    // 입력 필드와 추가 버튼을 감싸는 div 생성
    const inputContainer = document.createElement('div');
    const inputField = document.createElement('input');
    const addButton = document.createElement('button');

    inputField.type = 'text';
    inputField.placeholder = '새 메인 카테고리 이름 입력';
    addButton.textContent = '메인 카테고리 추가';

    // input과 버튼을 div에 추가
    inputContainer.appendChild(inputField);
    inputContainer.appendChild(addButton);

    // 새로운 서브 카테고리 추가 영역을 subCate에 삽입
    mainCate.insertAdjacentElement('beforeend', inputContainer);

    // 추가 버튼 클릭 이벤트
    addButton.addEventListener('click', function() {
        const mainCategoryName = inputField.value.trim();



        // 메인 카테고리 이름이 비어있지 않은 경우에만 추가
        if (mainCategoryName) {
            // 새로운 메인 카테고리 생성
            const newMainCategory = document.createElement('li');
            newMainCategory.innerHTML = `
                ${mainCategoryName} <button class="delBtn parent">삭제</button>
            `;

            // li 요소 다음에 추가할 div 요소 생성
            const subCateDiv = document.createElement('div');
            subCateDiv.classList.add('subCate');

            // div 요소의 innerHTML 설정
            subCateDiv.innerHTML = `
                <button class="addSub">
                    <div class="icon">+</div>
                    <div>서브 카테고리 추가하기</div>
                </button>
            `;

            // div 요소에 id 동적 설정

            subCateDiv.id = 'sub' + numMain;
            numMain++;

            // 필요한 위치에 요소를 추가 (li 뒤에 div 추가)
            const mainAdd = document.getElementsByClassName('addMain')[0];
            mainCate.insertBefore(newMainCategory, mainAdd);
            newMainCategory.insertAdjacentElement('afterend', subCateDiv);

            newMainCategory.onclick = function() {
                open1(subCateDiv.id); // 클릭할 때만 함수 호출
            };
            console.log(subCateDiv.id);


            // 새로운 서브 카테고리를 subCate 영역에 추가

            // 입력 필드 제거
            inputContainer.remove();

            // 삭제 버튼 클릭 이벤트
            newMainCategory.querySelector('.delBtn').addEventListener('click', function() {
                if (confirm("삭제하시겠습니까?")) {
                    newMainCategory.remove();
                    subCateDiv.remove();
                }
            });

            // 2차 카테고리 추가 버튼 이벤트
            subCateDiv.querySelector('.addSub').addEventListener('click', function() {
                console.log(subCateDiv.id);
                addSub(subCateDiv.id);
            });

        } else {
            alert("서브 카테고리 이름을 입력하세요.");
        }
    });
});

function setupDeleteButtons() {
    const deleteButtons = document.querySelectorAll('.delBtn'); // 모든 삭제 버튼 선택

    deleteButtons.forEach(button => {

        button.addEventListener('click', function () {
            if(confirm("삭제하시겠습니까?")) {
                const categoryItem = button.parentElement; // 버튼의 부모 div 선택
                categoryItem.remove(); // 해당 div 삭제
            }});
    });
}
document.addEventListener('DOMContentLoaded', setupDeleteButtons);