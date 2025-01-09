const userName = document.getElementById('username');
const userRole = document.getElementById('userrole')

axios.get("http://localhost:8088/offer/userLogin")
.then((res) => {
  
console.log(res);
userName.innerHTML = res.data.username;
userRole.innerHTML = res.data.role;

})
.catch((err) => {
console.log(err);
});

const exportButton = document.getElementById('exportButton');
const confirmationModal = document.getElementById('confirmationModal');
const yesButton = document.getElementById('yesButton');
const noButton = document.getElementById('noButton');
const contractPeriodStartInput = document.getElementById('contract-period-start');
const contractPeriodEndInput = document.getElementById('contract-period-end');


exportButton.addEventListener('click', () => {
  confirmationModal.style.display = 'block';
});

yesButton.addEventListener('click', () => {
const startDate = new Date(contractPeriodStartInput.value).toISOString();
const endDate = new Date(contractPeriodEndInput.value).toISOString();

const dateExport = [startDate, endDate];

axios.post('http://localhost:8088/offer/exportExcel', dateExport)
  .then(function (response) {
    console.log(response);
  })
  .catch(function (error) {
    console.log(error);
  });

   window.location.href = "http://localhost:8088/offer/download";


  confirmationModal.style.display = 'none';
});

noButton.addEventListener('click', () => {
  confirmationModal.style.display = 'none';
});

window.addEventListener('click', (event) => {
  if (event.target == confirmationModal) {
    confirmationModal.style.display = 'none';
  }
});

const pageSize = 5;
let currentPage = 0;

// Định nghĩa hàm addRow
function addRow(value0, value1, value2, value3, value4, value5, value6) {

  const tableBody = document.getElementById('dataTable').getElementsByTagName('tbody')[0];
  const newRow = document.createElement('tr');
  newRow.status = value6;
  newRow.id = value0;
  newRow.innerHTML = `
    <td>${value1}</td>
    <td>${value2}</td>
    <td>${value3}</td>
    <td>${value4}</td>
    <td>${value5}</td>
    <td>${value6}</td>
    <td>
      <span class="material-icons" id="view">visibility</span>
      <span class="material-icons" id="edit">edit</span>
    </td>
  `;

  newRow.querySelector('#view').addEventListener('click', function() {
    const trId = this.closest('tr').id;
    localStorage.setItem('myVariable', trId);

    const thisStatus = this.closest('tr').status; 
    if(thisStatus === "Waiting For Approval"){
       window.location.href = 'http://127.0.0.1:5500/templates/offerViewDetails.html';
    }
    if(thisStatus === "Approved"){
       window.location.href = 'http://127.0.0.1:5500/templates/offerSentToCandidate.html';
    }
    if(thisStatus === "Waiting For Response"){
       window.location.href = 'http://127.0.0.1:5500/templates/offerCandidate.html';
    }
  });

  newRow.querySelector('#edit').addEventListener('click', function() {
    const trId = this.closest('tr').id;
    localStorage.setItem('myVariable', trId);
    const thisStatus = this.closest('tr').status; 

    if(thisStatus === "Waiting For Approval"){
        window.location.href = 'http://127.0.0.1:5500/templates/offerEdit.html';
    }
  });

  tableBody.appendChild(newRow);
}

function render(data) {
  const paginationContainer = document.getElementById('pagination-container');
  
  for(let i = 0; i < data.length; i++) {
      items.push(data[i].candidateName);
  }

  paginationContainer.innerHTML = '';

  const totalPages = Math.ceil(data.length / pageSize);

  const firstButton = document.createElement('button');
  firstButton.textContent = 'First';
  firstButton.addEventListener('click', () => changePage(data, 0));
  paginationContainer.appendChild(firstButton);

  const prevButton = document.createElement('button');
  prevButton.textContent = 'Previous';
  prevButton.addEventListener('click', () => changePage(data, currentPage - 1));
  paginationContainer.appendChild(prevButton);

  for (let i = 0; i < totalPages; i++) {
    const pageButton = document.createElement('button');
    pageButton.textContent = i + 1;
    pageButton.addEventListener('click', () => changePage(data, i));
    paginationContainer.appendChild(pageButton);
  }

  const nextButton = document.createElement('button');
  nextButton.textContent = 'Next';
  nextButton.addEventListener('click', () => changePage(data, currentPage + 1));
  paginationContainer.appendChild(nextButton);

  const lastButton = document.createElement('button');
  lastButton.textContent = 'Last';
  lastButton.addEventListener('click', () => changePage(data, totalPages - 1));
  paginationContainer.appendChild(lastButton);

  displayData(data, 0);
}

function changePage(data, newPage) {
  const totalPages = Math.ceil(data.length / pageSize);
  
  if (newPage >= 0 && newPage < totalPages) {
    currentPage = newPage;
    displayData(data, currentPage);
  }
}

function displayData(data, pageIndex) {
  const tableBody = document.getElementById('dataTable').getElementsByTagName('tbody')[0];
  tableBody.innerHTML = '';

  const startIndex = pageIndex * pageSize;
  const endIndex = startIndex + pageSize;
  const currentPageData = data.slice(startIndex, endIndex);

  for (let dt of currentPageData) {
    addRow(dt.id, dt.candidateName, dt.email, dt.approver, dt.department, dt.notes, dt.status);
  }

  updatePaginationButtons(Math.ceil(data.length / pageSize));
}

function updatePaginationButtons(totalPages) {
  const buttons = document.getElementById('pagination-container').getElementsByTagName('button');
  
  buttons[0].disabled = currentPage === 0; 
  buttons[1].disabled = currentPage === 0; 
  buttons[buttons.length - 2].disabled = currentPage === totalPages - 1; 
  buttons[buttons.length - 1].disabled = currentPage === totalPages - 1;

  for (let i = 2; i < buttons.length - 2; i++) {
    buttons[i].classList.toggle('active', i - 2 === currentPage);
  }
}

 //searching 
function getDepartment() {
var selectElement = document.getElementById('department');
var selectedValue = selectElement.value;
}
 
const searchInput = document.getElementById('search-input');
const buttonSearch = document.getElementById('search-button');

buttonSearch.addEventListener('click', function() {
  
  getBySearch();
});

function getBySearch(){

  const inputValue = [];
  inputValue.push(searchInput.value);
  var selectElement = document.getElementById('department');
  var selectElementStatus = document.getElementById('status');
  inputValue.push(selectElement.value);
  inputValue.push(selectElementStatus.value);

  console.log(inputValue);

  axios.post('http://localhost:8088/offer/postTest', {
    data: inputValue
  })
  .then(function (response) {
    render(response.data);
    console.log(response);
    
  })
  .catch(function (error) {
    console.log(error);
    
  });
  
}

//gợi ý searching 

const suggestionsContainer = document.getElementById('suggestions');

const items = [];

searchInput.addEventListener('input', function() {
    const searchTerm = this.value.toLowerCase();
    const filteredItems = items.filter(item => 
        item.toLowerCase().includes(searchTerm)
    );

    displaySuggestions(filteredItems);
});

function displaySuggestions(suggestions) {
    suggestionsContainer.innerHTML = '';
    suggestions.forEach(item => {
        const div = document.createElement('div');
        div.textContent = item;
        div.addEventListener('click', function() {
            searchInput.value = this.textContent;
            hideSuggestions();
        });
        suggestionsContainer.appendChild(div);
    });
}

// Thêm hàm mới để ẩn gợi ý
function hideSuggestions() {
    suggestionsContainer.innerHTML = '';
}

// Thêm sự kiện click cho document
document.addEventListener('click', function(event) {
    if (!searchInput.contains(event.target) && !suggestionsContainer.contains(event.target)) {
        hideSuggestions();
    }
});

// Ngăn chặn sự kiện click trên ô input lan ra document
searchInput.addEventListener('click', function(event) {
    event.stopPropagation();
});



axios
.get("http://localhost:8088/offer/getAllOffers")
.then((res) => {

render(res.data);
  
console.log(res);
})
.catch((err) => {
console.log(err);
});