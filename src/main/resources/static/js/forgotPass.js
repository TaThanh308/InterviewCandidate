function validateForm() {
    var email = document.getElementById("email").value;
    var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

    if (!emailPattern.test(email)) {
        alert("Vui lòng nhập một địa chỉ email hợp lệ.");
        return false;
    }
    return true;
}


document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('.cancel-button').addEventListener('click', function () {
        window.location.href = '/login';
    });

    document.querySelector('.submit-button').addEventListener('click', function () {
        window.location.href = '/reset-password';
    });

});

