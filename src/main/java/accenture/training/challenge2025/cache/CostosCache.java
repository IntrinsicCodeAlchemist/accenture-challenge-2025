package accenture.training.challenge2025.cache;

import accenture.training.challenge2025.exception.BadRequestException;
import accenture.training.challenge2025.constants.Constants;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
public class CostosCache {
    private final Map<Integer, Map<Integer, Integer>> graph = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() { initDefaultData(); }

    public void addCosto(int a, int b, int costo) {
        if (costo <= 0) throw new BadRequestException(Constants.COSTO_INVALID_EXCEPTION);
        if (a == b) throw new BadRequestException(Constants.COSTO_SELF_LOOP_EXCEPTION);

        graph.computeIfAbsent(a, x -> new ConcurrentHashMap<>()).put(b, costo);
        graph.computeIfAbsent(b, x -> new ConcurrentHashMap<>()).put(a, costo);
    }

    public void removeCosto(int a, int b) {
        Optional.ofNullable(graph.get(a)).ifPresent(map -> map.remove(b));
        Optional.ofNullable(graph.get(b)).ifPresent(map -> map.remove(a));
    }

    public void removePuntoDeVenta(int id) {
        graph.remove(id);
        graph.values().forEach(vecinos -> vecinos.remove(id));
    }

    public Map<Integer, Integer> getVecinos(int a) { return graph.getOrDefault(a, Map.of()); }

    public void initDefaultData() {
        addCosto(1, 2, 2);
        addCosto(1, 3, 3);
        addCosto(2, 3, 5);
        addCosto(2, 4, 10);
        addCosto(1, 4, 11);
        addCosto(4, 5, 5);
        addCosto(2, 5, 14);
        addCosto(6, 7, 32);
        addCosto(8, 9, 11);
        addCosto(10, 7, 5);
        addCosto(3, 8, 10);
        addCosto(5, 8, 30);
        addCosto(10, 5, 5);
        addCosto(4, 6, 6);
    }
}
