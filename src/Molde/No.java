package molde;

/** Nó de lista encadeada simples (um elo da corrente). Campos públicos para uso em estruturas fora do pacote. */
public class No<T> {
    public T valor;
    public No<T> proximo;

    public No(T valor) {
        this.valor = valor;
        this.proximo = null;
    }
}
