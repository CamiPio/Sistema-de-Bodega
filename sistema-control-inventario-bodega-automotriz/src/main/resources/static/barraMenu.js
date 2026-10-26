const inicializarMenu = () => {
    const barra = document.querySelector('.barra-menu');
    if (!barra || barra.dataset.inicializada === 'true') {
        return;
    }
    barra.dataset.inicializada = 'true';

    const accesos = barra.querySelector('#accesosDirectos');
    const botonMenu = barra.querySelector('#menuBtn');
    const desplegables = [
        [barra.querySelector('#opRecurso'), barra.querySelector('#listaRecurso')],
        [barra.querySelector('#opPerfil'), barra.querySelector('#listaPerfil')]
    ];

    const cerrarDesplegables = () => {
        desplegables.forEach(([boton, lista]) => {
            if (boton && lista) {
                lista.classList.remove('is-open');
                boton.setAttribute('aria-expanded', 'false');
            }
        });
    };

    if (botonMenu && accesos) {
        botonMenu.addEventListener('click', () => {
            const abierto = accesos.classList.toggle('is-open');
            botonMenu.setAttribute('aria-expanded', String(abierto));
            if (!abierto) {
                cerrarDesplegables();
            }
        });
    }

    desplegables.forEach(([boton, lista]) => {
        if (!boton || !lista) {
            return;
        }

        boton.addEventListener('click', (evento) => {
            evento.stopPropagation();
            const abierto = lista.classList.toggle('is-open');
            boton.setAttribute('aria-expanded', String(abierto));
            desplegables.forEach(([otroBoton, otraLista]) => {
                if (otraLista && otraLista !== lista) {
                    otraLista.classList.remove('is-open');
                    otroBoton.setAttribute('aria-expanded', 'false');
                }
            });
        });
    });

    document.addEventListener('click', (evento) => {
        if (!barra.contains(evento.target)) {
            cerrarDesplegables();
            if (accesos) {
                accesos.classList.remove('is-open');
            }
            if (botonMenu) {
                botonMenu.setAttribute('aria-expanded', 'false');
            }
        }
    });

    document.addEventListener('keydown', (evento) => {
        if (evento.key === 'Escape') {
            cerrarDesplegables();
            if (accesos) {
                accesos.classList.remove('is-open');
            }
            if (botonMenu) {
                botonMenu.setAttribute('aria-expanded', 'false');
            }
        }
    });
};

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', inicializarMenu, { once: true });
} else {
    inicializarMenu();
}