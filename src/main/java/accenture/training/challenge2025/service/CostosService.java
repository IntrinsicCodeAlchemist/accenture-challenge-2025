package accenture.training.challenge2025.service;

import accenture.training.challenge2025.cache.CostosCache;
import accenture.training.challenge2025.cache.PuntoDeVentaCache;
import accenture.training.challenge2025.constants.Constants;
import accenture.training.challenge2025.dto.costos.CaminoMinimoResponse;
import accenture.training.challenge2025.dto.costos.Costo;
import accenture.training.challenge2025.dto.costos.CostoRequest;
import accenture.training.challenge2025.dto.costos.CostosOrigenResponse;
import accenture.training.challenge2025.dto.costos.Vecino;
import accenture.training.challenge2025.dto.punto_de_venta.PuntoDeVenta;
import accenture.training.challenge2025.exception.BadRequestException;
import accenture.training.challenge2025.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CostosService {
    private final CostosCache costosCache;
    private final PuntoDeVentaCache puntoDeVentaCache;

    public void agregarCosto(CostoRequest request) {
        validarPuntosExistentes(request.origen(), request.destino());
        validarCostoDirecto(request.origen(), request.destino(), request.costo());
        costosCache.addCosto(request.origen(), request.destino(), request.costo());
    }

    public void removerCosto(int origen, int destino) {
        validarPuntosExistentes(origen, destino);
        costosCache.removeCosto(origen, destino);
    }

    /**
     * Ejecuta el algoritmo de Dijkstra para encontrar el camino menos costoso
     *
     * @param origen  nodo de origen
     * @param destino nodo de destino
     * @return El costo total y el recorrido de los nodos
     */
    public CaminoMinimoResponse calcularCaminoMinimo(int origen, int destino) {
        validarPuntosExistentes(origen, destino);

        if (origen == destino) {
            return new CaminoMinimoResponse(0, List.of(obtenerNombrePuntoDeVenta(origen)));
        }

        Map<Integer, Integer> distancias = inicializarDistancias(origen);
        Map<Integer, Integer> predecesores = new HashMap<>();
        Set<Integer> visitados = new HashSet<>();

        PriorityQueue<Costo> cola = crearCola(origen);

        while (!cola.isEmpty()) {
            Costo actual = cola.poll();
            if (visitados.contains(actual.id()))
                continue;
            visitados.add(actual.id());
            if (actual.id() == destino)
                break;

            actualizarDistancias(actual, distancias, predecesores, cola);
        }

        if (!hayCamino(distancias, destino))
            throw new NotFoundException(Constants.COSTOS_CAMINO_NOT_FOUND);

        List<String> recorrido = reconstruirRecorrido(origen, destino, predecesores);
        return new CaminoMinimoResponse(distancias.get(destino), recorrido);
    }

    /**
     * Obtiene un punto de venta origen con vecinos con los que tiene un costo
     *
     * @param origen el punto de venta de origen
     * @return Los vecinos asociados con sus costos
     */
    public CostosOrigenResponse getVecinos(int origen) {
        String pdvNombre = puntoDeVentaCache
                                .findById(origen)
                                .map(PuntoDeVenta::nombre)
                                .orElseThrow(() -> new NotFoundException(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION));

        Map<Integer, Integer> vecinos = costosCache.getVecinos(origen);

        List<Vecino> origenVecinos = vecinos
                                        .entrySet()
                                        .stream()
                                        .map(entry -> {
                                            int id = entry.getKey();
                                            int costo = entry.getValue();
                                            String nombre = obtenerNombrePuntoDeVenta(id);

                                            return new Vecino(id, nombre, costo);
                                        })
                                        .toList();

        return new CostosOrigenResponse(pdvNombre, origenVecinos);
    }

    private Map<Integer, Integer> inicializarDistancias(int origen) {
        Map<Integer, Integer> distancias = new HashMap<>();
        costosCache
            .getGraph()
            .keySet()
            .forEach(nodo -> distancias.put(nodo, Integer.MAX_VALUE));

        distancias.put(origen, 0);
        return distancias;
    }

    private PriorityQueue<Costo> crearCola(int origen) {
        PriorityQueue<Costo> cola = new PriorityQueue<>(Comparator.comparingInt(Costo::costo));
        cola.add(new Costo(origen, 0));
        return cola;
    }

    private void actualizarDistancias(Costo nodoActual, Map<Integer, Integer> distancias,
                                      Map<Integer, Integer> predecesores, PriorityQueue<Costo> cola) {
        Map<Integer, Integer> vecinos = costosCache.getVecinos(nodoActual.id());
        vecinos.forEach((id, costo) -> {
            int nuevoCosto = distancias.get(nodoActual.id()) + costo;

            if (nuevoCosto < distancias.get(id)) {
                distancias.put(id, nuevoCosto);
                predecesores.put(id, nodoActual.id());
                cola.add(new Costo(id, nuevoCosto));
            }
        });
    }

    private boolean hayCamino(Map<Integer, Integer> distancias, int destino) {
        return distancias.containsKey(destino) && distancias.get(destino) != Integer.MAX_VALUE;
    }

    private List<String> reconstruirRecorrido(int origen, int destino, Map<Integer, Integer> predecesores) {
        List<String> recorrido = new ArrayList<>();
        int nodoActual = destino;

        while (nodoActual != origen) {
            String nombre = obtenerNombrePuntoDeVenta(nodoActual);

            recorrido.add(nombre);
            nodoActual = predecesores.get(nodoActual);
        }

        recorrido.add(obtenerNombrePuntoDeVenta(origen));
        Collections.reverse(recorrido);
        return recorrido;
    }

    private String obtenerNombrePuntoDeVenta(int pdvId) {
        return puntoDeVentaCache
                .findById(pdvId)
                .map(PuntoDeVenta::nombre)
                .orElse(Constants.PUNTO_DE_VENTA_UNKNOWN);
    }

    private void validarPuntosExistentes(int origen, int destino) {
        validarPuntoExistente(origen);
        validarPuntoExistente(destino);
    }

    private void validarPuntoExistente(int id) {
        puntoDeVentaCache
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION));
    }

    private void validarCostoDirecto(int origen, int destino, int costo) {
        if (origen == destino) {
            throw new BadRequestException(Constants.COSTO_SELF_LOOP_EXCEPTION);
        }

        if (costo <= 0) {
            throw new BadRequestException(Constants.COSTO_INVALID_EXCEPTION);
        }
    }
}
