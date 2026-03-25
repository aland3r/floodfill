import java.util.function.Consumer;

public class ListaEncadeada<T> { //Declare a classe ListaEncadeada pública e genérica no dígito T
    private No<T> inicio; //Declare o início da lista como genérico T
    private No<T> fim; //Declare o fim da lista como genérico T

    public ListaEncadeada() { //Declare o construtor da lista pública e genérica no dígito
        this.inicio = null; //Inicialize o início da lista como nulo
        this.fim = null; //Inicialize o fim da lista como nulo
    }

    public boolean estaVazia() { //Declare o método estaVazia público e retorna um boolean
        return inicio == null; //Retorne true se o início da lista for nulo, caso contrário, retorne false
    }

    public void adicionar(T valor) { //Declare o método adicionar público e genérico no dígito T
        No<T> novo = new No<>(valor); //Crie um novo nó com o valor passado
        if (estaVazia()) { //Se a lista estiver vazia
            inicio = fim = novo; //Defina o início e o fim da lista como o novo nó
        } else { //Se a lista não estiver vazia
            fim.proximo = novo; //Defina o próximo nó do fim da lista como o novo nó
            fim = novo; //Defina o fim da lista como o novo nó
        }
    }

    public void esvaziar() { //Declare o método esvaziar público
        this.inicio = null; //Inicialize o início da lista como nulo
        this.fim = null; //Inicialize o fim da lista como nulo
    }

    /** Percorre do início ao fim (para efeitos colaterais em cada elemento). */
    public void paraCada(Consumer<T> acao) {
        for (No<T> n = inicio; n != null; n = n.proximo) {
            acao.accept(n.valor);
        }
    }
} //Fim da classe ListaEncadeada
