    const productAll = new FormData();
    let submitData = [];
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
        fetch('/admin/prod/info', {
            method: 'POST',
            body: productAll
         })
            .then(resp => resp.json())
            .then(data => {
            if (data.success > 0) {
            alert('결제 정보가 등록되었습니다!');
            for(let i = 0; i < submitData.length; i++){
                submitData[i].productId = String(data.success);
            }
            console.log("44555"+JSON.stringify(submitData));
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
        window.location.href ="/admin/prod/products";
        } else {
            alert('결제 정보 등록에 실패하였습니다');
        }

        })
            .catch(err => {
             console.log(err);
        });
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

    if (!option1.trim()) {
    alert('옵션명을 입력해주세요.')
    console.log(event)
    return;
}

    // 새로운 입력 세트 추가
    newInputs.innerHTML = `
            <div class="option_inputs">
                <input type="text" style="opacity:0" class="option_input option_input_name" value="${option1}">
                <input type="text" class="option_input option_input_value" placeholder="세부옵션">
                <input type="text" class="option_input option_input_price" placeholder="추가금액">
                <button class="btnDelete" type="button" onclick="deleteOption2(event)">삭제</button>
            </div>
        `;

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
                    <input type="text" class="option_input option_input_name" placeholder="옵션명">
                    <input type="text" class="option_input option_input_value" placeholder="세부옵션">
                    <input type="text" class="option_input option_input_price" placeholder="추가금액">
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
    document.getElementById('check').disabled = true;
    return;
}

}

    function confirmOption(event) {
    const optionName = document.getElementsByClassName('option_input option_input_name')[0];

    if (!optionName.value.trim()) {
    alert('옵션 이름을 입력해 주세요.');
    return; // 함수 실행 중단
}

    document.querySelectorAll('.option_inputs').forEach(v => {
    let name = v.querySelector('.option_input_name').value;
    let value = v.querySelector('.option_input_value').value;
    let price = v.querySelector('.option_input_price').value;

    let jsonData = {
    optionName: name,
    optionValue: value,
    additionalPrice: price,
    productId: null
};

    submitData.push(jsonData);
});
    console.log(submitData)
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
    document.getElementById('check').disabled = false;
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
    document.getElementById('check').disabled = false;
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
