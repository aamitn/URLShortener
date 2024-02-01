// src/main/resources/static/js/register.js

document.getElementById('registrationForm').addEventListener('submit', function (event) {
    event.preventDefault();

    // Show loading icon
    const loadingIcon = document.getElementById('loadingIcon');
    loadingIcon.innerHTML = '<svg class="animate-spin h-5 w-5 text-blue-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><circle class="opacity-25" cx="12" cy="12" r="10" stroke-width="4"></circle><path class="opacity-75" d="M4 12a8 8 0 018-8V4m0 16a8 8 0 018-8h2m-8 16a8 8 0 01-8-8h2a6 6 0 016 6v2m-8 0a8 8 0 018-8V4"></path></svg>';

    // Add 'loading' class to the body
    document.body.classList.add('loading');

    var formData = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value,
        email: document.getElementById('email').value,
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value
        // Add other fields as needed
    };

    // Hide the redirection loader initially
    const redirectionLoader = document.getElementById('redirectionLoader');
    redirectionLoader.style.display = 'none';

    axios.post('/api/user/register', formData)
        .then(function (response) {
            // Hide loading icon
            loadingIcon.innerHTML = '';
            // Remove 'loading' class to the body
            document.body.classList.remove('loading');

            // Display the redirection loader with text and countdown
            redirectionLoader.style.display = 'inline';
            redirectionLoader.innerHTML = `
            <div class="spinner"></div>
            <p id="countdown">Redirecting in 5 seconds...</p>
            <p>If not redirected, <a href="${window.location.origin}/verify-registration?email=${encodeURIComponent(document.getElementById('email').value)}">click here</a>.</p>
            `;

            let seconds = 5;

            // Set a timer to update the countdown every second
            const countdownInterval = setInterval(function () {
                document.getElementById('countdown').textContent = `Redirecting in ${seconds} seconds...`;
                seconds--;

                if (seconds < 0) {
                    // Clear the countdown interval
                    clearInterval(countdownInterval);

                    // Hide the redirection loader
                    redirectionLoader.style.display = 'none';

                    // Redirect after the timer
                    window.location.href = "/verify-registration?email=" + document.getElementById('email').value;
                }
            }, 1000); // 1000 milliseconds (1 second)
        })
        .catch(function (error) {
            // Hide loading icon
            loadingIcon.innerHTML = '';

            document.getElementById('info').innerHTML = "<p>Some Error Occurred</p>";
            document.getElementById('info').innerHTML = error.response.data;
            // alert('Registration failed: ' + error.response.data); // Handle failure
        });
});



document.addEventListener("DOMContentLoaded", function () {
    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const usernameStatus = document.getElementById('usernameStatus');
    const emailStatus = document.getElementById('emailStatus');
    const passwordStatus = document.getElementById('passwordStatus');
    const infoDiv = document.getElementById('info');
    const usernameText = document.getElementById('usernameText');
    const emailText = document.getElementById('emailText');
    const passwordText = document.getElementById('passwordText');
    const usernameRequirements = document.getElementById('usernameRequirements');
    const passwordRequirements = document.getElementById('passwordRequirements');


    let usernameTimeout;
    let emailTimeout;
    let passwordTimeout;
    const time = 800;
    checkFormValidity();
    // Add input event listeners
    usernameInput.addEventListener('input', function () {
        clearTimeout(usernameTimeout);
        usernameStatus.innerHTML = '<i class="fa fa-spinner fa-spin"></i>';
        usernameTimeout = setTimeout(() => checkUsername(), time);
        checkFormValidity();
    });

    emailInput.addEventListener('input', function () {
        clearTimeout(emailTimeout);
        emailStatus.innerHTML = '<i class="fa fa-spinner fa-spin"></i>';
        emailTimeout = setTimeout(() => checkEmail(), time);
        checkFormValidity();
    });

    passwordInput.addEventListener('input', function () {
        clearTimeout(passwordTimeout);
        passwordStatus.innerHTML = '<i class="fa fa-spinner fa-spin"></i>';
        passwordTimeout = setTimeout(() => checkPassword(), time);
        checkFormValidity();
    });

    // Add input event listeners
    usernameInput.addEventListener('blur', function () {
        clearTimeout(usernameTimeout);
        usernameStatus.innerHTML = '<i class="fa fa-spinner fa-spin"></i>';
        usernameTimeout = setTimeout(() => checkUsername(), time);
        checkFormValidity();
    });

    emailInput.addEventListener('blur', function () {
        clearTimeout(emailTimeout);
        emailStatus.innerHTML = '<i class="fa fa-spinner fa-spin"></i>';
        emailTimeout = setTimeout(() => checkEmail(), time);
        checkFormValidity();
    });

    passwordInput.addEventListener('blur', function () {
        clearTimeout(passwordTimeout);
        passwordStatus.innerHTML = '<i class="fa fa-spinner fa-spin"></i>';
        passwordTimeout = setTimeout(() => checkPassword(), time);
        checkFormValidity();
    });

    // Add input event listeners
    usernameInput.addEventListener('focus', function () {
        clearTimeout(usernameTimeout);
        usernameStatus.innerHTML = '<i class="fa fa-spinner fa-spin"></i>';
        usernameTimeout = setTimeout(() => checkUsername(), time);
        checkFormValidity();
    });

    emailInput.addEventListener('focus', function () {
        clearTimeout(emailTimeout);
        emailStatus.innerHTML = '<i class="fa fa-spinner fa-spin"></i>';
        emailTimeout = setTimeout(() => checkEmail(), time);
        checkFormValidity();
    });

    passwordInput.addEventListener('focus', function () {
        clearTimeout(passwordTimeout);
        passwordStatus.innerHTML = '<i class="fa fa-spinner fa-spin"></i>';
        passwordTimeout = setTimeout(() => checkPassword(), time);
        checkFormValidity();
    });





    async function checkUsername() {
        const value = usernameInput.value.trim();
      //  const usernameStatus = document.getElementById('usernameStatus');
      //  const usernameText = document.getElementById('usernameText');
      //  const usernameRequirements = document.getElementById('usernameRequirements');

        // Check for valid username format (alphanumeric, no special characters, minimum 4 characters)
        if (!/^[a-zA-Z0-9]{4,}$/.test(value)) {
            usernameStatus.innerHTML = '<i class="fa fa-times-circle"></i>';
            usernameStatus.style.color = 'red';

            // Display username requirements card
            usernameRequirements.innerHTML = `
        <div class="bg-gray-100 border rounded p-2 mt-2">
            <p class="font-semibold text-lg mb-2">Username Requirements:</p>
            <ul class="list-disc pl-4">
                <li class="mb-1">Alphanumeric characters only</li>
                <li class="mb-1">No special characters allowed</li>
                <li class="mb-1">Minimum 4 characters</li>
            </ul>
        </div>
    `;

            // Clear username text
            usernameText.innerHTML = '';
            usernameStatus.classList.remove('valid');
            return;
        }

      await  axios.get(`/api/user/public/check/username?username=${value}`)
            .then(response => {
                const exists = response.data.exists;
                const icon = exists ? '<i class="fa fa-times-circle"></i>' : '<i class="fa fa-check-circle"></i>';
                const text = exists ? 'Username taken' : 'Username available';

                usernameStatus.innerHTML = icon;
                usernameStatus.style.color = exists ? 'red' : 'green';

                // Display username text
                usernameText.innerHTML = text;

                // Add or remove the valid class based on existence
                if (exists) {
                    usernameStatus.classList.remove('valid');
                } else {
                    usernameStatus.classList.add('valid');
                }
        //        console.log(`Username validity: ${usernameStatus.classList.contains('valid')}`);
                // Clear username requirements card
                usernameRequirements.innerHTML = '';
            })
            .catch(error => {
                console.error(error);
                usernameStatus.innerHTML = '<i class="fa fa-exclamation-triangle"></i>';
                usernameStatus.style.color = 'red';

                // Clear username text
                usernameText.innerHTML = '';
                usernameStatus.classList.remove('valid');
            });
    }



    function checkEmail() {
        const value = emailInput.value.trim();
       // const emailStatus = document.getElementById('emailStatus');
     //   const emailText = document.getElementById('emailText');

        // Check for valid email format
        if (!/^\S+@\S+\.\S+$/.test(value)) {
            emailStatus.innerHTML = '<i class="fa fa-times-circle"></i>';
            emailStatus.style.color = 'red';

            // Clear email text
            emailText.innerHTML = '';

            // Remove 'valid' class
            emailStatus.classList.remove('valid');
            return;
        }

        axios.get(`/api/user/public/check/email?email=${value}`)
            .then(response => {
                const exists = response.data.exists;
                const icon = exists ? '<i class="fa fa-times-circle"></i>' : '<i class="fa fa-check-circle"></i>';
                const text = exists ? 'Email taken' : 'Email available';

                emailStatus.innerHTML = icon;
                emailStatus.style.color = exists ? 'red' : 'green';

                // Display email text
                emailText.innerHTML = text;

                // Add or remove 'valid' class based on email existence
                if (exists) {
                    emailStatus.classList.remove('valid');
                } else {
                    emailStatus.classList.add('valid');
                }
               // exists ? emailStatus.classList.remove('valid') : emailStatus.classList.add('valid');
 //               console.log(`Email validity: ${emailStatus.classList.contains('valid')}`);
            })
            .catch(error => {
                console.error(error);
                emailStatus.innerHTML = '<i class="fa fa-exclamation-triangle"></i>';
                emailStatus.style.color = 'red';

                // Clear email text
                emailText.innerHTML = '';

                // Remove 'valid' class
                emailStatus.classList.remove('valid');
            });
    }


    function checkPassword() {
        const value = passwordInput.value.trim();
    //    const passwordStatus = document.getElementById('passwordStatus');
   //     const passwordText = document.getElementById('passwordText');
     //   const passwordRequirements = document.getElementById('passwordRequirements');

        // Check for valid password format (at least 8 characters, 1 special character, and 1 number)
        const isValidPassword = /^(?=.*[A-Za-z0-9])(?=.*[^A-Za-z0-9]).{8,}$/.test(value);

        if (!isValidPassword) {
            passwordStatus.innerHTML = '<i class="fa fa-times-circle"></i>';
            passwordStatus.style.color = 'red';

            // Display password requirements card
            passwordRequirements.innerHTML = `
            <div class="bg-gray-100 border rounded p-4 mt-2">
                <p class="font-semibold text-lg mb-2">Password Requirements:</p>
                <ul class="list-disc pl-4">
                    <li>At least 8 characters</li>
                    <li>At least 1 special character</li>
                    <li>At least 1 number</li>
                </ul>
            </div>
        `;

            // Display password text
            passwordText.innerHTML = 'Invalid password';

            // Remove 'valid' class
            passwordStatus.classList.remove('valid');
        } else {
            passwordStatus.innerHTML = '<i class="fa fa-check-circle"></i>';
            passwordStatus.style.color = 'green';

            // Clear password requirements card
            passwordRequirements.innerHTML = '';

            // Display password text
            passwordText.innerHTML = 'Valid password';

            // Add 'valid' class
            passwordStatus.classList.add('valid');
        }
       // console.log(`Password validity: ${passwordStatus.classList.contains('valid')}`);
    }

    function checkFormValidity() {
        const usernameValid = usernameStatus.classList.contains('valid');
        const emailValid = emailStatus.classList.contains('valid');
        const passwordValid = passwordStatus.classList.contains('valid');

        console.log("VALIDITY")
        console.log(`Username validity: ${usernameValid}`);
        console.log(`Email validity: ${emailValid}`);
        console.log(`Password validity: ${passwordValid}`);

        document.getElementById('info').innerHTML = '';

        // Enable or disable the form based on the validity of all fields
        const submitButton = document.getElementById('registrationForm').querySelector('button[type="submit"]');
        submitButton.disabled = !(usernameValid && emailValid && passwordValid);

        // Show or hide the submit button based on validity
        if (usernameValid && emailValid && passwordValid) {
            submitButton.style.display = 'block'; // Show the button
            submitButton.style.cursor = 'pointer'; // Hide the button
        } else {
         //   submitButton.style.display = 'none'; // Hide the button
            submitButton.style.cursor = 'not-allowed'; // Hide the button
        }
    }
});