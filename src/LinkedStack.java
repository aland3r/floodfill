/** Pilha LIFO — último a entrar, primeiro a sair. */
public class LinkedStack<T> { //<T> é o espaço reservado (placeholder) para o tipo de dado que a pilha vai armazenar
    private Node<T> top; //Topo da pilha com tipo de nó genérico

    public LinkedStack() { //Construtor da pilha genérica
        this.top = null; //Inicialize o topo da pilha como nulo
    }

    public void push(T value) { //método para adicionar um novo elemento no topo da pilha
        Node<T> newNode = new Node<>(value); // novo nó que armazena o elemento recebido em push (tipo T; no flood fill, T é Pixel)
        newNode.next = top; //Defina o próximo nó do novo nó como o topo da pilha (next foi declarado na classe Node)
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
}