package com.mycompany.projeto2;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BuscadorRotas {
 
    /** Uma rota concreta encontrada pela busca, com sua sequência de pontos e custo total. */
    public static class Caminho implements Comparable<Caminho> {
        public final List<String> pontos;
        public final double custo;
 
        Caminho(List<String> pontos, double custo) {
            this.pontos = pontos;
            this.custo = custo;
        }
 
        @Override
        public int compareTo(Caminho outro) {
            return Double.compare(this.custo, outro.custo);
        }
 
        @Override
        public String toString() {
            return String.join(" -> ", pontos) + " (custo: " + custo + ")";
        }
    }
 
    /**
     * Retorna todas as rotas simples de origem até destino, ordenadas da
     * mais barata para a mais cara.
     */
    public static List<Caminho> buscarTodasRotas(Grafo grafo, String origem, String destino) {
        List<Caminho> resultados = new ArrayList<>();
 
        if (!grafo.contemVertice(origem) || !grafo.contemVertice(destino)) {
            return resultados;
        }
 
        Set<String> visitados = new LinkedHashSet<>();
        List<String> caminhoAtual = new ArrayList<>();
        dfs(grafo, origem, destino, visitados, caminhoAtual, 0.0, resultados);
 
        Collections.sort(resultados);
        return resultados;
    }
 
    private static void dfs(Grafo grafo, String atual, String destino, Set<String> visitados,
                             List<String> caminhoAtual, double custoAcumulado, List<Caminho> resultados) {
 
        visitados.add(atual);
        caminhoAtual.add(atual);
 
        if (atual.equals(destino)) {
            resultados.add(new Caminho(new ArrayList<>(caminhoAtual), custoAcumulado));
        } else {
            for (Aresta aresta : grafo.getVizinhos(atual)) {
                String vizinho = aresta.getDestino();
                if (!visitados.contains(vizinho)) {
                    dfs(grafo, vizinho, destino, visitados, caminhoAtual,
                            custoAcumulado + aresta.getPeso(), resultados);
                }
            }
        }
 
        // backtrack: libera o vértice para que outros caminhos possam usá-lo
        visitados.remove(atual);
        caminhoAtual.remove(caminhoAtual.size() - 1);
    }
}
