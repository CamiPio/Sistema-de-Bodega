document.addEventListener('DOMContentLoaded', () => {
    const container = document.getElementById('registroNotificaciones');
    if (!container) {
        return;
    }

    const timers = new Map();
    const showToast = (message, type, key = Symbol(message)) => {
        const existing = timers.get(key);
        if (existing) {
            clearTimeout(existing.timer);
            existing.toast.remove();
        }

        const toast = document.createElement('div');
        toast.className = `registro-toast ${type}`;
        toast.setAttribute('role', type === 'error' ? 'alert' : 'status');
        toast.textContent = message;
        container.append(toast);
        requestAnimationFrame(() => toast.classList.add('is-visible'));

        const timer = window.setTimeout(() => {
            toast.classList.remove('is-visible');
            window.setTimeout(() => toast.remove(), 200);
            timers.delete(key);
        }, type === 'success' ? 3500 : 5500);
        timers.set(key, { toast, timer });
    };

    document.querySelectorAll('[data-registro-aviso]').forEach((source) => {
        const message = source.textContent.trim();
        if (message) {
            showToast(message, source.dataset.registroAviso);
        }
        source.remove();
    });

    document.addEventListener('registro:validacion-contrasena', (event) => {
        showToast(event.detail.message, event.detail.type, 'politica-contrasena');
    });
});
