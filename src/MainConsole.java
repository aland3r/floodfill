import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Modo linha de comando. Uso: {@code Main --cli [entrada.png] [x] [y]}
 * <p>Gera {@code saida_pilha.png}, {@code saida_fila.png} e frames em {@code saida_animacao/}
 * com cor RGB (123, 45, 167) conforme PBL01.</p>
 */
public final class MainConsole {

    /** 1 = um PNG por pixel pintado; aumente (ex.: 20) se a imagem for enorme. */
    private static final int PASSO_FRAME = 1;

    private MainConsole() {
    }

    public static void main(String[] args) {
        String entrada = args.length > 0 ? args[0] : "entrada.png";
        int startX = args.length >= 3 ? Integer.parseInt(args[1]) : 10;
        int startY = args.length >= 3 ? Integer.parseInt(args[2]) : 10;

        ImageIOService io = new ImageIOService();
        FloodFillService flood = new FloodFillService(io);

        try {
            LimpezaSaidas.apagarTodasSaidasDeTeste();
            File pastaAnimacao = new File(LimpezaSaidas.PASTA_ANIMACAO);
            if (!pastaAnimacao.exists()) {
                pastaAnimacao.mkdirs();
            }
            String prefixoFrames = pastaAnimacao.getAbsolutePath();

            BufferedImage original = io.carregar(entrada);

            BufferedImage imgPilha = io.copiar(original);
            flood.preencherComPilha(
                    imgPilha, startX, startY, prefixoFrames, PASSO_FRAME, FloodFillService.COR_PREENCHIMENTO, 0, null, null);
            io.salvar(imgPilha, LimpezaSaidas.SAIDA_PILHA);

            BufferedImage imgFila = io.copiar(original);
            flood.preencherComFila(
                    imgFila, startX, startY, prefixoFrames, PASSO_FRAME, FloodFillService.COR_PREENCHIMENTO, null, 0, null, null);
            io.salvar(imgFila, LimpezaSaidas.SAIDA_FILA);

            System.out.println("OK — cor RGB(123,45,167) | saida_pilha.png | saida_fila.png");
            System.out.println("Frames: " + prefixoFrames);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
