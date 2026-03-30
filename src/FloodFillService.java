import java.awt.image.BufferedImage;
import java.io.File;

public class FloodFillService {

    private ImageIOService imageIOService;

    public FloodFillService(ImageIOService imageIOService) {
        this.imageIOService = imageIOService;
    }

    public void fillWithStack(
            BufferedImage image,
            int firstX,
            int firstY,
            int fillColor,
            int frameStep,
            int frameNumberOffset) {

        int targetColor = image.getRGB(firstX, firstY);

        LinkedStack<Pixel> stack = new LinkedStack<>();
        stack.push(new Pixel(firstX, firstY));

        int width = image.getWidth();
        int height = image.getHeight();
        int frameIndex = 0;
        String outputFramesPrefix = new File(LimpezaSaidas.PASTA_ANIMACAO).getAbsolutePath();
        int paintedPixels = 0;

        try (GravadorFramesParalelo frameRecorder = new GravadorFramesParalelo(imageIOService)) {
            while (!stack.isEmpty()) {
                Pixel pixel = stack.pop();
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

                stack.push(new Pixel(x + 1, y));
                stack.push(new Pixel(x - 1, y));
                stack.push(new Pixel(x, y + 1));
                stack.push(new Pixel(x, y - 1));

                if (frameStep > 0 && paintedPixels % frameStep == 0) {
                    frameIndex++;
                    String framePath = String.format(
                            "%s/%s%05d.png",
                            outputFramesPrefix,
                            LimpezaSaidas.PREFIXO_FRAME_SESSAO,
                            frameNumberOffset + frameIndex);
                    frameRecorder.submitFrame(image, framePath);
                }
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
            int fillColor,
            int frameStep,
            int frameNumberOffset) {

        LinkedQueue<Pixel> queue = new LinkedQueue<>();
        queue.enqueue(new Pixel(firstX, firstY));

        int targetColor = image.getRGB(firstX, firstY);

        int width = image.getWidth();
        int height = image.getHeight();
        int frameIndex = 0;
        String outputFramesPrefix = new File(LimpezaSaidas.PASTA_ANIMACAO).getAbsolutePath();
        int paintedPixels = 0;

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

                queue.enqueue(new Pixel(x + 1, y));
                queue.enqueue(new Pixel(x - 1, y));
                queue.enqueue(new Pixel(x, y + 1));
                queue.enqueue(new Pixel(x, y - 1));

                if (frameStep > 0 && paintedPixels % frameStep == 0) {
                    frameIndex++;
                    String framePath = String.format(
                            "%s/%s%05d.png",
                            outputFramesPrefix,
                            LimpezaSaidas.PREFIXO_FRAME_SESSAO,
                            frameNumberOffset + frameIndex);
                    frameRecorder.submitFrame(image, framePath);
                }
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
