document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('.edit-button').addEventListener('click', function () {
        const idElement = document.getElementById('email');
        const email = idElement.textContent.trim();
        window.location.href = '/user/adminUpdate/' + email;
    });
    document.querySelector('.cancel-button').addEventListener('click', function () {
        window.location.href = '/user/adminList';
    });

});

function showDialog() {
    document.getElementById('confirmDialog').style.display = 'flex';
}

function closeDialog() {
    document.getElementById('confirmDialog').style.display = 'none';
}


function showStatusDialog(email) {
    document.getElementById('statusForm').action = '/user/changeStatus/' + email;
    document.getElementById('confirmStatusDialog').style.display = 'flex';
}

function closeStatusDialog() {
    document.getElementById('confirmStatusDialog').style.display = 'none';
}


document.querySelector('.status-yes').addEventListener('click', function () {
    window.location.href = '/user/userDetailForAdmin';
});

document.querySelector('.btn-yes').addEventListener('click', function () {
    window.location.href = '/login';
});
document.querySelector('.edit-button').addEventListener('click', function () {
        window.location.href = '/user/userEditForAdmin';
});

