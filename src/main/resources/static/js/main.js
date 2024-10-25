


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
            <a href="#" target="_blank" class="d2_tit cf">${v.name}
                <ul class="dep3 cf">
                    ${subCategories.map(sub => `
                        <li><a href="#" target="_blank" data-value="${sub.id}" onclick="productList(this)">${sub.name}</a></li>
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
    console.log(e.dataset.value);

    const cate = e.dataset.value;
    window.location.href = `/prod/products?cate=${cate}`;


}