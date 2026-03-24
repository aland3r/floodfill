# Plano de estudos — Resolução de Problemas Estruturados (5 dias → prova na segunda)

**Meta:** preparar arguição/defesa do **PBL01 Flood Fill** e rever o conteúdo de **RA1** alinhado ao plano da disciplina.  
**Prova:** segunda-feira (usa o último dia só para consolidação e descanso leve).

**Materiais de referência (abre na ordem sugerida por dia):**

| Ordem | Ficheiro |
|-------|----------|
| Base | `Informação no Computador.pdf` |
| Estruturas lineares | `Array Dinâmico (ArrayList).pdf` |
| ED nucleares | `Pilha e Fila Encadeada.pdf` |
| ED avançada | `Lista Duplamente Encadeada.pdf` |
| Projeto | `PBL01 - Flood Fill.pdf` |
| Contexto da UC | `Plano de Ensino - Resolução de Problemas Estruturados em Computação.pdf` |

**Caminho base (ajusta se a pasta mudar):**  
`...\26-1\Resolução de Problemas Estruturados em Computação\RA1\`

---

## Visão geral dos 5 dias

| Dia | Foco principal | Ligação ao teu código |
|-----|------------------|------------------------|
| **1** | Representação de dados + visão do plano de ensino | `BufferedImage`, `getRGB`, tipos primitivos vs objetos |
| **2** | Array dinâmico (ArrayList): ideia, amortização, quando usar | Contraste: enunciado pede estruturas **próprias**, não `ArrayList` no núcleo |
| **3** | **Pilha e fila encadeadas** (LIFO/FIFO, nós, ponteiros) | `PilhaEncadeada`, `FilaEncadeada`, DFS vs BFS no `FloodFillService` |
| **4** | **Lista duplamente encadeada** (nó, ant/prox, inserção/remoção) | Comparar com lista simples; `ListaEncadeada` do histórico de frames |
| **5** | **PBL01** (enunciado) + revisão do backend do projeto + simulação oral | `FloodFillService`, `ImageIOService`, frames, `LimpezaSaidas` |

---

## Dia 1 — Informação no computador + Plano de ensino

**Manhã (2–3 h)**

- Lê **`Informação no Computador.pdf`**: bits/bytes, representação de inteiros e texto o que o PDF da disciplina exigir (não precisas decorar tudo, mas saber explicar “como um valor vira padrão de bits na memória” ajuda na oral).
- Folheia **`Plano de Ensino - Resolução de Problemas Estruturados em Computação.pdf`**: objetivos da UC, avaliação, datas — anota **2 frases** sobre o que a disciplina considera “estruturar o problema”.

**Tarde (1–2 h)**

- Liga à prática: abre `ImageIOService` / uso de `BufferedImage` e `getRGB` no teu projeto — explica em voz alta: “a cor é um `int` em ARGB; o flood fill compara esse `int` com a cor alvo”.

**Checklist do dia**

- [ ] Consigo explicar, numa frase, o papel de **tipo** e **representação em memória** no contexto da imagem.
- [ ] Sei onde está no plano de ensino o tema **estruturas de dados** / projeto.

---

## Dia 2 — Array dinâmico (ArrayList)

**Manhã (2–3 h)**

- Estuda **`Array Dinâmico (ArrayList).pdf`**: crescimento do vetor interno, custo amortizado de append, limitações (acesso por índice vs inserção no meio).

**Tarde (1–2 h)**

- **Contraste obrigatório com o PBL:** o enunciado do Flood Fill pede **pilha/fila/lista encadeadas implementadas por vocês**, não `ArrayList` como estrutura do algoritmo. Prepara a frase: “Usei encadeamento porque o trabalho exige; ArrayList seria outra estrutura com outro custo/comportamento.”

**Checklist do dia**

- [ ] Sei explicar **O(1)** amortizado no append vs custo de inserir no meio.
- [ ] Sei justificar **por que** no PBL não usaste `ArrayList` como pilha/fila principal.

---

## Dia 3 — Pilha e fila encadeadas (prioridade máxima)

**Manhã (3 h)**

- Estuda **`Pilha e Fila Encadeada.pdf`**: nó, início/fim da fila, topo da pilha, operações elementares, diagramas no caderno.

**Tarde (2 h)**

- Abre no código: `PilhaEncadeada.java`, `FilaEncadeada.java`, `No.java`.
- Percorre **`FloodFillService.preencherComPilha`** e **`preencherComFila`**:  
  - Pilha → **DFS** (profundidade).  
  - Fila → **BFS** (largura).  
  - Vizinhos **4-conectados** (cima/baixo/esquerda/direita).

**Checklist do dia**

- [ ] Desenhei no papel uma grade pequena e numerei a ordem de visita **pilha** vs **fila** a partir do mesmo pixel.
- [ ] Explico **sem slides** o papel de `pixelSentinela` e dos limites `x, y` na imagem.
- [ ] Sei responder: “O que muda entre pilha e fila se a **área** pintada é a mesma?”

---

## Dia 4 — Lista duplamente encadeada

**Manhã (2–3 h)**

- Estuda **`Lista Duplamente Encadeada.pdf`**: duplo encadeamento, percorrer para trás, custo de remoção se o PDF destacar.

**Tarde (1–2 h)**

- Compara com **`ListaEncadeada`** do projeto (histórico de caminhos dos frames): é **simples**; explica porque não precisas de dupla para esse uso (só percorrer/adicionar no fim — adapta ao que o teu código faz de facto).

**Checklist do dia**

- [ ] Sei dizer **uma vantagem** da lista dupla sobre a simples e **um custo** (memória/extra ponteiro).
- [ ] Relaciono `ListaEncadeada<String>` com o **histórico de PNGs** da animação.

---

## Dia 5 — PBL01 + código + simulação de prova

**Manhã (2–3 h)**

- Lê com atenção **`PBL01 - Flood Fill.pdf`**: requisitos, entregáveis, cores, animação, o que a professora considera “correto”.
- Revisa só o **backend**: `FloodFillService` (pilha, fila, gradiente opcional, frames, `quadrosAnimacaoUniforme`, `salvarFrameFinalSeNecessario`), `GravadorFramesParalelo`, `LimpezaSaidas`, `MainConsole` vs Swing.

**Tarde (2 h)**

- Simulação **oral** (grava no telemóvel ou fala ao espelho):  
  1. Problema em 30 s.  
  2. Por que pilha **e** fila.  
  3. Complexidade em função do número de pixels da região.  
  4. Onde está paralelismo (só gravação de frames, não o fill).  
  5. Uma limitação honesta (ex.: muitos pushes na pilha sem `visited` explícito — se for o teu caso).

**Noite (leve)**

- Folheia RA1 só nos **títulos** dos PDFs para refrescar memória; dorme cedo.

**Checklist do dia**

- [ ] Consigo percorrer o fluxo **do clique** até ao **último PNG** gravado.
- [ ] Tenho 5 respostas **decorridas** (não lidas) sobre o projeto.

---

## Mapa rápido: PDF → tema de prova

| PDF | Pergunta típica |
|-----|-----------------|
| Informação no computador | Representação de valores; relação com dados na imagem |
| ArrayList | Crescimento dinâmico; diferença para lista encadeada |
| Pilha e fila | LIFO/FIFO; aplicação direta no flood fill |
| Lista dupla | Por que dois ponteiros; operações |
| PBL01 | Requisitos do trabalho; o que implementaste |
| Plano de ensino | Objetivos da UC; encaixe do PBL |

---

## Nota

Este plano foi montado pelos **títulos** dos teus ficheiros e pelo **README do repositório Flood Fill**. Ao estudar cada PDF, **subtitui** os blocos “Manhã/Tarde” com os **números de página ou seções** que o documento usar, para ficares com um guia 100% alinhado ao material da professora.

Boa prova na segunda.
