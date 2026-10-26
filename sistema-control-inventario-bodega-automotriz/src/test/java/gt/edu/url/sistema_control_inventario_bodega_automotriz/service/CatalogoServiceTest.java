package gt.edu.url.sistema_control_inventario_bodega_automotriz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gt.edu.url.sistema_control_inventario_bodega_automotriz.model.Catalogo;
import gt.edu.url.sistema_control_inventario_bodega_automotriz.repository.CatalogoRepository;

@ExtendWith(MockitoExtension.class)
class CatalogoServiceTest {

    @Mock
    private CatalogoRepository catalogoRepository;

    private CatalogoService catalogoService;

    @BeforeEach
    void configurar() {
        catalogoService = new CatalogoService(catalogoRepository);
    }

    @Test
    void asignaLaCantidadTotalComoDisponibilidadInicial() {
        Catalogo herramienta = herramienta(4, null);

        catalogoService.guardar(herramienta);

        assertEquals(4, herramienta.getDisponibilidad());
        verify(catalogoRepository).save(herramienta);
    }

    @Test
    void conservaExistenciasPrestadasAlEditarCantidad() {
        Catalogo existente = herramienta(4, 2);
        existente.setId(1L);
        Catalogo editada = herramienta(5, 999);
        editada.setId(1L);
        when(catalogoRepository.findById(1L)).thenReturn(Optional.of(existente));

        catalogoService.guardar(editada);

        assertEquals(3, editada.getDisponibilidad());
        verify(catalogoRepository).save(editada);
    }

    @Test
    void ignoraDisponibilidadEnviadaYLaCalculaParaHerramientaNueva() {
        Catalogo herramienta = herramienta(4, 5);

        catalogoService.guardar(herramienta);

        assertEquals(4, herramienta.getDisponibilidad());
        verify(catalogoRepository).save(herramienta);
    }

    @Test
    void noPermiteReducirCantidadPorDebajoDeLasUnidadesPrestadas() {
        Catalogo existente = herramienta(4, 1);
        existente.setId(1L);
        Catalogo editada = herramienta(2, 1);
        editada.setId(1L);
        when(catalogoRepository.findById(1L)).thenReturn(Optional.of(existente));

        assertThrows(IllegalArgumentException.class, () -> catalogoService.guardar(editada));

        verify(catalogoRepository, never()).save(editada);
    }

    private Catalogo herramienta(int cantidad, Integer disponibilidad) {
        Catalogo herramienta = new Catalogo();
        herramienta.setNombre("Llave");
        herramienta.setCategoria("Herramienta");
        herramienta.setDescripcion("Llave de prueba");
        herramienta.setCantidad(cantidad);
        herramienta.setDisponibilidad(disponibilidad);
        return herramienta;
    }
}
