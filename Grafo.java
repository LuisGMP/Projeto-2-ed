package com.mycompany.projeto2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Grafo {

    private final Map<String, List<Aresta>> adjacencias = new LinkedHashMap<>();

    /** Adiciona um vértice (ponto) ao grafo, se ainda não existir. */
    public void adicionarVertice(String vertice) {
        adjacencias.putIfAbsent(vertice, new ArrayList<>());
    }

    /**
     * Adiciona uma aresta entre origem e destino.
     * @param bidirecional se true, cria a via nos dois sentidos (rua de mão dupla).
     */
    public void adicionarAresta(String origem, String destino, double peso, boolean bidirecional) {
        adicionarVertice(origem);
        adicionarVertice(destino);

        adjacencias.get(origem).add(new Aresta(destino, peso));
        if (bidirecional) {
            adjacencias.get(destino).add(new Aresta(origem, peso));
        }
    }

    /** Sobrecarga: por padrão as vias são bidirecionais (mão dupla). */
    public void adicionarAresta(String origem, String destino, double peso) {
        adicionarAresta(origem, destino, peso, true);
    }

    public List<Aresta> getVizinhos(String vertice) {
        return adjacencias.getOrDefault(vertice, new ArrayList<>());
    }

    public Set<String> getVertices() {
        return adjacencias.keySet();
    }

    public boolean contemVertice(String vertice) {
        return adjacencias.containsKey(vertice);
    }

    /**
     * Retorna o peso da aresta origem -> destino, se existir.
     * Usado pela classe Rota para calcular o custo de um caminho manual.
     */
    public Double getPesoAresta(String origem, String destino) {
        for (Aresta a : getVizinhos(origem)) {
            if (a.getDestino().equals(destino)) {
                return a.getPeso();
            }
        }
        return null; // não existe via direta entre origem e destino
    }

    public void imprimir() {
        for (Map.Entry<String, List<Aresta>> entry : adjacencias.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
}
