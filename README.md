# Flood Fill — PBL01 (PUCPR)

## Requisitos do enunciado (checklist)

| Requisito | Implementação |
|-----------|----------------|
| PNG, cores sólidas recomendadas | Swing: `entrada.png` no repositório; CLI: argumento |
| `File` e `BufferedImage` | `ImageIOService` + `java.io.File` |
| Imagem de entrada e **saída** | `saida_pilha.png`, `saida_fila.png` |
| **Animação** | CLI: `pilha_frame_*.png` / `fila_frame_*.png`; Swing: `anim_frame_*.png` (sequência única, pilha+fila) |
| Pilha e **Fila próprias** | `PilhaEncadeada`, `FilaEncadeada` |
| **Lista** encadeada | `ListaEncadeada` (histórico dos caminhos dos frames) |
| Não usar `Stack`/`Queue`/`LinkedList` do Java | — |
| POO, não tudo na `Main` | Serviços + estruturas em classes |
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
5. **Ver animação** reproduz todos os `anim_frame_*.png` em ordem (**pilha e fila na mesma sequência**).  
6. **Restaurar** volta a imagem, zera o ponto e apaga saídas de teste (como `limpar-saidas`).  
7. **Vários preenchimentos:** cada Pilha/Fila parte do estado atual; os frames **seguem a numeração** em `anim_frame_*.png` até **Restaurar**.

A imagem na área central **cabe inteira**, usa até **82%** da largura/altura do painel (sangria branca em volta), **pode ampliar** imagens pequenas; o clique continua alinhado ao retângulo desenhado. Interpolação **nearest-neighbor** mantém pixel art nítido. A mesma lógica de escala/sangria vale na janela **Ver animação**.

## Ver a animação

Na **Swing**: **Ver animação** usa `anim_frame_*.png`. No **`--cli`**, continuam existindo `pilha_frame_*` e `fila_frame_*` separados.

**Dica:** em `FloodFillSwingApp`, constante `PASSO_FRAME` (padrão **20**). No **`--cli`**, ajuste `PASSO_FRAME` em `MainConsole.java`.

### Multithreading (frames)

A gravação dos PNG em `saida_animacao/` pode usar **`GravadorFramesParalelo`** (`ExecutorService`) no serviço de flood fill. O preenchimento em si continua sequencial no `BufferedImage`.

Para o enunciado (cor **123, 45, 167**), use o modo **`Main --cli`** ou alinhe a demonstração com o professor.

## Limpar saídas do último teste

Na pasta **`FloodFill`**, apague manualmente `saida_pilha.png`, `saida_fila.png` e a pasta **`saida_animacao`**, ou execute **`limpar-saidas.bat`** (Windows) / **`limpar-saidas.ps1`**.

## Modo console

Argumentos: `--cli entrada.png 10 10`
