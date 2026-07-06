package com.mycompany.projeto2;
public class Aresta 
{

    private final String destino;
    private final double peso;

    public Aresta(String destino, double peso) {
        if (peso < 0) {
            throw new IllegalArgumentException("Peso da aresta não pode ser negativo: " + peso);
        }
        this.destino = destino;
        this.peso = peso;
    }

    public String getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }

    @Override
    public String toString() {
        return "-> " + destino + " (custo: " + peso + ")";
    }
}
