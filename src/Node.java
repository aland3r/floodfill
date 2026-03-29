public class Node<T> { //Declare a classe Nó pública e genérica no dígito T
    T value; //Declare o valor do nó como genérico T
    Node<T> next; //Declare o próximo nó como genérico T

    public Node(T value) { //Declare o construtor do nó público e genérico no dígito T
        this.value = value; //Inicialize o valor do nó com o valor passado
        this.next = null; //Inicialize o próximo nó como nulo
    }
} //Fim da classe Node
