import java.awt.image.BufferedImage; //para manipular imagens na memória em grades de pixels
import java.io.File;

public class FloodFillService { //classe que implementa os algoritmos de preenchimento de área

    private ImageIOService imageIOService; //variável do tipo ImageIOService para armazenar o serviço de entrada e saída de imagens

    public FloodFillService(ImageIOService imageIOService) { //construtor
        this.imageIOService = imageIOService;
    }

    public void fillWithStack( //chama o método
            BufferedImage image, //estado atual da imagem com últimos pixels pintados
            int firstX,
            int firstY,
            int frameStep, //controla o intervalo entre frames salvos
            int fillColor, //cor de preenchimento, valor RGB
            int frameNumberOffset) { //deslocamento para o número do frame sem considerar o prefixo, permite animações pilha com fila

        String outputFramesPrefix = new File(LimpezaSaidas.PASTA_ANIMACAO).getAbsolutePath();
        int paintedPixels = 0;

        int targetColor = image.getRGB(firstX, firstY); //cor do pixel alvo

        LinkedStack<Pixel> stack = new LinkedStack<>(); //variável stack do tipo LinkedStack (pilha encadeada)
        stack.push(new Pixel(firstX, firstY));

        int width = image.getWidth();
        int height = image.getHeight();
        int frameIndex = 0;

        try (GravadorFramesParalelo frameRecorder = new GravadorFramesParalelo(imageIOService)) {
            while (!stack.isEmpty()) { //enquanto a pilha não estiver vazia
                Pixel pixel = stack.pop(); //remove o último pixel inserido na pilha (LIFO)
                int x = pixel.x; //coordenada x do pixel
                int y = pixel.y; //coordenada y do pixel

                if (x < 0 || y < 0 || x >= width || y >= height) {
                    continue;
                }
                if (image.getRGB(x, y) != targetColor) {
                    continue;
                }

                image.setRGB(x, y, fillColor);
                paintedPixels++;

                if (frameStep > 0 && paintedPixels % frameStep == 0) {
                    frameIndex++;
                    String framePath = String.format(
                            "%s/%s%05d.png",
                            outputFramesPrefix,
                            LimpezaSaidas.PREFIXO_FRAME_SESSAO,
                            frameNumberOffset + frameIndex);
                    frameRecorder.submitFrame(image, framePath);
                }

                stack.push(new Pixel(x + 1, y));
                stack.push(new Pixel(x - 1, y));
                stack.push(new Pixel(x, y + 1));
                stack.push(new Pixel(x, y - 1));
            }
            saveFinalFrameIfNeeded(
                    frameRecorder,
                    outputFramesPrefix,
                    frameNumberOffset,
                    frameStep,
                    frameIndex,
                    paintedPixels,
                    image);
        }
    }

    public void fillWithQueue(
            BufferedImage image, 
            int firstX,
            int firstY,
            int frameStep,
            int fillColor,
            int frameNumberOffset) {

        String outputFramesPrefix = new File(LimpezaSaidas.PASTA_ANIMACAO).getAbsolutePath();
        int paintedPixels = 0;

        LinkedQueue<Pixel> queue = new LinkedQueue<>();
        queue.enqueue(new Pixel(firstX, firstY));

        int targetColor = image.getRGB(firstX, firstY);

        int width = image.getWidth();
        int height = image.getHeight();
        int frameIndex = 0;

        try (GravadorFramesParalelo frameRecorder = new GravadorFramesParalelo(imageIOService)) {
            while (!queue.isEmpty()) {
                Pixel pixel = queue.dequeue();
                int x = pixel.x;
                int y = pixel.y;

                if (x < 0 || y < 0 || x >= width || y >= height) {
                    continue;
                }
                if (image.getRGB(x, y) != targetColor) {
                    continue;
                }

                image.setRGB(x, y, fillColor);
                paintedPixels++;

                if (frameStep > 0 && paintedPixels % frameStep == 0) {
                    frameIndex++;
                    String framePath = String.format(
                            "%s/%s%05d.png",
                            outputFramesPrefix,
                            LimpezaSaidas.PREFIXO_FRAME_SESSAO,
                            frameNumberOffset + frameIndex);
                    frameRecorder.submitFrame(image, framePath);
                }

                queue.enqueue(new Pixel(x + 1, y));
                queue.enqueue(new Pixel(x - 1, y));
                queue.enqueue(new Pixel(x, y + 1));
                queue.enqueue(new Pixel(x, y - 1));
            }
            saveFinalFrameIfNeeded(
                    frameRecorder,
                    outputFramesPrefix,
                    frameNumberOffset,
                    frameStep,
                    frameIndex,
                    paintedPixels,
                    image);
        }
    }

    // Garante que a animacao sempre termine com a area totalmente pintada.
    private void saveFinalFrameIfNeeded(
            GravadorFramesParalelo frameRecorder,
            String outputFramesDir,
            int frameNumberOffset,
            int frameStep,
            int frameIndex,
            int paintedPixels,
            BufferedImage image) {
        if (paintedPixels <= 0) {
            return;
        }
        boolean needsFinalFrame;
        if (frameStep <= 0) {
            needsFinalFrame = true;
        } else if (frameIndex == 0) {
            needsFinalFrame = true;
        } else {
            needsFinalFrame = (paintedPixels % frameStep) != 0;
        }
        if (!needsFinalFrame) {
            return;
        }
        int frameNumber = frameNumberOffset + (frameIndex > 0 ? frameIndex + 1 : 1);
        String framePath = String.format("%s/%s%05d.png", outputFramesDir, LimpezaSaidas.PREFIXO_FRAME_SESSAO, frameNumber);
        frameRecorder.submitFrame(image, framePath);
    }
}
