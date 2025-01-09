// Hàm để chuyển đổi fullname thành username
function convertFullnameToUsername(fullname) {
    var words = fullname.split(' ');
    var reversedWords = words.reverse();

    // Lấy toàn bộ từ cuối cùng sau khi đảo ngược
    var lastWord = reversedWords[0];

    // Lấy chữ cái đầu của các từ trừ từ cuối cùng
    var initials = reversedWords.slice(1).map(word => word.charAt(0)).join('');

    // Kết hợp từ cuối cùng và các chữ cái đầu
    var username = lastWord + initials;

    return username.toLowerCase();
}

// Hàm để kiểm tra tính duy nhất của username
async function isUsernameUnique(username) {
    try {
        const response = await fetch(`/user/check-username?username=${username}`);
        const data = await response.json();
        return data.unique;
    } catch (error) {
        console.error("Error checking username uniqueness:", error);
        return false;
    }
}

// Hàm để tạo username duy nhất
async function generateUniqueUsername(fullname) {
    let baseUsername = convertFullnameToUsername(fullname);
    let uniqueUsername = baseUsername;
    let counter = 1;

    while (!(await isUsernameUnique(uniqueUsername))) {
        uniqueUsername = `${baseUsername}${counter}`;
        counter++;
    }

    return uniqueUsername;
}

// Sử dụng hàm để chuyển đổi và cập nhật giá trị của input
document.addEventListener("DOMContentLoaded", function() {
    var fullnameInput = document.getElementById("name");
    var usernameInput = document.getElementById("username");

    fullnameInput.addEventListener("input", async function() {
        var fullname = fullnameInput.value.trim();
        var username = await generateUniqueUsername(fullname);
        usernameInput.value = username;
    });
});


document.addEventListener('DOMContentLoaded', function() {

    var fullnameInput = document.getElementById("name");
    var usernameInput = document.getElementById("username");

    fullnameInput.addEventListener("input", function() {
        var fullname = fullnameInput.value.trim();
        var username = convertFullnameToUsername(fullname);
        usernameInput.value = username;
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

document.querySelector('.btn-yes').addEventListener('click', function () {
    window.location.href = '/login';
});