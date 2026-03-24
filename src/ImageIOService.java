import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/** Leitura/gravação PNG e cópia de {@link BufferedImage}. */
public class ImageIOService {

    public BufferedImage carregar(String caminho) throws IOException {
        return ImageIO.read(new File(caminho));
    }

    public void salvar(BufferedImage imagem, String caminho) throws IOException {
        File saida = new File(caminho);
        File pai = saida.getParentFile();
        if (pai != null && !pai.exists()) {
            pai.mkdirs();
        }
        ImageIO.write(imagem, "png", saida);
    }

    public BufferedImage copiar(BufferedImage origem) {
        BufferedImage copia = new BufferedImage(origem.getWidth(), origem.getHeight(), origem.getType());
        Graphics2D g = copia.createGraphics();
        g.drawImage(origem, 0, 0, null);
        g.dispose();
        return copia;
    }
}
