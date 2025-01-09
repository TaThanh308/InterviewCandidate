let Id = localStorage.getItem('myVariable');
console.log(Id);

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

const approve = document.getElementById('approve');
const reject = document.getElementById('reject');

approve.addEventListener('click', () => {
  let status = "Approved";
  editStatus(status);
  window.location.href = 'http://127.0.0.1:5500/templates/offer.html';
});

reject.addEventListener('click', () => {
  let status = "Rejected";
  editStatus(status);
  window.location.href = 'http://127.0.0.1:5500/templates/offer.html';
});

function editStatus(status) {
  console.log(status);
  axios.post('http://localhost:8088/offer/editStatus',{ status: status, id:Id })
  .then(function (response) {
    console.log(response);
  })
  .catch(function (error) {
    console.log(error);
  });
}

const create = document.getElementById('date-create');
const candidateName = document.getElementById('candidate-name');
const position = document.getElementById('position');
const Approver = document.getElementById('Approver');
const interviewInfo = document.getElementById('interview-info');
const interviewer = document.getElementById('interviewer');
const contractPeriodStart = document.getElementById('contract-period-start');
const contractPeriodEnd = document.getElementById('contract-period-end');
const interviewNotes = document.getElementById('interview-notes');
const contractType = document.getElementById('contract-type');
const level = document.getElementById('Level');
const department = document.getElementById('department');
const recruiterOwner = document.getElementById('recruiter-owner');
const dueDate = document.getElementById('due-date');
const basicSalary = document.getElementById('basic-salary');
const notes = document.getElementById('note');
const status = document.getElementById('status');

const params = new URLSearchParams();
params.append('id',Id);

axios.get('http://localhost:8088/offer/view', { params: params })
  .then(function(response) {

    
    console.log(response);
    data = response.data;

    candidateName.innerHTML = data.candidate;
    position.innerHTML = data.position;
    Approver.innerHTML = data.approver;
    interviewInfo.innerHTML = data.interviewInfo;
    interviewer.innerHTML = data.interviewer;
    contractPeriodStart.innerHTML = data.contractFrom;
    contractPeriodEnd.innerHTML = data.contractTo;
    interviewNotes.innerHTML = data.interviewNotes;
    contractType.innerHTML = data.contractType;
    level.innerHTML = data.level;
    department.innerHTML = data.department;
    recruiterOwner.innerHTML = data.recruiter;
    dueDate.innerHTML = data.dueDate;
    basicSalary.innerHTML = data.salaryBasic;
    notes.innerHTML = data.notes;
    status.innerHTML = data.status;
    create.innerHTML = data.update;

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

  let status = "Declined";
  editStatus(status);
  confirmationModal.style.display = 'none';
});

noButton.addEventListener('click', () => {
  confirmationModal.style.display = 'none';
});

window.addEventListener('click', (event) => {
  if (event.target === confirmationModal) {
    confirmationModal.style.display = 'none';
  }
});