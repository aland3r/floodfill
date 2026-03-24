/** Pilha LIFO — último a entrar, primeiro a sair. */
public class PilhaEncadeada<T> {
    private No<T> topo;

    public PilhaEncadeada() {
        this.topo = null;
    }

    public void push(T valor) {
        No<T> novo = new No<>(valor);
        novo.proximo = topo;
        topo = novo;
    }

    public T pop() {
        if (estaVazia()) {
            throw new IllegalStateException("Pilha vazia");
        }
        T v = topo.valor;
        topo = topo.proximo;
        return v;
    }

    public boolean estaVazia() {
        return topo == null;
    }
}
