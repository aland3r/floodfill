# Flood Fill — PBL01 (PUCPR)

O código-fonte fica **direto em `FloodFill/src`** (pacote default): um `.java` por classe, sem subpastas de pacote — o `Main` chama `FloodFillSwingUI.iniciar()` sem `import` entre classes do projeto.

## Requisitos do enunciado (checklist)

| Requisito | Implementação |
|-----------|----------------|
| PNG, cores sólidas recomendadas | `entrada.png` no repositório |
| `File` e `BufferedImage` | `ImageIOService` + `java.io.File` |
| Imagem de entrada e **saída** | `saida_pilha.png`, `saida_fila.png` |
| **Animação** | `anim_frame_*.png` (sequência única, pilha+fila) |
| Pilha e **Fila próprias** | `PilhaEncadeada`, `FilaEncadeada` |
| **Lista** encadeada | `ListaEncadeada` (ex.: enfileira arquivos a apagar em `LimpezaSaidas`) |
| Não usar `Stack`/`Queue`/`LinkedList` do Java | — |
| POO, não tudo na entrada do programa | Serviços + estruturas em classes |
| Cor **RGB (123, 45, 167)** | `FloodFillService.COR_PREENCHIMENTO` |
| Comentário `// algoritmo validado automaticamente` | No início dos métodos de preenchimento |
| Nomes / controle do enunciado | `pixelsPintados` (passos), `filaPrimariaExecucao`; limites com `x,y` na grade |

## Rodar (IntelliJ)

1. **Working directory:** pasta `FloodFill` (onde ficam `entrada.png`, saídas). Com isso, **`entrada.png` é carregada sozinha** ao abrir a janela (não precisa usar “Abrir imagem…” se o arquivo já estiver no projeto).
2. Execute **`Main`**.

### Ordem na interface (Swing enxuto)

1. **`entrada.png`** na pasta de execução (`FloodFill/`). Ícone do cursor: **`src/paint-bucket-icon.png`** (única cópia necessária).  
2. Escolher uma das **4 cores** (botões coloridos).  
3. **Clicar na imagem** na região a preencher.  
4. **Preencher (pilha)** ou **Preencher (fila)**.  
5. **Ver animação** reproduz todos os `anim_frame_*.png` em ordem — na mesma sessão, **pilha e fila** podem ficar **numa sequência só** (numeração continua entre um fill e o próximo).  
6. **Restaurar** volta a imagem, zera o ponto e apaga saídas de teste (inclui esvaziar `saida_animacao`).  
7. **Fluxo típico (duas áreas):** preencher uma região com **pilha**, outra com **fila**, depois **Ver animação**; um terceiro fill seguiria anexando frames até **Restaurar**.

A imagem na área central **cabe inteira**, usa até **82%** da largura/altura do painel (sangria branca em volta), **pode ampliar** imagens pequenas; o clique continua alinhado ao retângulo desenhado. Interpolação **nearest-neighbor** mantém pixel art nítido. A mesma lógica de escala/sangria vale na janela **Ver animação**.

## Ver a animação

**Ver animação** usa `anim_frame_*.png`.

**Dica:** em `FloodFillSwingUI`, constante `PASSO_FRAME` (padrão **20**).

### Multithreading (frames)

A gravação dos PNG em `saida_animacao/` pode usar **`GravadorFramesParalelo`** (`ExecutorService`) no serviço de flood fill. O preenchimento em si continua sequencial no `BufferedImage`.

## Limpar saídas do último teste

Na pasta **`FloodFill`**, apague manualmente `saida_pilha.png`, `saida_fila.png` e a pasta **`saida_animacao`**, ou execute **`limpar-saidas.bat`** (Windows) / **`limpar-saidas.ps1`**.
