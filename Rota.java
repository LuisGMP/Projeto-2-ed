package com.mycompany.projeto2;

import java.util.List;

/**
 * Representa uma rota concreta: uma sequência ordenada de pontos (vértices)
 * por onde o caminhão de coleta passa. Normalmente usada para representar a
 * "rota atual" (a que o motorista já faz na prática, sem otimização),
 * para depois comparar seu custo com o da rota otimizada pelo Dijkstra.
 */
public class Rota {

    private final List<String> pontos;

    public Rota(List<String> pontos) {
        if (pontos == null || pontos.size() < 2) {
            throw new IllegalArgumentException("Uma rota precisa ter pelo menos 2 pontos.");
        }
        this.pontos = pontos;
    }

    public List<String> getPontos() {
        return pontos;
    }

    /**
     * Calcula o custo total da rota, somando o peso de cada trecho
     * (pontos.get(i) -> pontos.get(i+1)) segundo as arestas do grafo.
     *
     * Lança exceção se algum trecho da rota não existir de fato no grafo
     * (ex.: rota manual passando por uma via que não existe).
     */
    public double calcularCusto(Grafo grafo) {
        double custoTotal = 0.0;

        for (int i = 0; i < pontos.size() - 1; i++) {
            String origem = pontos.get(i);
            String destino = pontos.get(i + 1);

            Double peso = grafo.getPesoAresta(origem, destino);
            if (peso == null) {
                throw new IllegalStateException(
                    "Não existe via direta de \"" + origem + "\" para \"" + destino + "\" no grafo."
                );
            }
            custoTotal += peso;
        }

        return custoTotal;
    }

    @Override
    public String toString() {
        return String.join(" -> ", pontos);
    }
}
