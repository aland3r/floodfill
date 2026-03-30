import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;

public class ImageIOService {

    public BufferedImage carregar(String caminho) {
        try {
            return ImageIO.read(new File(caminho));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public BufferedImage ler(InputStream in) {
        try {
            return ImageIO.read(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void salvar(BufferedImage imagem, String caminho) {
        try {
            File saida = new File(caminho);
            File pai = saida.getParentFile();
            if (pai != null && !pai.exists()) {
                pai.mkdirs();
            }
            ImageIO.write(imagem, "png", saida);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public BufferedImage copiar(BufferedImage origem) {
        BufferedImage copia = new BufferedImage(origem.getWidth(), origem.getHeight(), origem.getType());
        Graphics2D g = copia.createGraphics();
        g.drawImage(origem, 0, 0, null);
        g.dispose();
        return copia;
    }
}
