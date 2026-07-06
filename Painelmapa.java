package com.mycompany.projeto2;
 
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Painel que desenha o grafo como um "mapa": pontos (vértices) posicionados
 * em círculo, vias (arestas) como linhas cinzas com o peso escrito, e a rota
 * escolhida destacada em azul, mais grossa, com setas indicando o sentido —
 * parecido com a forma que apps de GPS destacam o trajeto sobre o mapa.
 */
public class Painelmapa extends JPanel {
 
    private final Grafo grafo;
    private List<String> rotaDestacada = new ArrayList<>();
    private final Map<String, Point> posicoes = new LinkedHashMap<>();
 
    private static final Color COR_VIA = new Color(215, 215, 215);
    private static final Color COR_PESO = new Color(140, 140, 140);
    private static final Color COR_ROTA = new Color(25, 110, 235);
    private static final Color COR_NO_BORDA = new Color(120, 120, 120);
    private static final Color COR_TEXTO = new Color(60, 60, 60);
    private static final Color COR_ORIGEM = new Color(34, 170, 80);
    private static final Color COR_DESTINO = new Color(220, 55, 55);
 
    private static final int RAIO_NO = 16;
 
    public Painelmapa(Grafo grafo) {
        this.grafo = grafo;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(520, 460));
    }
 
    /** Define qual rota (sequência de pontos) deve ser destacada no mapa. */
    public void definirRotaDestacada(List<String> rota) {
        this.rotaDestacada = (rota == null) ? new ArrayList<>() : rota;
        repaint();
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 
        atualizarPosicoes();
        desenharVias(g2);
        desenharRotaDestacada(g2);
        desenharPontos(g2);
    }
 
    /** Distribui os vértices em círculo, proporcional ao tamanho atual do painel. */
    private void atualizarPosicoes() {
        List<String> vertices = new ArrayList<>(grafo.getVertices());
        int n = vertices.size();
        if (n == 0) return;
 
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int raio = Math.max(60, Math.min(getWidth(), getHeight()) / 2 - 55);
 
        posicoes.clear();
        for (int i = 0; i < n; i++) {
            double angulo = (2 * Math.PI * i / n) - (Math.PI / 2);
            int x = (int) (cx + raio * Math.cos(angulo));
            int y = (int) (cy + raio * Math.sin(angulo));
            posicoes.put(vertices.get(i), new Point(x, y));
        }
    }
 
    /** Desenha todas as vias do grafo em cinza claro, com o peso no meio. */
    private void desenharVias(Graphics2D g2) {
        Set<String> jaDesenhadas = new HashSet<>();
        g2.setFont(g2.getFont().deriveFont(11f));
 
        for (String origem : grafo.getVertices()) {
            Point p1 = posicoes.get(origem);
            if (p1 == null) continue;
 
            for (Aresta aresta : grafo.getVizinhos(origem)) {
                String destino = aresta.getDestino();
                String chave = origem.compareTo(destino) < 0 ? origem + "|" + destino : destino + "|" + origem;
                if (!jaDesenhadas.add(chave)) continue;
 
                Point p2 = posicoes.get(destino);
                if (p2 == null) continue;
 
                g2.setColor(COR_VIA);
                g2.setStroke(new BasicStroke(2.2f));
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
 
                int mx = (p1.x + p2.x) / 2;
                int my = (p1.y + p2.y) / 2;
                g2.setColor(COR_PESO);
                g2.drawString(formatarPeso(aresta.getPeso()), mx + 4, my - 4);
            }
        }
    }
 
    /** Desenha a rota escolhida por cima do mapa: linha azul grossa + setas de sentido. */
    private void desenharRotaDestacada(Graphics2D g2) {
        if (rotaDestacada.size() < 2) return;
 
        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(COR_ROTA);
 
        for (int i = 0; i < rotaDestacada.size() - 1; i++) {
            Point p1 = posicoes.get(rotaDestacada.get(i));
            Point p2 = posicoes.get(rotaDestacada.get(i + 1));
            if (p1 == null || p2 == null) continue;
 
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            desenharSeta(g2, p1, p2);
        }
    }
 
    /** Desenha uma pequena seta no meio do trecho, apontando o sentido do percurso. */
    private void desenharSeta(Graphics2D g2, Point p1, Point p2) {
        double angulo = Math.atan2(p2.y - p1.y, p2.x - p1.x);
        int mx = (p1.x + p2.x) / 2;
        int my = (p1.y + p2.y) / 2;
        int tamanho = 11;
 
        Polygon seta = new Polygon();
        seta.addPoint((int) (mx + tamanho * Math.cos(angulo)), (int) (my + tamanho * Math.sin(angulo)));
        seta.addPoint((int) (mx - tamanho * Math.cos(angulo - Math.PI / 7)), (int) (my - tamanho * Math.sin(angulo - Math.PI / 7)));
        seta.addPoint((int) (mx - tamanho * Math.cos(angulo + Math.PI / 7)), (int) (my - tamanho * Math.sin(angulo + Math.PI / 7)));
 
        g2.setColor(COR_ROTA.darker());
        g2.fillPolygon(seta);
    }
 
    /** Desenha os pontos (vértices): origem em verde, destino em vermelho, resto neutro. */
    private void desenharPontos(Graphics2D g2) {
        String origem = rotaDestacada.isEmpty() ? null : rotaDestacada.get(0);
        String destino = rotaDestacada.isEmpty() ? null : rotaDestacada.get(rotaDestacada.size() - 1);
 
        for (Map.Entry<String, Point> entrada : posicoes.entrySet()) {
            String nome = entrada.getKey();
            Point p = entrada.getValue();
            boolean naRota = rotaDestacada.contains(nome);
 
            Color preenchimento;
            if (nome.equals(origem)) preenchimento = COR_ORIGEM;
            else if (nome.equals(destino)) preenchimento = COR_DESTINO;
            else if (naRota) preenchimento = COR_ROTA;
            else preenchimento = Color.WHITE;
 
            g2.setColor(preenchimento);
            g2.fillOval(p.x - RAIO_NO, p.y - RAIO_NO, RAIO_NO * 2, RAIO_NO * 2);
 
            g2.setColor(naRota ? COR_ROTA.darker() : COR_NO_BORDA);
            g2.setStroke(new BasicStroke(naRota ? 2.5f : 1.5f));
            g2.drawOval(p.x - RAIO_NO, p.y - RAIO_NO, RAIO_NO * 2, RAIO_NO * 2);
 
            g2.setColor(COR_TEXTO);
            g2.setFont(g2.getFont().deriveFont(naRota ? Font.BOLD : Font.PLAIN, 12f));
            FontMetrics fm = g2.getFontMetrics();
            int larguraTexto = fm.stringWidth(nome);
            g2.drawString(nome, p.x - larguraTexto / 2, p.y + RAIO_NO + 16);
        }
    }
 
    private String formatarPeso(double peso) {
        return (peso == Math.floor(peso)) ? String.valueOf((long) peso) : String.format("%.1f", peso);
    }
}
