package estruturas;

import molde.No;

/** Fila FIFO — primeiro a entrar, primeiro a sair. */
public class FilaEncadeada<T> {
    private No<T> inicio;
    private No<T> fim;

    public FilaEncadeada() {
        this.inicio = null;
        this.fim = null;
    }

    public void enqueue(T valor) {
        No<T> novo = new No<>(valor);
        if (estaVazia()) {
            inicio = fim = novo;
        } else {
            fim.proximo = novo;
            fim = novo;
        }
    }

    public T dequeue() {
        T v = inicio.valor;
        inicio = inicio.proximo;
        if (inicio == null) {
            fim = null;
        }
        return v;
    }

    public boolean estaVazia() {
        return inicio == null;
    }
}
