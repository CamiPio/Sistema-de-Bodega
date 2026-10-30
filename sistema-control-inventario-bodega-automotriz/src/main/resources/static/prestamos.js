const inicializarBusquedaProductos = () => {
    const busqueda = document.querySelector('#buscarProductoPrestamo');
    const selector = document.querySelector('#productoPrestamo');
    const resultado = document.querySelector('#resultadoBusquedaProducto');

    if (!busqueda || !selector || !resultado) {
        return;
    }

    const opcionVacia = selector.querySelector('option[value=""]');
    const opciones = Array.from(selector.options).filter((opcion) => opcion.value !== '');

    if (!opcionVacia || opciones.length === 0) {
        resultado.textContent = 'No hay herramientas disponibles para seleccionar.';
        return;
    }

    const normalizar = (texto) => texto
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toLocaleLowerCase();

    busqueda.addEventListener('input', () => {
        const consulta = normalizar(busqueda.value.trim());
        const opcionesFiltradas = opciones.filter((opcion) =>
            normalizar(opcion.textContent).includes(consulta)
        );
        const valorSeleccionado = selector.value;
        const fragmento = document.createDocumentFragment();

        fragmento.append(opcionVacia.cloneNode(true));
        opcionesFiltradas.forEach((opcion) => fragmento.append(opcion));
        selector.replaceChildren(fragmento);

        if (opcionesFiltradas.some((opcion) => opcion.value === valorSeleccionado)) {
            selector.value = valorSeleccionado;
        }

        resultado.textContent = opcionesFiltradas.length === 0
            ? 'No se encontraron herramientas con ese nombre.'
            : `${opcionesFiltradas.length} herramienta${opcionesFiltradas.length === 1 ? '' : 's'} encontrada${opcionesFiltradas.length === 1 ? '' : 's'}.`;
    });
};

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', inicializarBusquedaProductos, { once: true });
} else {
    inicializarBusquedaProductos();
}
