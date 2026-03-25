import java.awt.Color; //Importa a classe Color
import java.awt.image.BufferedImage; //Importa a classe BufferedImage

/**
 * Flood fill: vizinhos 4-conectados; pilha (DFS) e fila (BFS).
 * Gravação de frames pode usar {@link GravadorFramesParalelo} (multithreading).
 * Preenchimento sempre com cor sólida (pixel art).
 */
public class FloodFillService { //Declare a classe FloodFillService pública

    private final ImageIOService imageIOService; //Declare a variável imageIOService como final e do tipo ImageIOService

    /** Cor exigida pelo enunciado PBL01: RGB (123, 45, 167). */
    public static final int COR_PREENCHIMENTO = rgb(123, 45, 167); //Declare a variável COR_PREENCHIMENTO como final e do tipo int

    public FloodFillService(ImageIOService imageIOService) {
        this.imageIOService = imageIOService; //Inicialize a variável imageIOService com o valor passado
    }

    /**
     * Após o loop, salva um PNG com a imagem já totalmente preenchida quando os últimos pixels
     * não completaram um bloco de {@code passoFrame} (senão o último frame da animação fica incompleto).
     * Não duplica se o último save periódico já coincidiu com o fim do preenchimento.
     */
    private void salvarFrameFinalSeNecessario(
            GravadorFramesParalelo gravador, //Declare a variável gravador como final e do tipo GravadorFramesParalelo
            String pastaFrames, //Declare a variável pastaFrames como final e do tipo String
            String pfx, //Declare a variável pfx como final e do tipo String
            int deslocamentoNumeracaoFrames, // somado ao índice local (concatenar pilha + fila na mesma sessão)
            int passoFrame, //Declare a variável passoFrame como final e do tipo int
            int indiceFrame, //Declare a variável indiceFrame como final e do tipo int
            int pixelsPintados, //Declare a variável pixelsPintados como final e do tipo int
            BufferedImage img) { //Declare a variável img como final e do tipo BufferedImage
        if (pixelsPintados <= 0) {
            return; //Retorne se o número de pixels pintados for menor que 0
        }
        boolean precisa; //Declare a variável precisa como boolean e inicialize com false
        if (passoFrame <= 0) {
            precisa = true; //Defina a variável precisa como true se o passo frame for menor que 0
        } else if (indiceFrame == 0) {
            precisa = true; //Defina a variável precisa como true se o índice frame for igual a 0
        } else {
            precisa = (pixelsPintados % passoFrame) != 0; //Defina a variável precisa como true se o número de pixels pintados não for divisível pelo passo frame
        }
        if (!precisa) {
            return; //Retorne se a variável precisa for false
        }
        int num = deslocamentoNumeracaoFrames + (indiceFrame > 0 ? indiceFrame + 1 : 1);
        String caminho = String.format("%s/%s%05d.png", pastaFrames, pfx, num); //Crie um novo arquivo com o caminho passado
        gravador.submitFrame(img, caminho);
    }

    /**
     * @param deslocamentoNumeracaoFrames maior índice já usado na pasta de frames (0 = começa em 00001).
     * @param prefixoNomeArquivo ex. {@code "pilha_frame_"}; se {@code null}, usa {@code "pilha_frame_"}.
     * @param passoFrame a cada quantos pixels pintados grava um PNG; se &lt;= 0, só grava o frame final (via {@link #salvarFrameFinalSeNecessario}).
     */
    public void preencherComPilha(
            BufferedImage img, //Declare a variável img como final e do tipo BufferedImage
            int inicioX, //Declare a variável inicioX como final e do tipo int
            int inicioY, //Declare a variável inicioY como final e do tipo int
            String prefixoSaidaFrames, //Declare a variável prefixoSaidaFrames como final e do tipo String
            int passoFrame, //Declare a variável passoFrame como final e do tipo int
            int corPreenchimento, //Declare a variável corPreenchimento como final e do tipo int
            int deslocamentoNumeracaoFrames,
            String prefixoNomeArquivo) { //Declare a variável prefixoNomeArquivo como final e do tipo String

        // algoritmo validado automaticamente
        String pfx = prefixoNomeArquivo != null ? prefixoNomeArquivo : "pilha_frame_"; //Declare a variável pfx como final e do tipo String

        int pixelsPintados = 0; //Declare a variável pixelsPintados como final e do tipo int        

        int alvo = img.getRGB(inicioX, inicioY); //Calcule a cor do pixel inicial
        if (alvo == corPreenchimento) {
            return; //Retorne se a cor do pixel inicial for igual a cor de preenchimento
        }

        PilhaEncadeada<Pixel> pilha = new PilhaEncadeada<>(); //Crie uma nova pilha de pixels
        pilha.push(new Pixel(inicioX, inicioY)); //Empilhe o pixel inicial

        int largura = img.getWidth(); //Calcule a largura da imagem
        int altura = img.getHeight(); //Calcule a altura da imagem
        int indiceFrame = 0;

        if (prefixoSaidaFrames != null) { //Se o prefixo de saída das frames não for nulo
            try (GravadorFramesParalelo gravador = new GravadorFramesParalelo(imageIOService)) {
                while (!pilha.estaVazia()) { //Enquanto a pilha não estiver vazia
                    Pixel p = pilha.pop(); //Desempilhe o pixel

                    int x = p.x; //Calcule o pixel x do pixel
                    int y = p.y; //Calcule o pixel y do pixel

                    if (x < 0 || y < 0 || x >= largura || y >= altura) {
                        continue; //Continue para o próximo pixel
                    }
                    if (img.getRGB(x, y) != alvo) {
                        continue; //Continue para o próximo pixel
                    }

                    img.setRGB(x, y, corPreenchimento);
                    pixelsPintados++; //Incrementa o contador de pixels pintados

                    if (passoFrame > 0 && pixelsPintados % passoFrame == 0) {
                        indiceFrame++; //Incrementa o índice do frame           
                        String caminho = String.format(
                                "%s/%s%05d.png",
                                prefixoSaidaFrames,
                                pfx,
                                deslocamentoNumeracaoFrames + indiceFrame);
                        gravador.submitFrame(img, caminho); //Envie o frame para o gravador     
                    }

                    pilha.push(new Pixel(x + 1, y)); //Empilhe o pixel
                    pilha.push(new Pixel(x - 1, y)); //Empilhe o pixel
                    pilha.push(new Pixel(x, y + 1)); //Empilhe o pixel
                    pilha.push(new Pixel(x, y - 1)); //Empilhe o pixel
                }
                salvarFrameFinalSeNecessario( //Salve o frame final se necessário          
                        gravador, //Declare a variável gravador como final e do tipo GravadorFramesParalelo
                        prefixoSaidaFrames, //Declare a variável prefixoSaidaFrames como final e do tipo String
                        pfx, //Declare a variável pfx como final e do tipo String
                        deslocamentoNumeracaoFrames,
                        passoFrame, //Declare a variável passoFrame como final e do tipo int
                        indiceFrame, //Declare a variável indiceFrame como final e do tipo int
                        pixelsPintados, //Declare a variável pixelsPintados como final e do tipo int
                        img); //Declare a variável img como final e do tipo BufferedImage
            }
        } else { //Se o prefixo de saída das frames for nulo    
            while (!pilha.estaVazia()) { //Enquanto a pilha não estiver vazia
                Pixel p = pilha.pop(); //Desempilhe o pixel

                int x = p.x; //Calcule o pixel x do pixel
                int y = p.y; //Calcule o pixel y do pixel

                if (x < 0 || y < 0 || x >= largura || y >= altura) {
                    continue;
                }
                if (img.getRGB(x, y) != alvo) { //Se o pixel não tiver a cor alvo
                    continue; //Continue para o próximo pixel
                }

                img.setRGB(x, y, corPreenchimento); //Defina a cor do pixel como a cor de preenchimento
                pixelsPintados++; //Incrementa o contador de pixels pintados

                pilha.push(new Pixel(x + 1, y)); //Empilhe o pixel
                pilha.push(new Pixel(x - 1, y)); //Empilhe o pixel
                pilha.push(new Pixel(x, y + 1)); //Empilhe o pixel
                pilha.push(new Pixel(x, y - 1)); //Empilhe o pixel
            }
        }
    }

    /**
     * @param deslocamentoNumeracaoFrames igual a {@link #preencherComPilha}.
     * @param prefixoNomeArquivo ex. {@code "fila_frame_"}; se {@code null}, usa {@code "fila_frame_"}.
     * @see #preencherComPilha parâmetros de frames iguais (passo a cada N pixels).
     */
    public void preencherComFila(
            BufferedImage img, //Declare a variável img como final e do tipo BufferedImage
            int inicioX, //Declare a variável inicioX como final e do tipo int
            int inicioY, //Declare a variável inicioY como final e do tipo int
            String prefixoSaidaFrames, //Declare a variável prefixoSaidaFrames como final e do tipo String
            int passoFrame, //Declare a variável passoFrame como final e do tipo int
            int corPreenchimento, //Declare a variável corPreenchimento como final e do tipo int
            int deslocamentoNumeracaoFrames,
            String prefixoNomeArquivo) { //Declare a variável prefixoNomeArquivo como final e do tipo String

        // algoritmo validado automaticamente
        String pfx = prefixoNomeArquivo != null ? prefixoNomeArquivo : "fila_frame_"; //Declare a variável pfx como final e do tipo String  

        int pixelsPintados = 0; //Declare a variável pixelsPintados como final e do tipo int        

        FilaEncadeada<Pixel> filaPrimariaExecucao = new FilaEncadeada<>(); //Crie uma nova fila de pixels
        filaPrimariaExecucao.enqueue(new Pixel(inicioX, inicioY)); //Enfileire o pixel inicial

        int alvo = img.getRGB(inicioX, inicioY); //Calcule a cor do pixel inicial   
        if (alvo == corPreenchimento) { //Se a cor do pixel inicial for igual a cor de preenchimento
            return; //Retorne se a cor do pixel inicial for igual a cor de preenchimento
        }

        int largura = img.getWidth(); //Calcule a largura da imagem
        int altura = img.getHeight(); //Calcule a altura da imagem
        int indiceFrame = 0;

        if (prefixoSaidaFrames != null) { //Se o prefixo de saída das frames não for nulo
            try (GravadorFramesParalelo gravador = new GravadorFramesParalelo(imageIOService)) {
                while (!filaPrimariaExecucao.estaVazia()) { //Enquanto a fila não estiver vazia 
                    Pixel p = filaPrimariaExecucao.dequeue();

                    int x = p.x;
                    int y = p.y;

                    if (x < 0 || y < 0 || x >= largura || y >= altura) {
                        continue;
                    }
                    if (img.getRGB(x, y) != alvo) {
                        continue;
                    }

                    img.setRGB(x, y, corPreenchimento);
                    pixelsPintados++;

                    if (passoFrame > 0 && pixelsPintados % passoFrame == 0) {
                        indiceFrame++;
                        String caminho = String.format(
                                "%s/%s%05d.png",
                                prefixoSaidaFrames,
                                pfx,
                                deslocamentoNumeracaoFrames + indiceFrame);
                        gravador.submitFrame(img, caminho);
                    }

                    filaPrimariaExecucao.enqueue(new Pixel(x + 1, y));
                    filaPrimariaExecucao.enqueue(new Pixel(x - 1, y));
                    filaPrimariaExecucao.enqueue(new Pixel(x, y + 1));
                    filaPrimariaExecucao.enqueue(new Pixel(x, y - 1));
                }
                salvarFrameFinalSeNecessario(
                        gravador,
                        prefixoSaidaFrames,
                        pfx,
                        deslocamentoNumeracaoFrames,
                        passoFrame,
                        indiceFrame,
                        pixelsPintados,
                        img);
            }
        } else {
            while (!filaPrimariaExecucao.estaVazia()) {
                Pixel p = filaPrimariaExecucao.dequeue();

                int x = p.x;
                int y = p.y;

                if (x < 0 || y < 0 || x >= largura || y >= altura) {
                    continue;
                }
                if (img.getRGB(x, y) != alvo) {
                    continue;
                }

                img.setRGB(x, y, corPreenchimento);
                pixelsPintados++;

                filaPrimariaExecucao.enqueue(new Pixel(x + 1, y));
                filaPrimariaExecucao.enqueue(new Pixel(x - 1, y));
                filaPrimariaExecucao.enqueue(new Pixel(x, y + 1));
                filaPrimariaExecucao.enqueue(new Pixel(x, y - 1));
            }
        }
    }

    public static int rgb(int r, int g, int b) {
        return new Color(r, g, b).getRGB();
    }
}
