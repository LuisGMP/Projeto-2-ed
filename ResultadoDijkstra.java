package com.mycompany.projeto2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Resultado de uma execução do Dijkstra a partir de um vértice de origem:
 * a menor distância até cada vértice alcançável, e o predecessor de cada um
 * no caminho mínimo (usado para reconstruir a rota completa).
 */
public class ResultadoDijkstra {

    private final Map<String, Double> distancias;
    private final Map<String, String> predecessores;
    private final String origem;

    public ResultadoDijkstra(String origem, Map<String, Double> distancias, Map<String, String> predecessores) {
        this.origem = origem;
        this.distancias = distancias;
        this.predecessores = predecessores;
    }

    /** Custo do menor caminho da origem até o destino, ou infinito se inalcançável. */
    public double getDistancia(String destino) {
        return distancias.getOrDefault(destino, Double.POSITIVE_INFINITY);
    }

    public boolean isAlcancavel(String destino) {
        return distancias.containsKey(destino) && distancias.get(destino) < Double.POSITIVE_INFINITY;
    }

    /** Reconstrói a sequência de vértices do caminho mínimo até o destino. */
    public List<String> reconstruirCaminho(String destino) {
        List<String> caminho = new ArrayList<>();

        if (!isAlcancavel(destino) && !destino.equals(origem)) {
            return caminho; // caminho vazio: destino inalcançável
        }

        String atual = destino;
        while (atual != null) {
            caminho.add(atual);
            if (atual.equals(origem)) break;
            atual = predecessores.get(atual);
        }
        Collections.reverse(caminho);
        return caminho;
    }

    public Map<String, Double> getTodasDistancias() {
        return distancias;
    }
}
