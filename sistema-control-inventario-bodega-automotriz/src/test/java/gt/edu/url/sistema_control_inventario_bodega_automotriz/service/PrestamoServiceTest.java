package gt.edu.url.sistema_control_inventario_bodega_automotriz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Catalogo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Prestamo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.CatalogoRepository;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.PrestamoRepository;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private CatalogoRepository catalogoRepository;

    private PrestamoService prestamoService;

    @BeforeEach
    void configurar() {
        prestamoService = new PrestamoService(prestamoRepository, catalogoRepository);
    }

    @Test
    void registraPrestamoYDescuentaUnaUnidadDisponible() {
        Catalogo herramienta = herramienta(5, 3);
        Prestamo prestamo = prestamoValido();
        when(catalogoRepository.findById(8L)).thenReturn(Optional.of(herramienta));

        prestamoService.registrarPrestamo(prestamo);

        assertEquals(2, herramienta.getDisponibilidad());
        assertEquals(true, prestamo.isEstadoActivo());
        verify(prestamoRepository).save(prestamo);
    }

    @Test
    void rechazaPrestamoSinExistenciasYNoGuardaElPrestamo() {
        when(catalogoRepository.findById(8L)).thenReturn(Optional.of(herramienta(5, 0)));

        assertThrows(IllegalArgumentException.class,
                () -> prestamoService.registrarPrestamo(prestamoValido()));

        verify(prestamoRepository, never()).save(any(Prestamo.class));
    }

    @Test
    void registraSolicitudPendienteSinDescontarDisponibilidad() {
        Catalogo herramienta = herramienta(5, 3);
        Prestamo solicitud = prestamoValido();
        solicitud.setIdUsuario(12L);
        when(catalogoRepository.findById(8L)).thenReturn(Optional.of(herramienta));

        prestamoService.registrarSolicitud(solicitud);

        assertEquals(true, solicitud.isEstadoPendiente());
        assertEquals(false, solicitud.isEstadoActivo());
        assertEquals(3, herramienta.getDisponibilidad());
        verify(catalogoRepository, never()).save(any(Catalogo.class));
        verify(prestamoRepository).save(solicitud);
    }

    @Test
    void aprobarSolicitudDescuentaDisponibilidadYActivaElPrestamo() {
        Catalogo herramienta = herramienta(5, 3);
        Prestamo solicitud = prestamoValido();
        solicitud.setIdUsuario(12L);
        solicitud.setEstadoPendiente(true);
        when(prestamoRepository.findById(3L)).thenReturn(Optional.of(solicitud));
        when(catalogoRepository.findById(8L)).thenReturn(Optional.of(herramienta));

        prestamoService.aprobarSolicitud(3L, "Sin daños visibles");

        assertEquals(false, solicitud.isEstadoPendiente());
        assertEquals(true, solicitud.isEstadoActivo());
        assertEquals(2, herramienta.getDisponibilidad());
        verify(catalogoRepository).save(herramienta);
        verify(prestamoRepository).save(solicitud);
    }

    @Test
    void noApruebaSolicitudSinComentarioDeSalida() {
        assertThrows(IllegalArgumentException.class,
                () -> prestamoService.aprobarSolicitud(3L, " "));

        verify(prestamoRepository, never()).findById(3L);
    }

    @Test
    void rechazarSolicitudNoAfectaDisponibilidad() {
        Catalogo herramienta = herramienta(5, 3);
        Prestamo solicitud = prestamoValido();
        solicitud.setEstadoPendiente(true);
        when(prestamoRepository.findById(3L)).thenReturn(Optional.of(solicitud));

        prestamoService.rechazarSolicitud(3L);

        assertEquals(false, solicitud.isEstadoPendiente());
        assertEquals(true, solicitud.isEstadoRechazado());
        assertEquals(3, herramienta.getDisponibilidad());
        verify(catalogoRepository, never()).save(any(Catalogo.class));
        verify(prestamoRepository).save(solicitud);
    }

    @Test
    void rechazaDevolucionRepetida() {
        Prestamo prestamo = prestamoValido();
        prestamo.setEstadoActivo(false);
        when(prestamoRepository.findById(3L)).thenReturn(Optional.of(prestamo));

        assertThrows(IllegalArgumentException.class,
                () -> prestamoService.registrarDevolucion(3L, "Buen estado"));

        verify(catalogoRepository, never()).save(any(Catalogo.class));
    }

    @Test
    void rechazaDevolucionDePrestamoDeOtroUsuario() {
        Prestamo prestamo = prestamoValido();
        prestamo.setIdUsuario(10L);
        when(prestamoRepository.findById(3L)).thenReturn(Optional.of(prestamo));

        assertThrows(IllegalArgumentException.class,
                () -> prestamoService.registrarDevolucion(3L, "Buen estado", 11L));

        verify(catalogoRepository, never()).save(any(Catalogo.class));
    }

    private Catalogo herramienta(int cantidad, int disponibles) {
        Catalogo herramienta = new Catalogo();
        herramienta.setId(8L);
        herramienta.setCantidad(cantidad);
        herramienta.setDisponibilidad(disponibles);
        return herramienta;
    }

    private Prestamo prestamoValido() {
        Prestamo prestamo = new Prestamo();
        prestamo.setIdProducto(8L);
        prestamo.setFecha(LocalDate.now());
        prestamo.setNombreSolicitante("Estudiante");
        return prestamo;
    }
}
