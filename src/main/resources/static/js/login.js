const inputs = document.querySelectorAll(".form-group input");

inputs.forEach((input) => {
    input.addEventListener("focus", function () {
        this.parentElement
            .querySelector(".input-label")
            .classList.add("active");
    });
    input.addEventListener("blur", function () {
        if (this.value === "") {
            this.parentElement
                .querySelector(".input-label")
                .classList.remove("active");
        }
    });
});

