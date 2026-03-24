import java.io.File;

/**
 * Remove saídas de teste no diretório de trabalho (mesmo conjunto que {@code limpar-saidas.ps1}).
 */
public final class LimpezaSaidas {

    public static final String PASTA_ANIMACAO = "saida_animacao";
    public static final String SAIDA_PILHA = "saida_pilha.png";
    public static final String SAIDA_FILA = "saida_fila.png";
    public static final String PREFIXO_FRAME_PILHA = "pilha_frame_";
    public static final String PREFIXO_FRAME_FILA = "fila_frame_";
    /** Na interface Swing: um único fluxo de frames (pilha e fila na mesma sequência). */
    public static final String PREFIXO_FRAME_SESSAO = "anim_frame_";

    private LimpezaSaidas() {
    }

    /**
     * Maior sufixo numérico já usado em arquivos {@code prefixo00001.png} (0 se não houver).
     * Permite continuar a animação após vários preenchimentos na mesma sessão.
     */
    public static int maiorIndiceArquivoFrame(File pasta, String prefixo) {
        if (pasta == null || !pasta.isDirectory()) {
            return 0;
        }
        int max = 0;
        File[] list = pasta.listFiles();
        if (list == null) {
            return 0;
        }
        for (File f : list) {
            if (!f.isFile()) {
                continue;
            }
            String n = f.getName();
            if (!n.startsWith(prefixo) || !n.endsWith(".png")) {
                continue;
            }
            String suf = n.substring(prefixo.length(), n.length() - 4);
            if (!suf.chars().allMatch(Character::isDigit)) {
                continue;
            }
            int v = Integer.parseInt(suf);
            max = Math.max(max, v);
        }
        return max;
    }

    /**
     * Remove todos os arquivos dentro de {@link #PASTA_ANIMACAO} e recria a pasta vazia se necessário.
     * Não remove o diretório em si — em Windows, apagar a pasta inteira costuma falhar se algo ainda referenciar um PNG;
     * assim {@link #maiorIndiceArquivoFrame} volta a 0 e a animação seguinte só mostra os novos preenchimentos.
     */
    public static void esvaziarPastaAnimacao() {
        File pastaAnim = new File(PASTA_ANIMACAO);
        if (pastaAnim.isDirectory()) {
            File[] filhos = pastaAnim.listFiles();
            if (filhos != null) {
                for (File f : filhos) {
                    if (f.isFile()) {
                        f.delete();
                    } else if (f.isDirectory()) {
                        apagarDiretorioRecursivo(f);
                    }
                }
            }
        }
        if (!pastaAnim.exists()) {
            pastaAnim.mkdirs();
        }
    }

    /** Apaga PNGs de saída ({@link #SAIDA_PILHA}, {@link #SAIDA_FILA}) e todos os frames em {@link #PASTA_ANIMACAO}. */
    public static void apagarTodasSaidasDeTeste() {
        File fPilha = new File(SAIDA_PILHA);
        if (fPilha.isFile()) {
            fPilha.delete();
        }
        File fFila = new File(SAIDA_FILA);
        if (fFila.isFile()) {
            fFila.delete();
        }
        esvaziarPastaAnimacao();
    }

    private static void apagarDiretorioRecursivo(File dir) {
        File[] filhos = dir.listFiles();
        if (filhos != null) {
            for (File f : filhos) {
                if (f.isDirectory()) {
                    apagarDiretorioRecursivo(f);
                } else {
                    f.delete();
                }
            }
        }
        dir.delete();
    }
}
