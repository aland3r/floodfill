/** Nó de lista encadeada simples (um elo da corrente). */
public class No<T> { //Declare a classe Nó pública e genérica no dígito T
    T value; //Declare o valor do nó como genérico T
    No<T> next; //Declare o próximo nó como genérico T

    public No(T value) { //Declare o construtor do nó público e genérico no dígito T
        this.value = value; //Inicialize o valor do nó com o valor passado
        this.next = null; //Inicialize o próximo nó como nulo
    }
} //Fim da classe Nó
