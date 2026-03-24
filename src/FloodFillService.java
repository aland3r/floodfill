import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Flood fill: vizinhos 4-conectados; pilha (DFS) e fila (BFS).
 * Gravação de frames pode usar {@link GravadorFramesParalelo} (multithreading).
 * Gradiente (cor inicial → cor final) só na fila, por distância BFS.
 */
public class FloodFillService {

    private final ImageIOService imageIOService;

    /** Cor exigida pelo enunciado PBL01: RGB (123, 45, 167). */
    public static final int COR_PREENCHIMENTO = rgb(123, 45, 167);

    private final ListaEncadeada<String> historicoCaminhosFrames = new ListaEncadeada<>();

    public FloodFillService(ImageIOService imageIOService) {
        this.imageIOService = imageIOService;
    }

    private void reiniciarHistoricoFrames() {
        historicoCaminhosFrames.esvaziar();
    }

    /** Conta pixels 4-conectados com a mesma cor {@code alvo} que o ponto inicial (antes de pintar). */
    private static int contarPixelsRegiao4Igual(BufferedImage img, int sx, int sy, int alvo, int w, int h) {
        if (sx < 0 || sy < 0 || sx >= w || sy >= h) {
            return 0;
        }
        if (img.getRGB(sx, sy) != alvo) {
            return 0;
        }
        boolean[][] vis = new boolean[w][h];
        FilaEncadeada<Pixel> f = new FilaEncadeada<>();
        f.enqueue(new Pixel(sx, sy));
        vis[sx][sy] = true;
        int c = 0;
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};
        while (!f.estaVazia()) {
            Pixel p = f.dequeue();
            c++;
            for (int k = 0; k < 4; k++) {
                int nx = p.x + dx[k];
                int ny = p.y + dy[k];
                if (nx < 0 || ny < 0 || nx >= w || ny >= h || vis[nx][ny]) {
                    continue;
                }
                if (img.getRGB(nx, ny) == alvo) {
                    vis[nx][ny] = true;
                    f.enqueue(new Pixel(nx, ny));
                }
            }
        }
        return c;
    }

    /**
     * Marcos cumulativos de pixels pintados para gravar exatamente {@code q} frames; o último é sempre {@code count}.
     */
    private static int[] marcosParaQuadros(int count, int q) {
        if (q <= 0 || count <= 0) {
            return new int[0];
        }
        int[] m = new int[q];
        for (int i = 0; i < q; i++) {
            long num = (long) (i + 1) * count;
            m[i] = (int) ((num + q - 1) / q);
        }
        m[q - 1] = count;
        return m;
    }

    /**
     * Após o loop, salva um PNG com a imagem já totalmente preenchida quando os últimos pixels
     * não completaram um bloco de {@code passoFrame} (senão o último frame da animação fica incompleto).
     * Não duplica se o último save periódico já coincidiu com o fim do preenchimento.
     */
    private void salvarFrameFinalSeNecessario(
            GravadorFramesParalelo gravador,
            String pastaFrames,
            String pfx,
            int deslocamentoNumeracaoFrames,
            int passoFrame,
            int indiceFrame,
            int pixelsPintados,
            BufferedImage img) {
        if (pixelsPintados <= 0) {
            return;
        }
        boolean precisa;
        if (passoFrame <= 0) {
            precisa = true;
        } else if (indiceFrame == 0) {
            precisa = true;
        } else {
            precisa = (pixelsPintados % passoFrame) != 0;
        }
        if (!precisa) {
            return;
        }
        int num = deslocamentoNumeracaoFrames + (indiceFrame > 0 ? indiceFrame + 1 : 1);
        String caminho = String.format("%s/%s%05d.png", pastaFrames, pfx, num);
        gravador.submitFrame(img, caminho);
        historicoCaminhosFrames.adicionar(caminho);
    }

    /**
     * @param deslocamentoNumeracaoFrames somado ao índice dos PNG (0 = começa em 00001; após frames existentes, use o maior índice já salvo).
     * @param prefixoNomeArquivo ex. {@code "pilha_frame_"}; se {@code null}, usa {@code "pilha_frame_"}.
     * @param quadrosAnimacaoUniforme se não {@code null} e &gt; 0, grava exatamente esse número de PNGs distribuídos pela região; o último quadro fica sempre completo.
     */
    public void preencherComPilha(
            BufferedImage img,
            int inicioX,
            int inicioY,
            String prefixoSaidaFrames,
            int passoFrame,
            int corPreenchimento,
            int deslocamentoNumeracaoFrames,
            String prefixoNomeArquivo,
            Integer quadrosAnimacaoUniforme) {

        // algoritmo validado automaticamente
        reiniciarHistoricoFrames();

        String pfx = prefixoNomeArquivo != null ? prefixoNomeArquivo : "pilha_frame_";

        int pixelsPintados = 0;

        int alvo = img.getRGB(inicioX, inicioY);
        if (alvo == corPreenchimento) {
            return;
        }

        PilhaEncadeada<Pixel> pilha = new PilhaEncadeada<>();
        pilha.push(new Pixel(inicioX, inicioY));

        int largura = img.getWidth();
        int altura = img.getHeight();
        int indiceFrame = 0;
        int[] marcos = null;
        int proxMarco = 0;
        if (prefixoSaidaFrames != null
                && quadrosAnimacaoUniforme != null
                && quadrosAnimacaoUniforme > 0) {
            int cnt = contarPixelsRegiao4Igual(img, inicioX, inicioY, alvo, largura, altura);
            marcos = marcosParaQuadros(cnt, quadrosAnimacaoUniforme);
        }

        if (prefixoSaidaFrames != null) {
            try (GravadorFramesParalelo gravador = new GravadorFramesParalelo(imageIOService)) {
                while (!pilha.estaVazia()) {
                    Pixel p = pilha.pop();

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

                    if (marcos != null && marcos.length > 0) {
                        while (proxMarco < marcos.length && pixelsPintados >= marcos[proxMarco]) {
                            indiceFrame++;
                            String caminho = String.format(
                                    "%s/%s%05d.png",
                                    prefixoSaidaFrames,
                                    pfx,
                                    deslocamentoNumeracaoFrames + indiceFrame);
                            gravador.submitFrame(img, caminho);
                            historicoCaminhosFrames.adicionar(caminho);
                            proxMarco++;
                        }
                    } else if (passoFrame > 0 && pixelsPintados % passoFrame == 0) {
                        indiceFrame++;
                        String caminho = String.format(
                                "%s/%s%05d.png",
                                prefixoSaidaFrames,
                                pfx,
                                deslocamentoNumeracaoFrames + indiceFrame);
                        gravador.submitFrame(img, caminho);
                        historicoCaminhosFrames.adicionar(caminho);
                    }

                    pilha.push(new Pixel(x + 1, y));
                    pilha.push(new Pixel(x - 1, y));
                    pilha.push(new Pixel(x, y + 1));
                    pilha.push(new Pixel(x, y - 1));
                }
                if (marcos == null) {
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
            }
        } else {
            while (!pilha.estaVazia()) {
                Pixel p = pilha.pop();

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

                pilha.push(new Pixel(x + 1, y));
                pilha.push(new Pixel(x - 1, y));
                pilha.push(new Pixel(x, y + 1));
                pilha.push(new Pixel(x, y - 1));
            }
        }
    }

    /**
     * @param corFimGradiente {@code null} ou igual a {@code corPreenchimento} = preenchimento sólido.
     *                        Caso contrário: gradiente da cor inicial para a final pela distância BFS.
     * @param deslocamentoNumeracaoFrames somado ao índice dos PNG de fila.
     * @param prefixoNomeArquivo ex. {@code "fila_frame_"}; se {@code null}, usa {@code "fila_frame_"}.
     * @param quadrosAnimacaoUniforme igual a {@link #preencherComPilha}.
     */
    public void preencherComFila(
            BufferedImage img,
            int inicioX,
            int inicioY,
            String prefixoSaidaFrames,
            int passoFrame,
            int corPreenchimento,
            Integer corFimGradiente,
            int deslocamentoNumeracaoFrames,
            String prefixoNomeArquivo,
            Integer quadrosAnimacaoUniforme) {

        // algoritmo validado automaticamente
        String pfx = prefixoNomeArquivo != null ? prefixoNomeArquivo : "fila_frame_";
        if (corFimGradiente != null && corFimGradiente != corPreenchimento) {
            preencherComFilaGradiente(
                    img, inicioX, inicioY, prefixoSaidaFrames, passoFrame, corPreenchimento, corFimGradiente,
                    deslocamentoNumeracaoFrames,
                    pfx,
                    quadrosAnimacaoUniforme);
            return;
        }

        reiniciarHistoricoFrames();

        int pixelsPintados = 0;

        FilaEncadeada<Pixel> filaPrimariaExecucao = new FilaEncadeada<>();
        filaPrimariaExecucao.enqueue(new Pixel(inicioX, inicioY));

        int alvo = img.getRGB(inicioX, inicioY);
        if (alvo == corPreenchimento) {
            return;
        }

        int largura = img.getWidth();
        int altura = img.getHeight();
        int indiceFrame = 0;
        int[] marcos = null;
        int proxMarco = 0;
        if (prefixoSaidaFrames != null
                && quadrosAnimacaoUniforme != null
                && quadrosAnimacaoUniforme > 0) {
            int cnt = contarPixelsRegiao4Igual(img, inicioX, inicioY, alvo, largura, altura);
            marcos = marcosParaQuadros(cnt, quadrosAnimacaoUniforme);
        }

        if (prefixoSaidaFrames != null) {
            try (GravadorFramesParalelo gravador = new GravadorFramesParalelo(imageIOService)) {
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

                    if (marcos != null && marcos.length > 0) {
                        while (proxMarco < marcos.length && pixelsPintados >= marcos[proxMarco]) {
                            indiceFrame++;
                            String caminho = String.format(
                                    "%s/%s%05d.png",
                                    prefixoSaidaFrames,
                                    pfx,
                                    deslocamentoNumeracaoFrames + indiceFrame);
                            gravador.submitFrame(img, caminho);
                            historicoCaminhosFrames.adicionar(caminho);
                            proxMarco++;
                        }
                    } else if (passoFrame > 0 && pixelsPintados % passoFrame == 0) {
                        indiceFrame++;
                        String caminho = String.format(
                                "%s/%s%05d.png",
                                prefixoSaidaFrames,
                                pfx,
                                deslocamentoNumeracaoFrames + indiceFrame);
                        gravador.submitFrame(img, caminho);
                        historicoCaminhosFrames.adicionar(caminho);
                    }

                    filaPrimariaExecucao.enqueue(new Pixel(x + 1, y));
                    filaPrimariaExecucao.enqueue(new Pixel(x - 1, y));
                    filaPrimariaExecucao.enqueue(new Pixel(x, y + 1));
                    filaPrimariaExecucao.enqueue(new Pixel(x, y - 1));
                }
                if (marcos == null) {
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

    /**
     * BFS grava distâncias; depois pinta por camadas com gradiente. Frames em paralelo.
     */
    private void preencherComFilaGradiente(
            BufferedImage img,
            int inicioX,
            int inicioY,
            String prefixoSaidaFrames,
            int passoFrame,
            int corInicio,
            int corFim,
            int deslocamentoNumeracaoFrames,
            String pfx,
            Integer quadrosAnimacaoUniforme) {

        reiniciarHistoricoFrames();

        int alvo = img.getRGB(inicioX, inicioY);
        if (alvo == corInicio && alvo == corFim) {
            return;
        }

        int largura = img.getWidth();
        int altura = img.getHeight();
        int[][] dist = new int[largura][altura];
        for (int x = 0; x < largura; x++) {
            Arrays.fill(dist[x], -1);
        }

        FilaEncadeada<Pixel> fila = new FilaEncadeada<>();
        dist[inicioX][inicioY] = 0;
        fila.enqueue(new Pixel(inicioX, inicioY));

        while (!fila.estaVazia()) {
            Pixel p = fila.dequeue();
            int x = p.x;
            int y = p.y;
            if (x < 0 || y < 0 || x >= largura || y >= altura) {
                continue;
            }
            if (img.getRGB(x, y) != alvo) {
                continue;
            }
            int d = dist[x][y];
            int[] dx = {1, -1, 0, 0};
            int[] dy = {0, 0, 1, -1};
            for (int k = 0; k < 4; k++) {
                int nx = x + dx[k];
                int ny = y + dy[k];
                if (nx < 0 || ny < 0 || nx >= largura || ny >= altura) {
                    continue;
                }
                if (img.getRGB(nx, ny) != alvo) {
                    continue;
                }
                if (dist[nx][ny] == -1) {
                    dist[nx][ny] = d + 1;
                    fila.enqueue(new Pixel(nx, ny));
                }
            }
        }

        int maxD = 0;
        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                if (dist[x][y] >= 0) {
                    maxD = Math.max(maxD, dist[x][y]);
                }
            }
        }

        Color c1 = new Color(corInicio, true);
        Color c2 = new Color(corFim, true);
        int pixelsPintados = 0;
        int indiceFrame = 0;
        int pixelsNaRegiao = 0;
        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                if (dist[x][y] >= 0) {
                    pixelsNaRegiao++;
                }
            }
        }
        int[] marcos = null;
        int proxMarco = 0;
        if (prefixoSaidaFrames != null
                && quadrosAnimacaoUniforme != null
                && quadrosAnimacaoUniforme > 0) {
            marcos = marcosParaQuadros(pixelsNaRegiao, quadrosAnimacaoUniforme);
        }

        if (prefixoSaidaFrames != null) {
            try (GravadorFramesParalelo gravador = new GravadorFramesParalelo(imageIOService)) {
                for (int d = 0; d <= maxD; d++) {
                    for (int x = 0; x < largura; x++) {
                        for (int y = 0; y < altura; y++) {
                            if (dist[x][y] != d) {
                                continue;
                            }
                            float t = maxD <= 0 ? 0f : (float) d / (float) maxD;
                            img.setRGB(x, y, corGradiente(c1, c2, t));
                            pixelsPintados++;
                            if (marcos != null && marcos.length > 0) {
                                while (proxMarco < marcos.length && pixelsPintados >= marcos[proxMarco]) {
                                    indiceFrame++;
                                    String caminho = String.format(
                                            "%s/%s%05d.png",
                                            prefixoSaidaFrames,
                                            pfx,
                                            deslocamentoNumeracaoFrames + indiceFrame);
                                    gravador.submitFrame(img, caminho);
                                    historicoCaminhosFrames.adicionar(caminho);
                                    proxMarco++;
                                }
                            } else if (passoFrame > 0 && pixelsPintados % passoFrame == 0) {
                                indiceFrame++;
                                String caminho = String.format(
                                        "%s/%s%05d.png",
                                        prefixoSaidaFrames,
                                        pfx,
                                        deslocamentoNumeracaoFrames + indiceFrame);
                                gravador.submitFrame(img, caminho);
                                historicoCaminhosFrames.adicionar(caminho);
                            }
                        }
                    }
                }
                if (marcos == null) {
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
            }
        } else {
            for (int d = 0; d <= maxD; d++) {
                for (int x = 0; x < largura; x++) {
                    for (int y = 0; y < altura; y++) {
                        if (dist[x][y] != d) {
                            continue;
                        }
                        float t = maxD <= 0 ? 0f : (float) d / (float) maxD;
                        img.setRGB(x, y, corGradiente(c1, c2, t));
                    }
                }
            }
        }
    }

    private static int corGradiente(Color a, Color b, float t) {
        if (t < 0f) {
            t = 0f;
        }
        if (t > 1f) {
            t = 1f;
        }
        int r = Math.round(a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return rgb(r, g, bl);
    }

    public static int rgb(int r, int g, int b) {
        return new Color(r, g, b).getRGB();
    }
}
