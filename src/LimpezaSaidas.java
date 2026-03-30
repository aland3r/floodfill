import java.io.File;

public final class LimpezaSaidas {

    public static final String SAIDA_PILHA = "saida_pilha.png";
    public static final String SAIDA_FILA = "saida_fila.png";

    public static final String PASTA_ANIMACAO = "saida_animacao";
    public static final String PREFIXO_FRAME_PILHA = "pilha_frame_";
    public static final String PREFIXO_FRAME_FILA = "fila_frame_";
    public static final String PREFIXO_FRAME_SESSAO = "anim_frame_";

    private LimpezaSaidas() {
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

    public static void esvaziarPastaAnimacao() {
        File pastaAnim = new File(PASTA_ANIMACAO);
        if (pastaAnim.isDirectory()) {
            File[] filhos = pastaAnim.listFiles();
            if (filhos != null) {
                LinkedList<File> files = new LinkedList<>();
                for (File f : filhos) {
                    if (f.isFile()) {
                        files.add(f);
                    } else if (f.isDirectory()) {
                        apagarDiretorioRecursivo(f);
                    }
                }
                files.forEach(File::delete);
            }
        }
        if (!pastaAnim.exists()) {
            pastaAnim.mkdirs();
        }
    }

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
}
