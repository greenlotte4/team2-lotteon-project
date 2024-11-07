let productAll = new FormData();
let submitData = [];
let totalStock = 0;
let isSingleOption = false;
window.onload = function (){
    if(document.getElementById('Category3') !== null){
        updateSelect4();
    }


    function setupImagePreview(inputId, previewId) {
        const input = document.getElementById(inputId);
        const preview = document.getElementById(previewId);

        input.addEventListener("change", function(event) {
            const file = event.target.files[0];
            preview.innerHTML = '';  // 기존 미리보기 초기화

            if (file && file.type.startsWith("image/")) {
                const reader = new FileReader();

                reader.onload = function(e) {
                    const img = document.createElement("img");
                    img.src = e.target.result;
                    img.style.maxWidth = "190px";
                    img.style.maxHeight = "190px";
                    preview.appendChild(img);
                };

                reader.readAsDataURL(file);
            }
        });
    }

    // 각 이미지 입력 필드와 미리보기 영역에 대해 함수 호출
    setupImagePreview("prodListImg", "previewListImg");
    setupImagePreview("prodBasicImg", "previewBasicImg");
    setupImagePreview("prodDetailImg", "previewDetailImg");

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
        console.log("44444"+document.getElementById('prodStock').value);
        if(document.getElementById('Category3') !== null){
            productAll.append('type', 'modify');
            productAll.append('prodId', document.getElementById('prodId').value);
        }else{
            productAll.append('type', 'insert');
            productAll.append('prodId', '0');
        }
        if(document.getElementById('prodStock').value <= 0){
            alert('옵션에서 물품 수량을 지정해주세요!');
        }else {
            fetch('/admin/prod/info', {
                method: 'POST',
                body: productAll
            })
                .then(resp => resp.json())
                .then(data => {
                    if (data.success > 0) {
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
                        alert('결제 정보가 등록되었습니다!');
                        window.location.href = "/admin/prod/products";
                    } else if(data.success === 0) {
                        fetch("/admin/prod/modifyOption", {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'  // 서버가 JSON 데이터를 받을 수 있도록 명시
                            },
                            body: JSON.stringify(optionSubmit)
                        }).then(resp => resp.json())
                            .then(data => {
                                console.log(data);
                            }).catch(err => {
                            console.log(err);
                        })
                        alert('상품 정보가 수정되었습니다!');
                        window.location.href = "/admin/prod/products";
                    }else {
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

function confirmOption(event) {
    const optionName = document.getElementById('optionName1');
    const optionStock = document.getElementById('optionStock');

    if(confirm('옵션을 저장하시겠습니까?')){
    if(isSingleOption){
        if (!optionName.value.trim()) {
            alert('옵션 이름을 입력해 주세요.');
            return; // 함수 실행 중단
        }
    }else{
        if(!optionStock.value.trim()) {
            alert('재고량을 입력해 주세요.');
            return; // 함수 실행 중단
        }
    }
    for(let i = 0; i < currentIndex; i++){
        const inputStock = document.querySelector(`.optionStock[data-id='${i}']`);
        const inputValue = document.querySelector(`.inputAddPrice[data-id='${i}']`);
        submitData[i].stock = inputStock.value;
        submitData[i].additionalPrice = inputValue.value;
        console.log(";;;;;;"+JSON.stringify(submitData[i]));
        totalStock += parseInt(inputStock.value);
        delete submitData[i].index;
    }
    const price = 0;
    if(submitData.length === 0){
        document.getElementById('prodStock').value = parseInt(optionStock.value);
        const jsonData = {
            optionName: '옵션없음',
            optionValue: null,
            optionName2: null,
            optionValue2: null,
            optionName3: null,
            optionValue3: null,
            additionalPrice: price,
            stock: optionStock.value
        };
        console.log(document.getElementById('prodStock').value);
        submitData.push(jsonData);
    }else{
        document.getElementById('prodStock').value = totalStock;
        totalStock = 0;
    }
    alert("옵션이 저장되었습니다!");
    }else {
        alert('옵션이 저장되지 않았습니다!');
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
                    if(item.name === document.getElementsByClassName('cate2')[0].value){
                        prodcate_opt.selected = true;
                    }
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

function showMixOption() {
    currentIndex = 0;
    // 입력 필드에서 옵션명과 옵션값 가져오기
    const optionMix = document.getElementById('mixOption');
    const optionMixed = document.getElementById("optionMixed");
    optionMixed.innerHTML = "";  // 기존의 옵션 조합 초기화

    // 첫 번째 옵션명과 옵션값 가져오기
    const optionName1 = document.getElementById("optionName1").value.trim();
    const optionValue1 = document.getElementById("optionValue1").value.trim().split(",");

    // 두 번째 옵션명과 옵션값 가져오기 (필드가 있을 때만)
    const optionName2 = document.getElementById("optionName2") ? document.getElementById("optionName2").value.trim() : "";
    const optionValue2 = document.getElementById("optionValue2") ? document.getElementById("optionValue2").value.trim().split(",") : [];

    // 세 번째 옵션명과 옵션값 가져오기 (필드가 있을 때만)
    const optionName3 = document.getElementById("optionName3") ? document.getElementById("optionName3").value.trim() : "";
    const optionValue3 = document.getElementById("optionValue3") ? document.getElementById("optionValue3").value.trim().split(",") : [];

    // 옵션1 필드가 비어있을 경우 알림
    if (optionName1 === "") {
        alert('옵션명1을 적어주세요!');
        return;
    } else if (optionValue1.length === 1 && optionValue1[0] === "") {
        alert('옵션값1을 적어주세요!');
        return;
    }

    // 옵션 테이블 보이기
    optionMix.style.display = 'inline-block';

    // 조합 데이터를 저장할 배열
    let a = 0;
    // 옵션 조합 생성
    optionValue1.forEach(value1 => {
        // 옵션1 + 옵션2 조합 생성
        if (optionName2 && optionValue2.length > 0) {
            optionValue2.forEach(value2 => {
                // 옵션1 + 옵션2 + 옵션3 조합 생성
                if (optionName3 && optionValue3.length > 0) {
                    optionValue3.forEach(value3 => {
                        createOptionRow(`${optionName1}: ${value1.trim()} / ${optionName2}: ${value2.trim()} / ${optionName3}: ${value3.trim()}`, 0);
                        const jsonData = {
                            optionName: optionName1,
                            optionValue: value1.trim(),
                            optionName2: optionName2,
                            optionValue2: value2.trim(),
                            optionName3: optionName3,
                            optionValue3: value3.trim(),
                            index: a
                        };
                        submitData.push(jsonData);
                        a++;
                    });
                } else {
                    createOptionRow(`${optionName1}: ${value1.trim()} / ${optionName2}: ${value2.trim()}`, 0);
                    const jsonData = {
                        optionName: optionName1,
                        optionValue: value1.trim(),
                        optionName2: optionName2,
                        optionValue2: value2.trim(),
                        optionName3: null,
                        optionValue3: null,
                        index: a
                    };
                    submitData.push(jsonData);
                    a++;
                }
            });
        } else {
            // 옵션1만 있는 경우
            createOptionRow(`${optionName1}: ${value1.trim()}`, 0);
            const jsonData = {
                optionName: optionName1,
                optionValue: value1.trim(),
                optionName2: null,
                optionValue2: null,
                optionName3: null,
                optionValue3: null,
                index: a
            };
            submitData.push(jsonData);
            a++;
        }
    });
    console.log("new ::::"+JSON.stringify(submitData, null, 2));
}
// 인덱스를 추적하기 위한 변수
let currentIndex = 0;

// 새로운 옵션 조합 행을 테이블에 추가하는 함수
function createOptionRow(combinationText, type) {
    const optionMixed = document.getElementById("optionMixed");

    const row = document.createElement("tr");

    // 옵션 조합 셀
    const combinationCell = document.createElement("td");
    const combinationInput = document.createElement('input');
    combinationCell.innerText = combinationText;
    combinationCell.className = 'option-combination';
    combinationInput.type = "hidden";
    combinationInput.value = combinationText;
    combinationInput.name = "optionName"
    combinationCell.style.textAlign = 'center';
    combinationCell.appendChild(combinationInput);
    row.appendChild(combinationCell);

    // 추가금액 입력 셀
    const priceCell = document.createElement("td");
    const priceInput = document.createElement("input");
    priceInput.type = "text";
    priceInput.dataset.id = `${currentIndex}`;
    priceInput.className = "inputAddPrice";
    priceInput.placeholder = "추가금액";
    priceInput.name = "optionAddPrice";
    priceInput.value = 0;
    priceCell.style.textAlign = 'center';
    priceCell.appendChild(priceInput);
    row.appendChild(priceCell);

    // 수량 입력 셀
    const stockCell = document.createElement("td");
    const stockInput = document.createElement("input");
    stockInput.type = "text";
    stockInput.dataset.id = `${currentIndex}`;
    stockInput.className = "optionStock";
    stockInput.placeholder = "수량";
    stockInput.name = "stock"
    stockInput.value = 0;
    stockCell.style.textAlign = 'center';
    stockCell.appendChild(stockInput);
    row.appendChild(stockCell);
    if (type === 1){
        const id = 0;
        const deleteCell = document.createElement("td");
        const deleteBtn = document.createElement("button");
        const deleteBtn2 = document.createElement("button");
        const hiddenInput = document.createElement('input');
        hiddenInput.value = id;
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'optionId';
        deleteBtn.type = 'button';
        deleteBtn.className = 'deleteOptionBtn';
        deleteBtn2.className = 'deleteOptionBtn';
        deleteBtn2.style.marginLeft = '5px';
        deleteBtn.textContent = '활성화';
        deleteBtn2.textContent = '/  삭제';
        deleteBtn.onclick = function() {
            deleteRow(this)
        };
        deleteBtn2.onclick = function() {
            deleteTr(this)
        };
        deleteCell.appendChild(hiddenInput);
        deleteCell.appendChild(deleteBtn);
        deleteCell.appendChild(deleteBtn2);
        row.appendChild(deleteCell);
    }
    // 조합을 테이블에 추가
    optionMixed.appendChild(row);
    currentIndex++;
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

let optionCount = 1;  // 현재 옵션 필드 개수 추적

function showMix() {
    const optionTable = document.getElementById("optionTable");
    const mix = document.getElementById("mix");
    const optionMix = document.getElementById("optionMix");
    const optionMixPlus = document.getElementById("optionMixPlus");
    const mainTitle = document.getElementsByClassName('main-title')[2];
    const mainDesc = document.getElementsByClassName('main-description')[2];

    if (mix.innerText === "조합형 옵션") {
        // 조합형 옵션으로 전환
        optionTable.innerHTML = `
            <tr class="option0">
                <th>옵션명</th>
                <th>옵션값</th>
            </tr>
            <tr class="option1">
                <td>
                    <input type="text" class="option_input option_input_name1" id="optionName1" placeholder="예: 색상">
                </td>
                <td>
                    <input type="text" class="option_input option_input_value1" id="optionValue1" placeholder="예: 레드, 블루">
                </td>
            </tr>
        `;
        optionCount = 1;  // 초기화
        optionMix.style.display = 'inline-block';
        optionMixPlus.style.display = 'inline-block';
        mix.innerText = "단일 옵션";
        mainTitle.innerText = "조합형 옵션";
        mainDesc.innerText = "옵션을 등록하면 자동으로 조합됩니다.";
        isSingleOption = true;
    } else {
        // 단일 옵션으로 전환
        optionTable.innerHTML = `
            <tr class="tr_option">
                <th style="text-align: left">옵션</th>
                <td class="optionTD" style="text-align: left">
                    <div class="option_inputs">
                        <input type="text" id="optionStock" class="option_input option_input_stock" placeholder="재고량">
                    </div>
                </td>
            </tr>
        `;
        optionMix.style.display = 'none';
        optionMixPlus.style.display = 'none';
        mix.innerText = "조합형 옵션";
        mainTitle.innerText = "상품 재고";
        mainDesc.innerText = "옵션이 없을 경우 재고량을 입력, 있을 경우 조합형 옵션 버튼을 눌러주세요.";
        isSingleOption = false;
    }
}

// 옵션 추가 함수
function addOptionRow() {
    if (optionCount >= 3) {
        alert("최대 3개의 옵션만 추가할 수 있습니다.");
        return;
    }

    optionCount++;
    const optionTable = document.getElementById("optionTable");

    // 새 옵션 행 생성
    const newOptionRow = document.createElement("tr");
    newOptionRow.className = `option${optionCount}`;

    // 옵션명 셀
    const optionNameCell = document.createElement("td");
    const optionNameInput = document.createElement("input");
    optionNameInput.type = "text";
    optionNameInput.className = `option_input option_input_name${optionCount}`;
    optionNameInput.id = `optionName${optionCount}`;
    optionNameInput.placeholder = `예: 옵션명 ${optionCount}`;
    optionNameCell.appendChild(optionNameInput);

    // 옵션값 셀
    const optionValueCell = document.createElement("td");
    const optionValueInput = document.createElement("input");
    optionValueInput.type = "text";
    optionValueInput.className = `option_input option_input_value${optionCount}`;
    optionValueInput.id = `optionValue${optionCount}`;
    optionValueInput.placeholder = `예: 옵션값 ${optionCount}`;
    optionValueInput.style.marginLeft = '48px';
    optionValueCell.appendChild(optionValueInput);

    // 삭제 버튼 생성
    const deleteButton = document.createElement("button");
    deleteButton.type = "button";
    deleteButton.innerText = "삭제";
    deleteButton.className = "deleteOptionButton";
    deleteButton.onclick = function () {
        optionTable.removeChild(newOptionRow); // 현재 행 삭제
        optionCount--; // 옵션 필드 개수 감소
    };
    deleteButton.style.marginLeft = '30px';
    deleteButton.style.backgroundColor = 'white';
    deleteButton.style.border = 'none';
    optionValueCell.appendChild(deleteButton);

    // 행에 셀 추가
    newOptionRow.appendChild(optionNameCell);
    newOptionRow.appendChild(optionValueCell);

    // 테이블에 새 행 추가
    optionTable.appendChild(newOptionRow);
}


