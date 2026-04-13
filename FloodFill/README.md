# Flood Fill - Versão final (defesa)

Projeto em Java que preenche regiões de uma imagem PNG com dois métodos:
- `fillWithStack` (DFS, usando pilha encadeada)
- `fillWithQueue` (BFS, usando fila encadeada)

O foco da implementação é demonstrar estruturas próprias, processamento por pixel e comparação visual entre pilha e fila.

## O que importa na defesa

### 1) Estruturas próprias (sem coleções prontas)
- `Node<T>`
- `LinkedStack<T>`
- `LinkedQueue<T>`
- `LinkedList<T>` (usada no suporte de limpeza de saídas)

Não são usadas `Stack`, `Queue` ou `LinkedList` da biblioteca Java para o algoritmo principal.

### 2) Algoritmo de Flood Fill
Em `FloodFillService`:
- Lê cor-alvo inicial: `targetColor = image.getRGB(firstX, firstY)`.
- Enquanto houver itens na estrutura (pilha ou fila), processa um `Pixel`.
- Valida limites da imagem (`x`, `y`, `width`, `height`).
- Só pinta quando a cor atual é igual a `targetColor`.
- Pinta com `image.setRGB(x, y, fillColor)`.
- Insere 4 vizinhos: direita, esquerda, baixo, cima.

Resumo de comportamento:
- Pilha (LIFO): caminho mais profundo primeiro.
- Fila (FIFO): expansão por camadas.

### 3) Animação e saídas
- Frames em `saida_animacao/` com prefixo `anim_frame_`.
- Imagens finais:
  - `saida_pilha.png`
  - `saida_fila.png`
- Gravação de frames com `GravadorFramesParalelo` (threads para I/O), sem alterar a lógica do preenchimento.

### 4) Interface (Swing)
`FloodFillSwingUI`:
- Carrega `entrada.png`.
- Usuário escolhe cor.
- Usuário clica no ponto inicial.
- Botão executa pilha ou fila.
- Botão reproduz animação.
- Botão restaurar limpa saídas e volta ao estado inicial.

## Estrutura de arquivos (principal)

Em `FloodFill/src`:
- `Main.java`
- `FloodFillSwingUI.java`
- `FloodFillService.java`
- `ImageIOService.java`
- `GravadorFramesParalelo.java`
- `ReprodutorAnimacao.java`
- `LimpezaSaidas.java`
- `Pixel.java`
- `Node.java`
- `LinkedStack.java`
- `LinkedQueue.java`
- `LinkedList.java`

Arquivos de entrada/saída:
- `entrada.png`
- `src/paint-bucket-icon.png`
- `saida_pilha.png` (gerado)
- `saida_fila.png` (gerado)
- `saida_animacao/` (gerado)

## Como executar

1. Abra o projeto no IntelliJ.
2. Garanta o working directory na pasta `FloodFill`.
3. Execute `Main`.
4. Na janela:
   - escolha uma cor,
   - clique na imagem,
   - execute com pilha ou fila,
   - use "Ver animação" para mostrar os frames.

## Roteiro rápido para apresentar (2-4 minutos)

1. Mostrar a imagem inicial (`entrada.png`).
2. Explicar que existem dois métodos: pilha (DFS) e fila (BFS).
3. Executar um preenchimento com pilha.
4. Executar outro com fila.
5. Abrir animação e comentar a diferença do padrão de expansão.
6. Fechar destacando:
   - estruturas próprias,
   - controle de limites/cor,
   - saídas finais + frames.

## Observações finais

- Código sem comentários no fonte (versão de entrega).
- README focado nos pontos técnicos essenciais para banca.
