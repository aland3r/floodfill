import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

/**
 * Swing mínimo: {@code entrada.png}, 4 cores, pilha/fila; frames {@code anim_frame_*} em sequência única para animação.
 */
public final class FloodFillSwingApp {

    private static final String ARQUIVO_CURSOR_BALDE = "paint-bucket-icon.png";

    /** Cursor de balde (PNG); hotspot na ponta da gota (canto inferior direito do ícone). */
    private static final Cursor CURSOR_BALDE_TINTA = criarCursorBaldeTinta();

    private static Cursor criarCursorBaldeTinta() {
        BufferedImage src = carregarImagemCursorBalde();
        if (src == null) {
            return criarCursorBaldeTintaFallback();
        }
        BufferedImage bi = garantirArgbParaCursor(src);
        final int target = 32;
        if (bi.getWidth() != target || bi.getHeight() != target) {
            BufferedImage scaled = new BufferedImage(target, target, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(bi, 0, 0, target, target, null);
            g.dispose();
            bi = scaled;
        }
        int tw = bi.getWidth();
        int th = bi.getHeight();
        // Ponta da gota junto ao canto inferior direito (ícone paint-bucket).
        int hx = Math.max(0, tw - 2);
        int hy = Math.max(0, th - 2);
        try {
            return Toolkit.getDefaultToolkit().createCustomCursor(bi, new Point(hx, hy), "baldeTinta");
        } catch (Exception e) {
            return criarCursorBaldeTintaFallback();
        }
    }

    private static BufferedImage garantirArgbParaCursor(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_ARGB) {
            return src;
        }
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return out;
    }

    private static BufferedImage carregarImagemCursorBalde() {
        try (InputStream in = FloodFillSwingApp.class.getResourceAsStream(ARQUIVO_CURSOR_BALDE)) {
            if (in != null) {
                return ImageIO.read(in);
            }
        } catch (IOException ignored) {
        }
        try {
            java.security.CodeSource cs = FloodFillSwingApp.class.getProtectionDomain().getCodeSource();
            if (cs != null) {
                URL loc = cs.getLocation();
                if ("file".equals(loc.getProtocol())) {
                    File dir = new File(loc.toURI());
                    if (dir.isDirectory()) {
                        File png = new File(dir, ARQUIVO_CURSOR_BALDE);
                        if (png.isFile()) {
                            return ImageIO.read(png);
                        }
                        File pai = dir.getParentFile();
                        if (pai != null) {
                            File pngPastaPai = new File(pai, ARQUIVO_CURSOR_BALDE);
                            if (pngPastaPai.isFile()) {
                                return ImageIO.read(pngPastaPai);
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        String base = System.getProperty("user.dir", ".");
        String[] candidatos = {
                "src" + File.separator + ARQUIVO_CURSOR_BALDE,
                ARQUIVO_CURSOR_BALDE,
                "FloodFill" + File.separator + ARQUIVO_CURSOR_BALDE,
                ".." + File.separator + ARQUIVO_CURSOR_BALDE,
                base + File.separator + "src" + File.separator + ARQUIVO_CURSOR_BALDE,
                base + File.separator + ARQUIVO_CURSOR_BALDE
        };
        for (String rel : candidatos) {
            File f = new File(rel);
            if (f.isFile()) {
                try {
                    return ImageIO.read(f);
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    private static Cursor criarCursorBaldeTintaFallback() {
        int w = 32;
        int h = 32;
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, w, h);
        g.setComposite(AlphaComposite.SrcOver);
        g.setColor(new Color(75, 78, 90));
        int[] bx = {7, 7, 9, 23, 25, 25, 23};
        int[] by = {8, 22, 25, 27, 25, 9, 7};
        g.fillPolygon(bx, by, bx.length);
        g.setColor(new Color(40, 42, 48));
        g.setStroke(new BasicStroke(1.2f));
        g.drawPolygon(bx, by, bx.length);
        g.setColor(new Color(90, 130, 240));
        g.fillPolygon(new int[]{9, 23, 22, 10}, new int[]{12, 12, 21, 21}, 4);
        g.setColor(new Color(55, 58, 65));
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(17, 3, 12, 11, 15, 130);
        g.dispose();
        try {
            return Toolkit.getDefaultToolkit().createCustomCursor(bi, new Point(5, 27), "baldeTinta");
        } catch (Exception e) {
            return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
        }
    }

    private static final String ARQUIVO_ENTRADA = "entrada.png";
    /** Menos frames = mais rápido (CLI / modo sem quadros fixos). */
    private static final int PASSO_FRAME = 20;
    /** Cada preenchimento grava exatamente estes PNGs; o último fecha a região (apresentação). */
    private static final int QUADROS_ANIMACAO_APRESENTACAO = 188;

    private static final Color[] CORES = {
            new Color(114, 233, 185),
            new Color(143, 179, 255),
            new Color(255, 235, 132),
            new Color(255, 159, 196)
    };

    private BufferedImage original;
    private BufferedImage trabalho;
    private final ImageIOService io = new ImageIOService();
    private final FloodFillService flood = new FloodFillService(io);

    private int indiceCor = 0;

    private final PainelImagem painel = new PainelImagem();
    private final JLabel lblCoords = new JLabel(" ");
    private final JButton[] botoesCor = new JButton[CORES.length];

    private JButton btnPilha;
    private JButton btnFila;
    private JButton btnReset;
    private JButton btnAnimacao;
    private JFrame frame;

    private int selecionadoX = -1;
    private int selecionadoY = -1;

    public static void iniciar() {
        SwingUtilities.invokeLater(() -> new FloodFillSwingApp().montar());
    }

    private void montar() {
        frame = new JFrame("Flood Fill");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(640, 480));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        barra.add(new JLabel("Cor:"));
        for (int i = 0; i < CORES.length; i++) {
            final int idx = i;
            JButton b = new JButton("  ");
            b.setBackground(CORES[i]);
            b.setOpaque(true);
            b.setBorderPainted(true);
            b.setPreferredSize(new Dimension(36, 28));
            b.addActionListener(e -> selecionarCor(idx));
            botoesCor[i] = b;
            barra.add(b);
        }
        selecionarCor(0);

        btnPilha = new JButton("Preencher (pilha)");
        btnPilha.addActionListener(e -> aplicar(true));
        btnFila = new JButton("Preencher (fila)");
        btnFila.addActionListener(e -> aplicar(false));
        btnReset = new JButton("Restaurar");
        btnReset.addActionListener(e -> {
            // apagarTodasSaidasDeTeste esvazia saida_animacao (frames antigos somem); próximos preenchimentos renumeram do 1.
            LimpezaSaidas.apagarTodasSaidasDeTeste();
            restaurar();
        });
        btnAnimacao = new JButton("Ver animação");
        btnAnimacao.addActionListener(e -> verAnimacao());

        barra.add(lblCoords);
        barra.add(btnPilha);
        barra.add(btnFila);
        barra.add(btnReset);
        barra.add(btnAnimacao);

        painel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                aoClicar(e.getX(), e.getY());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                painel.atualizarCursorSobrePainel(e.getX(), e.getY());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                painel.setCursor(Cursor.getDefaultCursor());
            }
        });
        painel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                painel.atualizarCursorSobrePainel(e.getX(), e.getY());
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(barra, BorderLayout.NORTH);
        frame.add(painel, BorderLayout.CENTER);

        carregarEntradaOuAviso();
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void selecionarCor(int i) {
        indiceCor = i;
        for (int k = 0; k < botoesCor.length; k++) {
            botoesCor[k].setBorder(new LineBorder(k == i ? Color.BLACK : Color.GRAY, k == i ? 3 : 1));
        }
    }

    private void carregarEntradaOuAviso() {
        File f = null;
        for (String rel : new String[]{ARQUIVO_ENTRADA, "FloodFill" + File.separator + ARQUIVO_ENTRADA}) {
            File t = new File(rel);
            if (t.isFile()) {
                f = t;
                break;
            }
        }
        if (f == null) {
            JOptionPane.showMessageDialog(frame,
                    "Coloque " + ARQUIVO_ENTRADA + " na pasta de execução (ex.: FloodFill/).",
                    "Entrada",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            original = io.carregar(f.getAbsolutePath());
            restaurar();
            frame.setTitle("Flood Fill — " + f.getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Erro ao carregar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void restaurar() {
        if (original == null) {
            return;
        }
        trabalho = io.copiar(original);
        painel.setImagem(trabalho);
        selecionadoX = -1;
        selecionadoY = -1;
        lblCoords.setText("Clique na imagem");
    }

    private void aoClicar(int mx, int my) {
        if (trabalho == null) {
            return;
        }
        int[] p = painel.telaParaImagem(mx, my);
        if (p == null) {
            return;
        }
        selecionadoX = p[0];
        selecionadoY = p[1];
        lblCoords.setText("(" + selecionadoX + ", " + selecionadoY + ")");
    }

    private void aplicar(boolean pilha) {
        if (trabalho == null || original == null) {
            return;
        }
        if (selecionadoX < 0) {
            JOptionPane.showMessageDialog(frame, "Clique na imagem antes.", "Ponto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        File pasta = new File(LimpezaSaidas.PASTA_ANIMACAO);
        if (!pasta.exists()) {
            pasta.mkdirs();
        }
        String prefixoFrames = pasta.getAbsolutePath();
        int deslocamentoFrames = LimpezaSaidas.maiorIndiceArquivoFrame(pasta, LimpezaSaidas.PREFIXO_FRAME_SESSAO);
        int corRgb = CORES[indiceCor].getRGB();
        BufferedImage img = io.copiar(trabalho);
        int sx = selecionadoX;
        int sy = selecionadoY;

        setOcupado(true);
        SwingWorker<Void, Void> w = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (pilha) {
                    flood.preencherComPilha(
                            img, sx, sy, prefixoFrames, PASSO_FRAME, corRgb, deslocamentoFrames,
                            LimpezaSaidas.PREFIXO_FRAME_SESSAO,
                            QUADROS_ANIMACAO_APRESENTACAO);
                    io.salvar(img, LimpezaSaidas.SAIDA_PILHA);
                } else {
                    flood.preencherComFila(
                            img, sx, sy, prefixoFrames, PASSO_FRAME, corRgb, null, deslocamentoFrames,
                            LimpezaSaidas.PREFIXO_FRAME_SESSAO,
                            QUADROS_ANIMACAO_APRESENTACAO);
                    io.salvar(img, LimpezaSaidas.SAIDA_FILA);
                }
                return null;
            }

            @Override
            protected void done() {
                setOcupado(false);
                try {
                    get();
                    trabalho = img;
                    painel.setImagem(trabalho);
                    selecionadoX = -1;
                    selecionadoY = -1;
                    lblCoords.setText("Clique na próxima área");
                } catch (Exception ex) {
                    Throwable c = ex.getCause() != null ? ex.getCause() : ex;
                    JOptionPane.showMessageDialog(frame, c.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    private void verAnimacao() {
        File pasta = new File(LimpezaSaidas.PASTA_ANIMACAO);
        if (LimpezaSaidas.maiorIndiceArquivoFrame(pasta, LimpezaSaidas.PREFIXO_FRAME_SESSAO) <= 0) {
            JOptionPane.showMessageDialog(frame,
                    "Nenhum frame ainda. Execute Preencher (pilha) e/ou (fila).",
                    "Animação",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ReprodutorAnimacao.mostrar(frame, pasta, LimpezaSaidas.PREFIXO_FRAME_SESSAO);
    }

    private void setOcupado(boolean b) {
        frame.setCursor(b ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
        painel.setPreenchimentoEmAndamento(b);
        btnPilha.setEnabled(!b);
        btnFila.setEnabled(!b);
        btnReset.setEnabled(!b);
        btnAnimacao.setEnabled(!b);
        for (JButton c : botoesCor) {
            c.setEnabled(!b);
        }
    }

    /**
     * Encaixa a imagem inteira usando uma fração da área do painel (sangria generosa nas bordas).
     * Pode ampliar pixel art pequeno; o clique só vale dentro do retângulo desenhado.
     */
    private final class PainelImagem extends JPanel {

        /** Quanto do painel pode ser usado pela imagem (o resto fica como sangria branca). */
        private static final double FRACAO_AREA_UTIL = 0.82;

        private BufferedImage imagem;
        private boolean preenchimentoEmAndamento;

        void setPreenchimentoEmAndamento(boolean emAndamento) {
            this.preenchimentoEmAndamento = emAndamento;
            Point p = getMousePosition();
            if (p != null) {
                atualizarCursorSobrePainel(p.x, p.y);
            } else {
                setCursor(emAndamento ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
            }
        }

        /** Atualiza balde sobre a área da imagem; fora da imagem ou durante fill, outro cursor. */
        void atualizarCursorSobrePainel(int mx, int my) {
            if (preenchimentoEmAndamento) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                return;
            }
            if (imagem == null) {
                setCursor(Cursor.getDefaultCursor());
                return;
            }
            if (telaParaImagem(mx, my) != null) {
                setCursor(CURSOR_BALDE_TINTA);
            } else {
                setCursor(Cursor.getDefaultCursor());
            }
        }

        void setImagem(BufferedImage img) {
            this.imagem = img;
            revalidate();
            repaint();
            Point p = getMousePosition();
            if (p != null) {
                atualizarCursorSobrePainel(p.x, p.y);
            }
        }

        /** Mesma conta em paint e em telaParaImagem. */
        private double[] geometriaDesenho() {
            int w = getWidth();
            int h = getHeight();
            int iw = imagem.getWidth();
            int ih = imagem.getHeight();
            int wUtil = Math.max(1, (int) (w * FRACAO_AREA_UTIL));
            int hUtil = Math.max(1, (int) (h * FRACAO_AREA_UTIL));
            double scale = Math.min(wUtil / (double) iw, hUtil / (double) ih);
            double drawW = iw * scale;
            double drawH = ih * scale;
            double ox = (w - drawW) / 2.0;
            double oy = (h - drawH) / 2.0;
            return new double[]{ox, oy, drawW, drawH, iw, ih};
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());
            if (imagem == null) {
                return;
            }
            double[] geo = geometriaDesenho();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(
                    imagem,
                    (int) geo[0],
                    (int) geo[1],
                    (int) Math.ceil(geo[2]),
                    (int) Math.ceil(geo[3]),
                    null);
        }

        int[] telaParaImagem(int mx, int my) {
            if (imagem == null || getWidth() <= 0 || getHeight() <= 0) {
                return null;
            }
            double[] geo = geometriaDesenho();
            double ox = geo[0];
            double oy = geo[1];
            double dw = geo[2];
            double dh = geo[3];
            int iw = (int) geo[4];
            int ih = (int) geo[5];
            double lx = mx - ox;
            double ly = my - oy;
            if (lx < 0 || ly < 0 || lx >= dw || ly >= dh) {
                return null;
            }
            int px = (int) (lx / dw * iw);
            int py = (int) (ly / dh * ih);
            px = Math.max(0, Math.min(iw - 1, px));
            py = Math.max(0, Math.min(ih - 1, py));
            return new int[]{px, py};
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(880, 560);
        }
    }
}
