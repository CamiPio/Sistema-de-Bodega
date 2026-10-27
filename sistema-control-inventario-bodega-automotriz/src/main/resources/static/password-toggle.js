document.querySelectorAll('[data-password-toggle]').forEach((checkbox) => {
    const passwordInput = document.getElementById(checkbox.dataset.passwordToggle);
    if (!passwordInput) {
        return;
    }

    checkbox.addEventListener('change', () => {
        passwordInput.type = checkbox.checked ? 'text' : 'password';
    });
});