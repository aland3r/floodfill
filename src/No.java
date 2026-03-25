/** Nó de lista encadeada simples (um elo da corrente). */
public class No<T> { //Declare a classe Nó pública e genérica no dígito T
    T valor; //Declare o valor do nó como genérico T
    No<T> proximo; //Declare o próximo nó como genérico T

    public No(T valor) { //Declare o construtor do nó público e genérico no dígito T
        this.valor = valor; //Inicialize o valor do nó com o valor passado
        this.proximo = null; //Inicialize o próximo nó como nulo
    }
} //Fim da classe Nó
