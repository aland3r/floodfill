/**
 * Lista linear encadeada simples (inserção no fim). Usada no histórico de caminhos dos frames.
 * Pilha/Fila são classes separadas.
 */
public class ListaEncadeada<T> {
    private No<T> inicio;
    private No<T> fim;

    public ListaEncadeada() {
        this.inicio = null;
        this.fim = null;
    }

    public boolean estaVazia() {
        return inicio == null;
    }

    public void adicionar(T valor) {
        No<T> novo = new No<>(valor);
        if (estaVazia()) {
            inicio = fim = novo;
        } else {
            fim.proximo = novo;
            fim = novo;
        }
    }

    public void esvaziar() {
        this.inicio = null;
        this.fim = null;
    }
}
