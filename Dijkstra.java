package com.mycompany.projeto2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Implementação do algoritmo de Dijkstra: encontra o caminho de menor custo
 * de um vértice de origem até todos os demais vértices do grafo.
 *
 * Complexidade: O((V + E) log V) usando uma fila de prioridade (min-heap).
 *
 * Pré-condição: pesos das arestas não-negativos (garantido em Aresta).
 */
public class Dijkstra {

    // Par (vértice, distância acumulada) usado dentro da fila de prioridade
    private static class No {
        final String vertice;
        final double distancia;

        No(String vertice, double distancia) {
            this.vertice = vertice;
            this.distancia = distancia;
        }
    }

    public static ResultadoDijkstra calcular(Grafo grafo, String origem) {
        if (!grafo.contemVertice(origem)) {
            throw new IllegalArgumentException("Vértice de origem não existe no grafo: " + origem);
        }

        Map<String, Double> distancias = new HashMap<>();
        Map<String, String> predecessores = new HashMap<>();
        Set<String> visitados = new HashSet<>();

        for (String v : grafo.getVertices()) {
            distancias.put(v, Double.POSITIVE_INFINITY);
        }
        distancias.put(origem, 0.0);

        PriorityQueue<No> fila = new PriorityQueue<>((a, b) -> Double.compare(a.distancia, b.distancia));
        fila.add(new No(origem, 0.0));

        while (!fila.isEmpty()) {
            No atual = fila.poll();

            if (visitados.contains(atual.vertice)) continue;
            visitados.add(atual.vertice);

            for (Aresta aresta : grafo.getVizinhos(atual.vertice)) {
                String vizinho = aresta.getDestino();
                if (visitados.contains(vizinho)) continue;

                double novaDistancia = distancias.get(atual.vertice) + aresta.getPeso();
                if (novaDistancia < distancias.getOrDefault(vizinho, Double.POSITIVE_INFINITY)) {
                    distancias.put(vizinho, novaDistancia);
                    predecessores.put(vizinho, atual.vertice);
                    fila.add(new No(vizinho, novaDistancia));
                }
            }
        }

        return new ResultadoDijkstra(origem, distancias, predecessores);
    }

    /** Atalho: já retorna diretamente o custo mínimo origem -> destino. */
    public static double custoMinimo(Grafo grafo, String origem, String destino) {
        return calcular(grafo, origem).getDistancia(destino);
    }
}
