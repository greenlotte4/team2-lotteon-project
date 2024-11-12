


async function openCategory(event){
    console.log(event.target.dataset.id)
    let id = event.target.dataset.id
    try {
        const response = await fetch(`/categories?id=${id}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const result = await response.json();
        console.log(result.cates)
        const section = document.getElementById('data-response');
        section.innerHTML = result.cates.map(v => {
            // cates2에서 현재 카테고리의 이름에 해당하는 하위 카테고리를 찾기
            const subCategories = result.cates2[v.name] || []; // 없으면 빈 배열로 기본값 설정
            console.log(subCategories)
            return `
        <li class="dep2_line">
            <a href="#" onmouseover="hoverFunction(event)" onclick="productList(${v.id})" data-id="${v.id}" class="d2_tit cf" style="z-index: 1000">${v.name}
                <ul class="dep3 cf" id="${v.id}" style="display: none">
                    ${subCategories.map(sub => `
                        <li><a href="#" data-value="${sub.id}" onclick="productList(${sub.id})">${sub.name}</a></li>
                    `).join('')}
                </ul>
            </a>
        </li>
    `;
        }).join('');

    } catch (error) {
        console.error('Error:', error);
    }


    document.querySelector('.dep2').style.display = 'block'

}

function closePopupCategory(){
     document.querySelector('.dep2').style.display = 'none'
}

function productList(e){
    window.location.href = `/prod/products?cate=${e}`;
}

function hoverFunction(event){
    const id = event.target.dataset.id
    const allDiv = document.querySelectorAll('.dep3')
    allDiv.forEach(v=>{
        if(v.style.display === 'block'){
            v.style.display = 'none';
            v.classList.remove('fade-in')
            v.classList.remove('show')
        }
    })
    document.getElementById(`${id}`).style.display='block'
    const element = document.getElementById(`${id}`)

    // 요소가 보이도록 하고 클래스를 추가
    element.classList.add('fade-in');
    setTimeout(() => {
        element.classList.add('show'); // 0.5초 후에 show 클래스 추가
    }, 10);
}
