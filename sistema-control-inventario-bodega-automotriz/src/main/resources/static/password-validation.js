document.querySelectorAll('[data-password-policy]').forEach((input) => {
    const feedback = document.getElementById(input.getAttribute('aria-describedby'));
    if (!feedback) {
        return;
    }

    const updateFeedback = () => {
        const password = input.value;
        const issues = [];
        if (password.length < 6) {
            const remaining = 6 - password.length;
            issues.push(`llegar a 6 caracteres (faltan ${remaining})`);
        } else if (password.length > 72) {
            issues.push('no exceder 72 caracteres');
        }
        if (!/[a-z]/.test(password)) issues.push('una minúscula');
        if (!/[A-Z]/.test(password)) issues.push('una mayúscula');
        if (!/[0-9]/.test(password)) issues.push('un número');
        if (!/[^a-zA-Z0-9\s()\-´”`’]/.test(password)) issues.push('un carácter especial');

        const forbidden = [...new Set([...password].filter((character) => '()-´”`’'.includes(character)))];
        if (forbidden.length > 0) {
            issues.push(`quitar el carácter${forbidden.length > 1 ? 'es' : ''} no permitido${forbidden.length > 1 ? 's' : ''} ${forbidden.map((character) => `«${character}»`).join(', ')}`);
        }

        if (issues.length > 0) {
            const message = `Falta: ${issues.join('; ')}.`;
            feedback.textContent = message;
            feedback.classList.remove('valida');
            feedback.classList.add('error');
            input.setCustomValidity(message);
            input.setAttribute('aria-invalid', 'true');
            if (input.closest('.caja-formulario.registro')) {
                document.dispatchEvent(new CustomEvent('registro:validacion-contrasena', {
                    detail: { message, type: 'error' }
                }));
            }
        } else {
            feedback.textContent = 'Contraseña válida.';
            feedback.classList.add('valida');
            feedback.classList.remove('error');
            input.setCustomValidity('');
            input.removeAttribute('aria-invalid');
            if (input.closest('.caja-formulario.registro')) {
                document.dispatchEvent(new CustomEvent('registro:validacion-contrasena', {
                    detail: { message: feedback.textContent, type: 'success' }
                }));
            }
        }
    };

    input.addEventListener('input', updateFeedback);
    input.addEventListener('focus', updateFeedback);
    if (input.value) {
        updateFeedback();
    }
});
