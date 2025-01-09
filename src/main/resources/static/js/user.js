document.querySelector('.add-new-button').addEventListener('click', function () {
    window.location.href = '/user/form/create';
});

function showDialog() {
    document.getElementById('confirmDialog').style.display = 'flex';
}

function closeDialog() {
    document.getElementById('confirmDialog').style.display = 'none';
}

document.querySelector('.btn-yes').addEventListener('click', function () {
    window.location.href = '/login';
});