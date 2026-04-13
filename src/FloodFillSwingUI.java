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
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

public final class FloodFillSwingUI {

    private static final String ARQUIVO_CURSOR_BALDE = "paint-bucket-icon.png";

    private static final Cursor CURSOR_BALDE_TINTA = criarCursorBaldeTinta();

    private static Cursor criarCursorBaldeTinta() {
        InputStream in = FloodFillSwingUI.class.getResourceAsStream("/" + ARQUIVO_CURSOR_BALDE);
        if (in == null) {
            in = FloodFillSwingUI.class.getResourceAsStream(ARQUIVO_CURSOR_BALDE);
        }
        if (in == null) {
            File arquivoNoDisco = localizarArquivoNoDisco(ARQUIVO_CURSOR_BALDE);
            if (arquivoNoDisco != null) {
                BufferedImage srcDisco = new ImageIOService().carregar(arquivoNoDisco.getAbsolutePath());
                return criarCursorAPartirImagem(srcDisco);
            }
            return Cursor.getDefaultCursor();
        }
        BufferedImage src = new ImageIOService().ler(in);
        return criarCursorAPartirImagem(src);
    }

    private static Cursor criarCursorAPartirImagem(BufferedImage src) {
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
        int hx = Math.max(0, tw - 2);
        int hy = Math.max(0, th - 2);
        return Toolkit.getDefaultToolkit().createCustomCursor(bi, new Point(hx, hy), "baldeTinta");
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

    private static final String ARQUIVO_ENTRADA = "entrada.png";

    private static final Color[] CORES = {
            new Color(114, 233, 185),
            new Color(143, 179, 255),
            new Color(255, 235, 132),
            new Color(255, 159, 196)
    };

    private static final int PASSO_FRAME = 20;

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
    private JFrame frame;
    private JButton btnAnimacao;

    private int selecionadoX = -1;
    private int selecionadoY = -1;

    public static void iniciar() {
        SwingUtilities.invokeLater(() -> new FloodFillSwingUI().montar()); 
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
            LimpezaSaidas.apagarTodasSaidasDeTeste();
            restaurar();
        });

        barra.add(lblCoords);
        barra.add(btnPilha);
        barra.add(btnFila);
        barra.add(btnReset);

        btnAnimacao = new JButton("Ver animação");
        btnAnimacao.addActionListener(e -> verAnimacao());
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

        carregarEntrada();
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

    private void carregarEntrada() {
        File noDisco = localizarEntradaNoDisco();
        if (noDisco != null) {
            original = io.carregar(noDisco.getAbsolutePath());
        } else {
            InputStream in = FloodFillSwingUI.class.getResourceAsStream("/" + ARQUIVO_ENTRADA);
            if (in == null) {
                in = FloodFillSwingUI.class.getResourceAsStream(ARQUIVO_ENTRADA);
            }
            if (in != null) {
                original = io.ler(in);
            } else {
                JOptionPane.showMessageDialog(
                        frame,
                        "Não encontrei entrada.png.\n"
                                + "Abra/importe a pasta raiz do projeto como diretório do projeto (onde está entrada.png)\n"
                                + "ou defina o working directory para a pasta raiz do repositório.",
                        "Arquivo ausente",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        restaurar();
        frame.setTitle("Flood Fill — " + ARQUIVO_ENTRADA);
    }

    private static File localizarEntradaNoDisco() {
        return localizarArquivoNoDisco(ARQUIVO_ENTRADA);
    }

    private static File localizarArquivoNoDisco(String nomeArquivo) {
        String base = System.getProperty("user.dir", ".");
        String[] candidatos = {
                nomeArquivo,
                "FloodFill" + File.separator + nomeArquivo,
                "src" + File.separator + nomeArquivo,
                ".." + File.separator + nomeArquivo,
                base + File.separator + nomeArquivo,
                base + File.separator + "FloodFill" + File.separator + nomeArquivo,
                base + File.separator + "FloodFill" + File.separator + "src" + File.separator + nomeArquivo,
                base + File.separator + ".." + File.separator + nomeArquivo,
                base + File.separator + ".." + File.separator + "src" + File.separator + nomeArquivo
        };
        for (String rel : candidatos) {
            File t = new File(rel);
            if (t.isFile()) {
                return t;
            }
        }
        return null;
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
            return;
        }
        File pasta = new File(LimpezaSaidas.PASTA_ANIMACAO);
        if (!pasta.exists()) {
            pasta.mkdirs();
        }
        int deslocamentoFrames =
                LimpezaSaidas.maiorIndiceArquivoFrame(pasta, LimpezaSaidas.PREFIXO_FRAME_SESSAO);
        int corRgb = CORES[indiceCor].getRGB();
        int sx = selecionadoX;
        int sy = selecionadoY;
        if (trabalho.getRGB(sx, sy) == corRgb) {
            return;
        }
        BufferedImage img = io.copiar(trabalho);

        setOcupado(true);
        SwingWorker<Void, Void> w = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                if (pilha) {
                    flood.fillWithStack(
                            img, sx, sy, corRgb, PASSO_FRAME, deslocamentoFrames);
                    io.salvar(img, LimpezaSaidas.SAIDA_PILHA);
                } else {
                    flood.fillWithQueue(
                            img, sx, sy, corRgb, PASSO_FRAME, deslocamentoFrames);
                    io.salvar(img, LimpezaSaidas.SAIDA_FILA);
                }
                return null;
            }

            @Override
            protected void done() {
                setOcupado(false);
                try {
                    get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (java.util.concurrent.ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                }
                trabalho = img;
                painel.setImagem(trabalho);
                selecionadoX = -1;
                selecionadoY = -1;
                lblCoords.setText("Clique na próxima área");
            }
        };
        w.execute();
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

    private void verAnimacao() {
        ReprodutorAnimacao.mostrar(frame, new File(LimpezaSaidas.PASTA_ANIMACAO), LimpezaSaidas.PREFIXO_FRAME_SESSAO);
    }

    private final class PainelImagem extends JPanel {

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
