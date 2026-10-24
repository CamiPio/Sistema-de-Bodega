document.addEventListener('DOMContentLoaded', () => {
    const btnRecurso = document.getElementById('opRecurso');
    const listaRecurso = document.getElementById('listaRecurso');
    
    const btnPerfil = document.getElementById('opPerfil');
    const listaPerfil = document.getElementById('listaPerfil');

    // Desplegar menú de Acciones
    if (btnRecurso && listaRecurso) {
        btnRecurso.addEventListener('click', (e) => {
            e.stopPropagation();
            listaRecurso.style.display = listaRecurso.style.display === 'block' ? 'none' : 'block';
            if (listaPerfil) listaPerfil.style.display = 'none';
        });
    }

    // Desplegar menú de Perfil
    if (btnPerfil && listaPerfil) {
        btnPerfil.addEventListener('click', (e) => {
            e.stopPropagation();
            listaPerfil.style.display = listaPerfil.style.display === 'block' ? 'none' : 'block';
            if (listaRecurso) listaRecurso.style.display = 'none';
        });
    }

    // Cerrar menús si se hace clic fuera de ellos
    window.addEventListener('click', () => {
        if (listaRecurso) listaRecurso.style.display = 'none';
        if (listaPerfil) listaPerfil.style.display = 'none';
    });
});