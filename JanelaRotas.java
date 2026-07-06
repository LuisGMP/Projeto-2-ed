package com.mycompany.projeto2;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JanelaRotas extends JFrame
  {
 
    private final Grafo grafo;
 
    private JComboBox<String> comboOrigem;
    private JComboBox<String> comboDestino;
    private Painelmapa painelMapa;
    private DefaultListModel<BuscadorRotas.Caminho> modeloRotas;
    private JList<BuscadorRotas.Caminho> listaRotas;
    private DefaultListModel<String> modeloInstrucoes;
    private JList<String> listaInstrucoes;
    private JLabel labelResumoRota;
 
    private double menorCustoAtual = Double.POSITIVE_INFINITY;
 
    private static final Color COR_ROTA = new Color(25, 110, 235);
    private static final Color COR_FUNDO_CARD_SELECIONADO = new Color(228, 238, 253);
 
    public JanelaRotas(Grafo grafo) {
        super("Sistema de Logística — Navegação de Rotas");
        this.grafo = grafo;
        montarInterface();
    }
 
    private void montarInterface() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 640);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));
 
        add(criarPainelSelecao(), BorderLayout.NORTH);
        add(criarPainelLateral(), BorderLayout.WEST);
 
        painelMapa = new Painelmapa(grafo);
        JPanel envoltorioMapa = new JPanel(new BorderLayout());
        envoltorioMapa.setBorder(new EmptyBorder(8, 4, 8, 8));
        envoltorioMapa.add(painelMapa, BorderLayout.CENTER);
        add(envoltorioMapa, BorderLayout.CENTER);
 
        buscarEExibir();
    }
 
    private JPanel criarPainelSelecao() {
        List<String> vertices = new ArrayList<>(grafo.getVertices());
        Collections.sort(vertices);
 
        comboOrigem = new JComboBox<>(vertices.toArray(new String[0]));
        comboDestino = new JComboBox<>(vertices.toArray(new String[0]));
        if (vertices.size() > 1) {
            comboDestino.setSelectedIndex(vertices.size() - 1);
        }
 
        JButton botaoBuscar = new JButton("Buscar rotas");
        botaoBuscar.addActionListener(e -> buscarEExibir());
 
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painel.add(new JLabel("Origem:"));
        painel.add(comboOrigem);
        painel.add(new JLabel("Destino:"));
        painel.add(comboDestino);
        painel.add(botaoBuscar);
        return painel;
    }
 
    /** Painel lateral esquerdo: lista de rotas (cards) + instruções passo a passo da rota selecionada. */
    private JPanel criarPainelLateral() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setPreferredSize(new Dimension(320, 0));
        painel.setBorder(new EmptyBorder(0, 8, 8, 4));
 
        // --- Lista de rotas disponíveis (estilo cards de app de navegação) ---
        modeloRotas = new DefaultListModel<>();
        listaRotas = new JList<>(modeloRotas);
        listaRotas.setCellRenderer(new RenderizadorCardRota());
        listaRotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaRotas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selecionarRota();
        });
 
        JPanel painelTituloRotas = new JPanel(new BorderLayout());
        JLabel tituloRotas = new JLabel("Rotas disponíveis");
        tituloRotas.setFont(tituloRotas.getFont().deriveFont(Font.BOLD, 13f));
        tituloRotas.setBorder(new EmptyBorder(4, 4, 4, 4));
        painelTituloRotas.add(tituloRotas, BorderLayout.WEST);
 
        JPanel blocoRotas = new JPanel(new BorderLayout());
        blocoRotas.add(painelTituloRotas, BorderLayout.NORTH);
        blocoRotas.add(new JScrollPane(listaRotas), BorderLayout.CENTER);
        blocoRotas.setPreferredSize(new Dimension(320, 230));
 
        // --- Instruções passo a passo da rota selecionada ---
        modeloInstrucoes = new DefaultListModel<>();
        listaInstrucoes = new JList<>(modeloInstrucoes);
        listaInstrucoes.setCellRenderer(new RenderizadorPasso());
 
        labelResumoRota = new JLabel(" ");
        labelResumoRota.setFont(labelResumoRota.getFont().deriveFont(Font.BOLD, 13f));
        labelResumoRota.setBorder(new EmptyBorder(6, 4, 6, 4));
 
        JPanel blocoInstrucoes = new JPanel(new BorderLayout());
        blocoInstrucoes.add(labelResumoRota, BorderLayout.NORTH);
        blocoInstrucoes.add(new JScrollPane(listaInstrucoes), BorderLayout.CENTER);
 
        painel.add(blocoRotas, BorderLayout.NORTH);
        painel.add(blocoInstrucoes, BorderLayout.CENTER);
        return painel;
    }
 
    private void buscarEExibir() {
        String origem = (String) comboOrigem.getSelectedItem();
        String destino = (String) comboDestino.getSelectedItem();
 
        modeloRotas.clear();
        modeloInstrucoes.clear();
        painelMapa.definirRotaDestacada(null);
        labelResumoRota.setText(" ");
 
        if (origem == null || destino == null || origem.equals(destino)) {
            labelResumoRota.setText("Escolha uma origem e um destino diferentes.");
            return;
        }
 
        List<BuscadorRotas.Caminho> rotas = BuscadorRotas.buscarTodasRotas(grafo, origem, destino);
        if (rotas.isEmpty()) {
            labelResumoRota.setText("Nenhuma rota encontrada entre " + origem + " e " + destino + ".");
            return;
        }
 
        menorCustoAtual = rotas.get(0).custo; // já vem ordenado (mais barata primeiro)
        for (BuscadorRotas.Caminho c : rotas) {
            modeloRotas.addElement(c);
        }
 
        listaRotas.setSelectedIndex(0); // seleciona automaticamente a rota recomendada
    }
 
    /** Chamado quando o usuário clica em uma rota da lista: destaca no mapa e mostra as instruções. */
    private void selecionarRota() {
        BuscadorRotas.Caminho caminho = listaRotas.getSelectedValue();
        if (caminho == null) return;
 
        painelMapa.definirRotaDestacada(caminho.pontos);
        exibirInstrucoes(caminho);
    }
 
    private void exibirInstrucoes(BuscadorRotas.Caminho caminho) {
        modeloInstrucoes.clear();
 
        boolean recomendada = caminho.custo == menorCustoAtual;
        labelResumoRota.setText(String.format(
                "Custo total: %.2f%s", caminho.custo, recomendada ? "  ★ recomendada" : ""));
 
        List<String> pontos = caminho.pontos;
        modeloInstrucoes.addElement("Partida: " + pontos.get(0));
 
        double acumulado = 0;
        for (int i = 0; i < pontos.size() - 1; i++) {
            String origem = pontos.get(i);
            String destino = pontos.get(i + 1);
            Double peso = grafo.getPesoAresta(origem, destino);
            double custoTrecho = (peso == null) ? 0 : peso;
            acumulado += custoTrecho;
            modeloInstrucoes.addElement(String.format(
                    "Siga até %s — custo %.1f (acumulado %.1f)", destino, custoTrecho, acumulado));
        }
 
        modeloInstrucoes.addElement("Chegada: " + pontos.get(pontos.size() - 1));
    }
 
    /** Renderiza cada rota da lista como um "card": rota resumida + custo, destacando a recomendada. */
    private class RenderizadorCardRota extends JPanel implements ListCellRenderer<BuscadorRotas.Caminho> {
        private final JLabel labelTitulo = new JLabel();
        private final JLabel labelDetalhe = new JLabel();
 
        RenderizadorCardRota() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(8, 10, 8, 10));
 
            JPanel textos = new JPanel();
            textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
            textos.setOpaque(false);
            textos.add(labelTitulo);
            textos.add(labelDetalhe);
            add(textos, BorderLayout.CENTER);
        }
 
        @Override
        public Component getListCellRendererComponent(JList<? extends BuscadorRotas.Caminho> list,
                BuscadorRotas.Caminho valor, int index, boolean selecionado, boolean temFoco) {
 
            boolean recomendada = valor.custo == menorCustoAtual;
 
            labelTitulo.setText("Rota " + (index + 1) + (recomendada ? "  ★ recomendada" : ""));
            labelTitulo.setFont(labelTitulo.getFont().deriveFont(Font.BOLD, 12f));
            labelTitulo.setForeground(recomendada ? COR_ROTA.darker() : Color.DARK_GRAY);
 
            labelDetalhe.setText(String.format("<html>Custo: %.2f<br>Via: %s</html>",
                    valor.custo, resumirTrajeto(valor.pontos)));
            labelDetalhe.setFont(labelDetalhe.getFont().deriveFont(Font.PLAIN, 11f));
            labelDetalhe.setForeground(Color.GRAY);
 
            setBackground(selecionado ? COR_FUNDO_CARD_SELECIONADO : Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                    new EmptyBorder(8, 10, 8, 10)));
 
            return this;
        }
 
        private String resumirTrajeto(List<String> pontos) {
            if (pontos.size() <= 3) return String.join(" → ", pontos);
            return pontos.get(0) + " → ... → " + pontos.get(pontos.size() - 1)
                    + " (" + pontos.size() + " pontos)";
        }
    }
 
    /** Renderiza cada instrução como um item simples com marcador. */
    private static class RenderizadorPasso extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object valor, int index,
                boolean selecionado, boolean temFoco) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, valor, index, selecionado, temFoco);
            label.setBorder(new EmptyBorder(5, 8, 5, 8));
            label.setFont(label.getFont().deriveFont(12f));
            return label;
        }
    }
 
    /** Abre a janela na thread de eventos do Swing (forma correta de iniciar uma UI Swing). */
    public static void abrir(Grafo grafo) {
        SwingUtilities.invokeLater(() -> new JanelaRotas(grafo).setVisible(true));
    }
}
