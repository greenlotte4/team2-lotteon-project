let productAll = new FormData();
let submitData = [];
let totalStock = 0;
let isSingleOption = false;
let optionCount = 0;
window.onload = function (){
    const prodInsert = document.getElementsByClassName('submit-btn')[0];
    prodInsert.addEventListener('click', (e) => {

        const prodCate = new FormData(document.getElementById('prodCategory'));
        const productInfo = new FormData(document.getElementById('productInfo'));
        const detail = new FormData(document.getElementById('detail'));

        e.preventDefault();
            for (const [key, value] of prodCate.entries()) {
                productAll.append(`postProdCateMapperDTO.${key}`, value)
            }
            for (const [key, value] of productInfo.entries()) {
                productAll.append(`postProductDTO.${key}`, value)
            }
            for (const [key, value] of detail.entries()) {
                productAll.append(`postProdDetailDTO.${key}`, value)
            }
        for (const [key, value] of productAll.entries()) {
            console.log(key, value)
        }
        if(document.getElementById('prodStock').value <= 0){
            alert('옵션에서 물품 수량을 지정해주세요!');
            return;
        }else {
            fetch('/admin/prod/info', {
                method: 'POST',
                body: productAll
            })
                .then(resp => resp.json())
                .then(data => {
                    if (data.success > 0) {
                        alert('결제 정보가 등록되었습니다!');
                        for (let i = 0; i < submitData.length; i++) {
                            submitData[i].productId = String(data.success);
                        }
                        console.log("44555" + JSON.stringify(submitData));
                        fetch("/admin/prod/option", {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'  // 서버가 JSON 데이터를 받을 수 있도록 명시
                            },
                            body: JSON.stringify(submitData)
                        }).then(resp => resp.json())
                            .then(data => {
                                console.log(data);
                            }).catch(err => {
                            console.log(err);
                        })
                        window.location.href = "/admin/prod/products";
                    } else {
                        alert('결제 정보 등록에 실패하였습니다');
                    }
                })
                .catch(err => {
                    console.log(err);
                    productAll = new FormData();
                });
        }
    });
}

function addOption(event) {
    // 클릭된 버튼의 부모 td를 찾습니다.
    const td = event.target.parentElement;

    // 새로운 입력 세트를 추가할 td 생성
    const newInputs = document.createElement('div'); // div로 여러 input 묶음

    const row = event.target.closest('tr'); // 클릭된 버튼이 있는 tr을 찾습니다.
    const inputs = row.querySelectorAll('input'); // 해당 tr 내의 모든 인풋을 찾습니다.

    const option1 = inputs[0].value; // 첫 번째 인풋의 값을 가져옵니다.
    const option2 = inputs[2].value; // 첫 번째 인풋의 값을 가져옵니다.
    const option3 = inputs[4].value; // 첫 번째 인풋의 값을 가져옵니다.

    console.log("option값들은?"+option1, option2, option3);

    if (!option1.trim()) {
        alert('옵션명을 입력해주세요.')
        console.log(event)
        return;
    }

    if(option2 === ''){
        newInputs.innerHTML = `
            <div class="option_inputs">
                <input type="hidden" class="option_input option_input_name" value="${option1}">
                <input type="text" class="option_input option_input_value" placeholder="옵션값1">
                <input type="text" class="option_input option_input_price" placeholder="추가금액">
                <input type="text" class="option_input option_input_stock" placeholder="재고량">
                <button class="btnDelete" type="button" onclick="deleteOption2(event)">삭제</button>
            </div>
        `;
    }else if(option3 === ''){
        newInputs.innerHTML = `
            <div class="option_inputs">
                <input type="hidden" class="option_input option_input_name" value="${option1}">
                <input type="text" class="option_input option_input_value" placeholder="옵션값1">
                <input type="hidden" class="option_input option_input_name2" value="${option2}">
                <input type="text" class="option_input option_input_value2" placeholder="옵션값2">
                <input type="text" class="option_input option_input_price" placeholder="추가금액">
                <input type="text" class="option_input option_input_stock" placeholder="재고량">
                <button class="btnDelete" type="button" onclick="deleteOption2(event)">삭제</button>
            </div>
        `;
    }else {
        // 새로운 입력 세트 추가
        newInputs.innerHTML = `
            <div class="option_inputs">
                <input type="hidden" class="option_input option_input_name" value="${option1}">
                <input type="text" class="option_input option_input_value" placeholder="옵션값1">
                <input type="hidden" class="option_input option_input_name2" value="${option2}">
                <input type="text" class="option_input option_input_value2" placeholder="옵션값2">
                <input type="hidden" class="option_input option_input_name3" value="${option3}">
                <input type="text" class="option_input option_input_value3" placeholder="옵션값3">
                <input type="text" class="option_input option_input_price" placeholder="추가금액">
                <input type="text" class="option_input option_input_stock" placeholder="재고량">
                <button class="btnDelete" type="button" onclick="deleteOption2(event)">삭제</button>
            </div>
        `;
    }
    // 기존 td 내에 새로운 입력 세트 추가
    td.appendChild(newInputs);
}

let cnt = 0;

function addParent(event) {
    const options = document.querySelector('.tb4'); // tbody를 선택하는 대신 tb4로 바로 작업
    const limit3 = document.querySelector('.limit3');

    // 새로운 옵션 행(tr) 추가
    const newTr = document.createElement('tr');
    newTr.innerHTML = `
            <th>옵션</th>
            <td class="optionTD">
                <div class="option_inputs">
                    <input type="text" class="option_input option_input_name" placeholder="옵션명1">
                    <input type="text" class="option_input option_input_value" placeholder="옵션값1">
                    <input type="text" class="option_input option_input_name2" placeholder="옵션명2">
                    <input type="text" class="option_input option_input_value2" placeholder="옵션값2">
                    <input type="text" class="option_input option_input_name3" placeholder="옵션명3">
                    <input type="text" class="option_input option_input_value3" placeholder="옵션값3">
                    <input type="text" class="option_input option_input_price" placeholder="추가금액">
                    <input type="text" class="option_input option_input_stock" placeholder="재고량">
                </div>
                <button class="btnOption" type="button" onclick="addOption(event)">세부옵션추가</button>
                <button class="btnDelete" type="button" onclick="deleteOption(event)">삭제</button>
            </td>
        `;

    // 테이블에 새로운 행 추가
    options.appendChild(newTr);

    const currentOptionsCount = options.querySelectorAll('tr').length;

    // 옵션이 3개 이상이면 추가를 막음
    if (currentOptionsCount >= 3) {
        limit3.innerText = '옵션은 3개까지만 가능합니다';
        document.getElementById('check').style.display = 'none';
    }

}

function confirmOption(event) {
    const optionName = document.getElementsByClassName('option_input option_input_name')[0];

    if(!isSingleOption){
        if (!optionName.value.trim()) {
            alert('옵션 이름을 입력해 주세요.');
            return; // 함수 실행 중단
        }
    }

    let optionName123 = null;

    document.querySelectorAll('.option_inputs').forEach(v => {
        const getValue = (selector) => {
            const element = v.querySelector(selector);
            return element ? element.value.trim() || null : null;
        };

        let name = getValue('.option_input_name');
        let value = getValue('.option_input_value');
        let name2 = getValue('.option_input_name2');
        let value2 = getValue('.option_input_value2');
        let name3 = getValue('.option_input_name3');
        let value3 = getValue('.option_input_value3');
        let price = getValue('.option_input_price');
        let stock = getValue('.option_input_stock');


        if(name == null){
            name='옵션없음';
        }

        let jsonData = {
            optionName: name,
            optionValue: value,
            optionName2: name2,
            optionValue2: value2,
            optionName3: name3,
            optionValue3: value3,
            additionalPrice: price,
            stock: stock,
            productId: null
        };
        totalStock += parseInt(jsonData.stock);
        optionName123 = jsonData.optionName;
        submitData.push(jsonData);
    });
    console.log(submitData)
    console.log("totalStock"+totalStock);
    console.log("optionName"+optionName123);
    document.getElementById('prodStock').value = totalStock;
    console.log("prodStock"+document.getElementById('prodStock').value);
    totalStock = 0;
}

function deleteOption(event) {
    const targetRow = event.target.closest('tr'); // 클릭된 버튼과 가장 가까운 tr 찾기
    targetRow.remove(); // 해당 행을 삭제
    // 현재 옵션 행의 수를 다시 확인
    const options = document.querySelector('.tb4');
    const currentOptionsCount = options.querySelectorAll('tr').length;

    // 옵션이 3개 미만이면 경고 메시지 제거 및 버튼 활성화
    if (currentOptionsCount < 3) {
        const limit3 = document.querySelector('.limit3');
        limit3.innerText = '';
        document.getElementById('check').style.display = 'inline-block';
    }
}

function deleteOption2(event) {
    const targetRow = event.target.closest('div'); // 클릭된 버튼과 가장 가까운 tr 찾기
    targetRow.remove(); // 해당 행을 삭제
    // 현재 옵션 행의 수를 다시 확인
    const options = document.querySelector('.tb4');
    const currentOptionsCount = options.querySelectorAll('tr').length;

    // 옵션이 3개 미만이면 경고 메시지 제거 및 버튼 활성화
    if (currentOptionsCount < 3) {
        const limit3 = document.querySelector('.limit3');
        limit3.innerText = '';
        document.getElementById('check').style.display = 'inline-block';
    }
}

function updateSelect2() {
    let option1Value = document.getElementsByClassName('cate1')[0];
    let option2Value = document.getElementsByClassName('cate2')[0];


    fetch("/admin/prod/cate1", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'  // 데이터를 JSON으로 전송하는 경우
        },
        body: JSON.stringify({id: option1Value.value})
    })
        .then(resp => resp.json())
        .then(data => {
            option2Value.innerHTML = '';
            if (data.length > 0) {
                // 데이터가 있을 경우 옵션 추가
                data.forEach(item => {
                    const prodcate_opt = document.createElement('option');
                    prodcate_opt.value = item.id;
                    prodcate_opt.innerText = item.name;
                    option2Value.appendChild(prodcate_opt);
                    updateSelect3();
                });
            } else {
                // 데이터가 없을 경우 '선택사항 없음' 추가
                const prodcate_opt = document.createElement('option');
                prodcate_opt.innerText = "선택사항 없음";
                option2Value.appendChild(prodcate_opt);
            }
        }).catch(err => {
        console.log(err);
    });
}

function updateSelect3() {
    let option2Value = document.getElementsByClassName('cate2')[0];
    let option3Value = document.getElementsByClassName('cate3')[0];

    fetch("/admin/prod/cate1", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'  // 데이터를 JSON으로 전송하는 경우
        },
        body: JSON.stringify({id: option2Value.value})
    })
        .then(resp => resp.json())
        .then(data => {
            console.log(data);
            option3Value.innerHTML = '';
            if (data.length > 0) {
                // 데이터가 있을 경우 옵션 추가
                data.forEach(item => {
                    const prodcate_opt = document.createElement('option');
                    prodcate_opt.value = item.id;
                    prodcate_opt.innerText = item.name;
                    option3Value.appendChild(prodcate_opt);
                });
            } else {
                // 데이터가 없을 경우 '선택사항 없음' 추가
                const prodcate_opt = document.createElement('option');
                prodcate_opt.innerText = "선택사항 없음";
                option3Value.appendChild(prodcate_opt);
            }
        }).catch(err => {
        console.log(err);
    });

    if (option3Value == null) {
        let option3Value = document.getElementsByClassName('cate3')[0];
        const prodcate_opt = document.createElement('option');
        prodcate_opt.innerText = "선택사항 없음";
        option3Value.appendChild(prodcate_opt);
    }

}
function setSingleOption() {
    // 단일옵션 버튼을 선택
    const button = document.querySelector('#optionChange');
    // 세부옵션 추가 버튼
    const detailOptionButton = document.querySelector('.btnOption.btn_blue');
    // 모든 입력 필드를 가져오기
    const optionInputs = document.querySelectorAll('.option_inputs input');
    // stock input태그 가져오기
    const stockInput = document.querySelector('.option_input_stock');
    // 옵션 추가버튼 가져오기
    const optionPlus = document.getElementById('check');
    // 테이블 내 동적으로 추가된 옵션 행들
    const optionsTable = document.querySelector('.tb4');
    const addedRows = optionsTable.querySelectorAll('tr:not(.tr_option)'); // 기본 행 제외
    // 버튼의 현재 텍스트를 기반으로 조건 처리
    if (button.innerText === "단일옵션") {
        // 버튼 텍스트를 조합형 옵션으로 변경
        button.innerText = "조합형 옵션";
        optionPlus.style.display='none';
        isSingleOption = true; // 단일옵션 상태로 설정
        // 동적으로 추가된 행 삭제
        addedRows.forEach(row => row.remove());
        // 입력 필드에 대해 반복 처리
        optionInputs.forEach(input => {
            // 클래스가 option_input_stock가 아닌 필드를 hidden으로 설정
            if (!input.classList.contains('option_input_stock')) {
                input.type = 'hidden';
                input.value = null; // value를 null로 설정
            }
        });
        stockInput.value = ""; // stock input 빈 문자열로 초기화
        detailOptionButton.style.display = "none";
    } else {
        // 버튼 텍스트를 단일옵션으로 변경
        button.innerText = "단일옵션";
        optionPlus.style.display='inline-block';
        isSingleOption = false; // 조합형 옵션 상태로 설정
        // 입력 필드에 대해 반복 처리하여 type을 text로 복원
        optionInputs.forEach(input => {
            // 클래스가 option_input_stock가 아닌 필드를 text로 설정
            if (!input.classList.contains('option_input_stock')) {
                input.type = 'text';
                input.value = ""; // value를 초기화 (필요에 따라 조정 가능)
            }
        });
        stockInput.value = ""; // stock input 빈 문자열로 초기화
        detailOptionButton.style.display = "inline-block";
    }
}

function generateCombinations() {
    // 입력 필드에서 옵션명과 옵션값을 가져오기
    const optionName1 = document.getElementById("optionName1").value.trim();
    const optionValue1 = document.getElementById("optionValue1").value.trim().split(",");
    const optionName2 = document.getElementById("optionName2").value.trim();
    const optionValue2 = document.getElementById("optionValue2").value.trim().split(",");

    if(optionName1 === ''){
     alert('옵션명1을 적어주세요!')
     return;
    }else if(optionValue1.length === 1 && optionValue1[0] === ''){
        alert('옵션값1을 적어주세요!')
        return;
    }else if(optionName2 ===''){
        alert('옵션명2를 적어주세요!')
        return;
    }else if(optionValue2.length === 1 && optionValue2[0] === ''){
        alert('옵션값2를 적어주세요!')
        return;
    }
    // 옵션 테이블의 본문을 선택
    const optionMixed = document.getElementById("optionMixed");

    // 이전 조합 삭제
    optionMixed.innerHTML = "";

    // 옵션1과 옵션2의 모든 조합 생성
    optionValue1.forEach(value1 => {
        optionValue2.forEach(value2 => {
            const row = document.createElement("tr");

            // 옵션 조합 셀 생성
            const combinationCell = document.createElement("td");
            combinationCell.innerText = `${optionName1}: ${value1.trim()} / ${optionName2}: ${value2.trim()}`;
            combinationCell.style.textAlign = 'center';
            row.appendChild(combinationCell);

            // 추가금액 입력 셀 생성
            const priceCell = document.createElement("td");
            const priceInput = document.createElement("input");
            priceCell.style.textAlign = 'center';
            priceInput.type = "text";
            priceInput.placeholder = "추가금액";
            priceCell.appendChild(priceInput);
            row.appendChild(priceCell);

            // 수량 입력 셀 생성
            const stockCell = document.createElement("td");
            const stockInput = document.createElement("input");
            stockInput.type = "text";
            stockInput.placeholder = "수량";
            stockCell.style.textAlign = 'center';
            stockCell.appendChild(stockInput);
            row.appendChild(stockCell);

            // 조합을 테이블에 추가
            optionMixed.appendChild(row);
        });
    });
}

function showMix() {
    // 조합형 옵션 div를 표시
    const mixOption = document.getElementById("mixOption");
    // 현재 display 상태를 확인하여 토글
    if (mixOption.style.display === "none" || mixOption.style.display === "") {
        mixOption.style.display = "block";  // 보이도록 설정
    } else {
        mixOption.style.display = "none";   // 숨기도록 설정
    }
}
document.addEventListener("DOMContentLoaded", function() {
    let prodPrice = document.getElementById('productPrice');
    let prodDiscount = document.getElementsByClassName('productDiscount')[0];
    let prodPoint = document.getElementById('productPoint');

    function calculatePoint() {
        const price = parseFloat(prodPrice.value) || 0;
        const discount = parseFloat(prodDiscount.value) || 0;

        if(discount > 100){
            alert('100%보다 더 할인할 수 없습니다!')
            prodDiscount.value = '';
            return;
        }

        // 포인트 = (상품금액 - (상품금액 * 할인율 / 100)) * 0.01
        // 계산 결과를 포인트 입력 필드에 출력
        prodPoint.value = Math.floor((price - (price * discount / 100)) * 0.01);
    }

// 이벤트 리스너 등록 (입력 값 변경 시 자동 계산)
    prodPrice.addEventListener("input", calculatePoint);
    prodDiscount.addEventListener("input", calculatePoint);
});