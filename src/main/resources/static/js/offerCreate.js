//set userLogin
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

// check candidate and interview
const candidateSelect1 = document.getElementById('candidate'); 
const interviewinfoSelect1 = document.getElementById('interview-info');
const interviewNotes = document.getElementById('interview-notes');

const handleClick = () => {
  if (candidateSelect1.value && interviewinfoSelect1.value) {
    const params = new URLSearchParams({
      candidateName: candidateSelect1.value,
      interviewName: interviewinfoSelect1.value
    });

    axios.get('http://localhost:8088/offer/getInterviewNotes', { params })
      .then(response => {
        interviewNotes.value = response.data;
        const errorMessageInterviewNotes = document.getElementById('interview-notes-error-message');
        
        if (response.data === "") {
          errorMessageInterviewNotes.textContent = "Candidate is not in this interview";
          errorMessageInterviewNotes.style.display = 'block';
        } else {
          errorMessageInterviewNotes.style.display = 'none';
        }
      })
      .catch(err => console.error('Error fetching interview notes:', err));
  }
};

if (interviewinfoSelect1) {
  interviewinfoSelect1.addEventListener('click', handleClick);
}

if (candidateSelect1) {
  candidateSelect1.addEventListener('click', handleClick);
}

// Select
function setCandidateName(candidates){
const candidateSelect = document.getElementById('candidate');

candidates.forEach(candidate => {
  const option = document.createElement('option');
  option.value = candidate;
  option.text = candidate;
  candidateSelect.add(option);
});
}
function setPosition(positions){
const positionSelect = document.getElementById('position');

positions.forEach(position => {
  const option = document.createElement('option');
  option.value = position;
  option.text = position;
  positionSelect.add(option);
});
}
function setApprover(approvers){
const approverSelect = document.getElementById('approver');

approvers.forEach(approver => {
  const option = document.createElement('option');
  option.value = approver;
  option.text = approver;
  approverSelect.add(option);
});
}

function setInterviewInfo(interviewinfos){
const interviewInfoSelect = document.getElementById('interview-info');

interviewinfos.forEach(interviewinfo => {
  const option = document.createElement('option');
  option.value = interviewinfo;
  option.text = interviewinfo;
  interviewInfoSelect.add(option);
});
}

function setContractType(contractypes){
const contractTypeSelect = document.getElementById('contracttype');

contractypes.forEach(contracttype => {
  const option = document.createElement('option');
  option.value = contracttype;
  option.text = contracttype;
  contractTypeSelect.add(option);
});
}

function setLevel(levels){
const levelSelect = document.getElementById('level');

levels.forEach(level => {
  const option = document.createElement('option');
  option.value = level;
  option.text = level;
  levelSelect.add(option);
});
}
function setDepartment(departments){
const departmentSelect = document.getElementById('department');

departments.forEach(department => {
  const option = document.createElement('option');
  option.value = department;
  option.text = department;
  departmentSelect.add(option);
});
}
function setRecruiterOwner(recruiterowners){
const recruiterOwnerSelect = document.getElementById('recruiter-owner');

recruiterowners.forEach(recruiterowner => {
  const option = document.createElement('option');
  option.value = recruiterowner;
  option.text = recruiterowner;
  recruiterOwnerSelect.add(option);
});


}
//get value Create
function getCandidateName() {
 // Lấy phần tử <select>
var selectElement = document.getElementById('candidate');
// Lấy value của option được chọn
var selectedValue = selectElement.value;
console.log(selectedValue);
}
function getPosition() {
var selectElement = document.getElementById('position');
var selectedValue = selectElement.value;
console.log(selectedValue);
}
function getApprover() {
var selectElement = document.getElementById('approver');
var selectedValue = selectElement.value;
console.log(selectedValue);
}
function getInterviewInfo() {
var selectElement = document.getElementById('interview-info');
var selectedValue = selectElement.value;
console.log(selectedValue);
}
function getForm() {
var selectElement = document.getElementById('contract-period-start');
var selectedValue = selectElement.value;
console.log(selectedValue);
}

function getTo() {
var selectElement = document.getElementById('contract-period-end');
var selectedValue = selectElement.value;
console.log(selectedValue);
}
function getInterviewNotes() {
var selectElement = document.getElementById('interview-notes');
var selectedValue = selectElement.value;
console.log(selectedValue);
}
function getContractType() {
var selectElement = document.getElementById('contracttype');
var selectedValue = selectElement.value;
console.log(selectedValue);
}
function getLevel() {
var selectElement = document.getElementById('level');
var selectedValue = selectElement.value;
console.log(selectedValue);
}
function getDepartment() {
var selectElement = document.getElementById('department');
var selectedValue = selectElement.value;
console.log(selectedValue);
}
function getRecruiterOwner() {
var selectElement = document.getElementById('recruiter-owner');
var selectedValue = selectElement.value;
console.log(selectedValue);
}
function getDueDate() {
var selectElement = document.getElementById('due-date');
var selectedValue = selectElement.value;
console.log(selectedValue);
}
function getBasicSalary() {
var selectElement = document.getElementById('basic-salary');
var selectedValue = selectElement.value;
console.log(selectedValue);
}
function getNote() {
var selectElement = document.getElementById('note');
var selectedValue = selectElement.value;
console.log(selectedValue);
}

const buttonSubmit = document.getElementById('submit');

buttonSubmit.addEventListener('click', function() {
  if(errorMessage.textContent === ""&& errorMessage1.textContent ==="" && errorMessage2.textContent ===""){
    const inputValue = getInputValues();

  axios.post('http://localhost:8088/offer/create', inputValue)
    .then(function(response) {

      var alertElement = document.createElement('alert');
      alertElement.setAttribute('role', 'alert');
      alertElement.textContent = response.data;

      document.body.appendChild(alertElement);

      setTimeout(function() {
        alertElement.style.display = 'none';
      }, 3000);
      console.log(response);
      window.location.href = 'http://127.0.0.1:5500/templates/offer.html';
    })
    .catch(function(error) {

      var alertElement = document.createElement('alert');
      alertElement.setAttribute('role', 'alert');
      alertElement.textContent = "Please fill in again";

      // Thêm phần tử <alert> vào body của trang web
      document.body.appendChild(alertElement);

      setTimeout(function() {
        alertElement.style.display = 'none';
      }, 3000);
    });
  }
  else{
     var alertElement = document.createElement('alert');
      alertElement.setAttribute('role', 'alert');
      alertElement.textContent = "Cannot Submit";
      document.body.appendChild(alertElement);
      setTimeout(function() {
        alertElement.style.display = 'none';
      }, 3000);
  }
});

function getInputValues() {
  const inputValueCreate = [
    document.getElementById('candidate').value,
    document.getElementById('position').value,
    document.getElementById('approver').value,
    document.getElementById('interview-info').value,
    document.getElementById('contract-period-start').value,
    document.getElementById('contract-period-end').value,
    document.getElementById('interview-notes').value,
    document.getElementById('contracttype').value,
    document.getElementById('level').value,
    document.getElementById('department').value,
    document.getElementById('recruiter-owner').value,
    document.getElementById('due-date').value,
    document.getElementById('basic-salary').value,
    document.getElementById('note').value
  ];

  return inputValueCreate;
}


axios
.get("http://localhost:8088/offer/getdefault")
.then((res) => {
  console.log(res);

for(var i = 0; i < res.data.length; i++) {
  switch(i){
    case 0: setCandidateName(res.data[i]); break;
    case 1: setPosition(res.data[i]); break;
    case 2: setApprover(res.data[i]); break;
    case 3: setInterviewInfo(res.data[i]); break;
    case 4: setContractType(res.data[i]); break;
    case 5: setLevel(res.data[i]); break;
    case 6: setDepartment(res.data[i]); break;
    case 7: setRecruiterOwner(res.data[i]); break;
  }
}

})
.catch((err) => {
console.log(err);
});

// cancel
const cancelButton = document.getElementById('cancelButton');
const confirmationModal = document.getElementById('confirmationModal');
const yesButton = document.getElementById('yesButton');
const noButton = document.getElementById('noButton');

cancelButton.addEventListener('click', () => {
  confirmationModal.style.display = 'block';
});

yesButton.addEventListener('click', () => {
  // Perform cancel action here
  confirmationModal.style.display = 'none';
  window.location.href = 'http://127.0.0.1:5500/templates/offer.html';
});

noButton.addEventListener('click', () => {
  confirmationModal.style.display = 'none';
});

window.addEventListener('click', (event) => {
  if (event.target === confirmationModal) {
    confirmationModal.style.display = 'none';
  }
});

//error message
const startDate = document.getElementById('contract-period-start');
    const endDate = document.getElementById('contract-period-end');
    const errorMessage = document.getElementById('error-message');
    const errorMessage1 = document.getElementById('due-date-error-message');
    const errorMessage2 = document.getElementById('salary-error-message');


    function validateDates() {
        if (startDate.value && endDate.value) {
            if (new Date(startDate.value) > new Date(endDate.value)) {
                errorMessage.textContent = "The from date cannot be greater than the to date.";
                errorMessage.style.display = 'block';
            } else {
                errorMessage.textContent = "";
                errorMessage.style.display = 'none';
            }
        }
    }

    startDate.addEventListener('change', validateDates);
    endDate.addEventListener('change', validateDates);

 const basicSalary = document.getElementById('basic-salary');
    const salaryErrorMessage = document.getElementById('salary-error-message');

    function validateSalary() {
        const salaryValue = basicSalary.value.trim();
        if (salaryValue === '') {
            salaryErrorMessage.textContent = "";
            salaryErrorMessage.style.display = 'none';
        } else if (isNaN(salaryValue) || !Number.isFinite(parseFloat(salaryValue))) {
            salaryErrorMessage.textContent ="The base salary must be a valid number.";
            salaryErrorMessage.style.display = 'block';
        } else {
            salaryErrorMessage.textContent = "";
            salaryErrorMessage.style.display = 'none';
        }
    }

    basicSalary.addEventListener('input', validateSalary);
    basicSalary.addEventListener('blur', validateSalary);

const dueDate = document.getElementById('due-date');
    const dueDateErrorMessage = document.getElementById('due-date-error-message');

    function validateDueDate() {
        const selectedDate = new Date(dueDate.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0); // Đặt thời gian về 00:00:00 để so sánh chỉ ngày

        if (dueDate.value === '') {
            dueDateErrorMessage.textContent = "";
            dueDateErrorMessage.style.display = 'none';
        } else if (selectedDate < today) {
            dueDateErrorMessage.textContent = "The due date cannot lie in the past.";
            dueDateErrorMessage.style.display = 'block';
        } else {
            dueDateErrorMessage.textContent = "";
            dueDateErrorMessage.style.display = 'none';
        }
    }

    dueDate.addEventListener('change', validateDueDate);