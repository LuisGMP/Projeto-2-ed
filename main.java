package com.mycompany.projeto2;
 
import java.util.Arrays;
 
public class Main {
 
    public static void main(String[] args) {
        Grafo grafo = new Grafo();
 
        // Malha viária de exemplo: pontos de coleta / cruzamentos de um bairro.
        // Peso da via representando, por exemplo, tempo estimado em minutos.
        grafo.adicionarAresta("Garagem", "PontoA", 5);
        grafo.adicionarAresta("PontoA", "PontoB", 4);
        grafo.adicionarAresta("PontoB", "PontoC", 3);
        grafo.adicionarAresta("Garagem", "PontoC", 15);   // via alternativa mais longa
        grafo.adicionarAresta("PontoA", "PontoD", 10);
        grafo.adicionarAresta("PontoD", "PontoC", 2);
        grafo.adicionarAresta("PontoC", "Aterro", 6);
        grafo.adicionarAresta("PontoB", "Aterro", 9);
 
        // Rota "atual": a que o caminhão faz hoje, sem otimização,
        // passando pela via alternativa mais longa (Garagem -> PontoC direto).
        Rota rotaAtual = new Rota(Arrays.asList("Garagem", "PontoC", "Aterro"));
 
        // Comparação no console (continua disponível para depuração/relatórios).
        ComparadorRotas.ResultadoComparacao resultado =
                ComparadorRotas.comparar(grafo, rotaAtual, "Garagem", "Aterro");
        resultado.imprimir();
 
        // Abre a janela gráfica mostrando todas as rotas disponíveis
        // entre os pontos escolhidos, com a melhor destacada.
        JanelaRotas.abrir(grafo);
    }
}
 
