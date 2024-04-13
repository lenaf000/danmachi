document.getElementById("signup-form").addEventListener("submit", function(event) {
    event.preventDefault(); // Prevent default form submission

    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;
    const email = document.getElementById("email").value;
    const username = document.getElementById("username").value;
    const passwordError = document.getElementById("passwordError");
    const emailError = document.getElementById("emailError");
    const usernameError = document.getElementById("usernameError");

    // Reset error messages
    passwordError.textContent = "";
    emailError.textContent = "";
    usernameError.textContent = "";

    // Validate password matching
    if (password !== confirmPassword) {
        passwordError.textContent = "Passwords do not match";
        return;
    }

    // Validate email format
    if (!validateEmail(email)) {
        emailError.textContent = "Invalid email format";
        return;
    }

    // Check email and username uniqueness
    $.ajax({
        url: "/checkUnique",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ "username": username, "email": email }),
        success: function(response) {
            if (response.usernameExists) {
                usernameError.textContent = "Username is already taken";
                return;
            }
            if (response.emailExists) {
                emailError.textContent = "Email is already registered";
                return;
            }

            // If all validations pass, submit the form
            document.getElementById("signup-form").submit();
        },
        error: function(xhr, status, error) {
            console.error("Error checking uniqueness:", error);
            // Handle error if needed
        }
    });
});

function validateEmail(email) {
    var re = /\S+@\S+\.\S+/;
    return re.test(email);
}