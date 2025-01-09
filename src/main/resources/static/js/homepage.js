function showDialog() {
    document.getElementById('confirmDialog').style.display = 'flex';
}

function closeDialog() {
    document.getElementById('confirmDialog').style.display = 'none';
}

document.querySelector('.btn-yes').addEventListener('click', function () {
    window.location.href = '/login';
});