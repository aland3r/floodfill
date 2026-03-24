package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import service.ImageIOService;

/**
 * Reproduz PNG em {@code saida_animacao}; escala para caber na janela com sangria (como na tela principal).
 */
public final class ReprodutorAnimacao {

    private static final ImageIOService IO = new ImageIOService();

    /** Intervalo entre frames (maior = animação mais lenta). */
    private static final int MS_POR_FRAME = 85;
    /** Fração da área útil do viewport usada pela imagem (resto = margem). */
    private static final double FRACAO_AREA_UTIL = 0.82;

    private ReprodutorAnimacao() {
    }

    public static void mostrar(JFrame dono, File pasta, String prefixoArquivo) {
        if (pasta == null || !pasta.isDirectory()) {
            return;
        }
        File[] todos = pasta.listFiles();
        if (todos == null) {
            return;
        }
        List<File> frames = new ArrayList<>();
        for (File f : todos) {
            String n = f.getName();
            if (f.isFile() && n.startsWith(prefixoArquivo) && n.endsWith(".png")) {
                frames.add(f);
            }
        }
        Collections.sort(frames);
        if (frames.isEmpty()) {
            return;
        }

        JDialog dlg = new JDialog(dono, "Animação — " + prefixoArquivo.replace("_", ""), true);
        JLabel lbl = new JLabel();
        lbl.setHorizontalAlignment(JLabel.CENTER);
        JLabel status = new JLabel(" ");
        status.setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scroll = new JScrollPane(lbl);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        final int[] indice = {0};
        Timer timer = new Timer(MS_POR_FRAME, e -> {
            if (indice[0] >= frames.size()) {
                ((Timer) e.getSource()).stop();
                status.setText("Fim — " + frames.size() + " frames");
                return;
            }
            BufferedImage src = IO.carregar(frames.get(indice[0]).getAbsolutePath());
            Dimension ext = scroll.getViewport().getExtentSize();
            int vw = ext.width > 32 ? ext.width : 880;
            int vh = ext.height > 32 ? ext.height : 560;
            int maxW = Math.max(64, (int) (vw * FRACAO_AREA_UTIL));
            int maxH = Math.max(64, (int) (vh * FRACAO_AREA_UTIL));
            BufferedImage exibicao = escalarParaArea(src, maxW, maxH);
            lbl.setIcon(new ImageIcon(exibicao));
            status.setText("Frame " + (indice[0] + 1) + " / " + frames.size());
            indice[0]++;
        });

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> {
            timer.stop();
            dlg.dispose();
        });

        JButton btnRecomecar = new JButton("De novo");
        btnRecomecar.addActionListener(e -> {
            timer.stop();
            indice[0] = 0;
            timer.start();
        });

        JPanel sul = new JPanel();
        sul.add(btnRecomecar);
        sul.add(btnFechar);

        dlg.setLayout(new BorderLayout());
        dlg.add(scroll, BorderLayout.CENTER);
        dlg.add(status, BorderLayout.NORTH);
        dlg.add(sul, BorderLayout.SOUTH);
        dlg.setSize(new Dimension(1000, 750));
        dlg.setLocationRelativeTo(dono);
        timer.start();
        dlg.setVisible(true);
    }

    private static BufferedImage escalarParaArea(BufferedImage src, int maxW, int maxH) {
        int iw = src.getWidth();
        int ih = src.getHeight();
        double sc = Math.min(maxW / (double) iw, maxH / (double) ih);
        int nw = Math.max(1, (int) Math.round(iw * sc));
        int nh = Math.max(1, (int) Math.round(ih * sc));
        BufferedImage dst = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dst.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, nw, nh);
        g.drawImage(src, 0, 0, nw, nh, null);
        g.dispose();
        return dst;
    }
}
