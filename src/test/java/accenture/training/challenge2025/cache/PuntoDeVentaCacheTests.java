package accenture.training.challenge2025.cache;

import accenture.training.challenge2025.dto.punto_de_venta.PuntoDeVenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PuntoDeVentaCacheTests {
    private PuntoDeVentaCache cache;

    @BeforeEach
    void setUp() {
        cache = new PuntoDeVentaCache();
        cache.init();
    }

    @Test
    void getAllReturnsInitialSnapshot() {
        var puntosDeVenta = cache.getAll();

        assertEquals(10, puntosDeVenta.size());
        assertTrue(puntosDeVenta.contains(new PuntoDeVenta(1, "CABA")));
        assertTrue(puntosDeVenta.contains(new PuntoDeVenta(10, "Catamarca")));
    }

    @Test
    void getAllReturnsUnmodifiableSnapshot() {
        var puntosDeVenta = cache.getAll();

        assertThrows(UnsupportedOperationException.class,
            () -> puntosDeVenta.clear());
        assertEquals(10, cache.getAll().size());
    }

    @Test
    void saveUpdateAndDeleteWorkWithConcurrentMap() {
        cache.save(new PuntoDeVenta(11, "Mendoza"));
        assertEquals("Mendoza", cache.findById(11).orElseThrow().nombre());

        cache.update(11, new PuntoDeVenta(11, "Mendoza Centro"));
        assertEquals("Mendoza Centro", cache.findById(11).orElseThrow().nombre());

        cache.delete(11);
        assertTrue(cache.findById(11).isEmpty());
    }

    @Test
    void supportsConcurrentReadsAndWrites() throws InterruptedException {
        int operations = 100;
        ExecutorService executor = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(operations);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < operations; i++) {
            int id = 1000 + i;
            futures.add(executor.submit(() -> {
                try {
                    start.await();
                    cache.save(new PuntoDeVenta(id, "PDV_" + id));
                    cache.getAll();
                    cache.update(id, new PuntoDeVenta(id, "PDV_UPDATED_" + id));
                    assertTrue(cache.findById(id).isPresent());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    fail(e);
                } finally {
                    done.countDown();
                }
            }));
        }

        start.countDown();
        assertTrue(done.await(5, TimeUnit.SECONDS));
        futures.forEach(future -> assertDoesNotThrow(() -> future.get()));
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        assertEquals(10 + operations, cache.getAll().size());
    }
}
