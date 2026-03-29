
public class LinkedStack<T> { //Declare a classe pública e genérica no dígito
    private Node<T> top; //Declare o topo da pilha como genérico T

    public LinkedStack() { //Declare o construtor da pilha pública e genérica no dígito
        this.top = null; //Inicialize o topo da pilha como nulo
    }

    public void push(T value) { //Declare o método push público e genérico no dígito T
        Node<T> newNode = new Node<>(value); //Crie um novo nó com o valor passado
        newNode.next = top; //Defina o próximo nó do novo nó como o topo da pilha
        top = newNode; //Defina o topo da pilha como o novo nó
    }

    public T pop() { //Declare o método pop público e genérico no dígito T
        T value = top.value; //Defina o valor do topo da pilha como o valor do topo da pilha
        top = top.next; //Defina o topo da pilha como o próximo nó do topo da pilha
        return value; //Retorne o valor do topo da pilha
    }

    public boolean isEmpty() { //Declare o método estaVazia público e retorna um boolean
        return top == null; //Retorne true se o topo da pilha for nulo, caso contrário, retorne false
    }
} //Fim da classe LinkedStack
