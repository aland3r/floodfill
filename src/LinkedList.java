import java.util.function.Consumer;

public class LinkedList<T> { //Declare a classe LinkedList pública e genérica no dígito T
    private Node<T> head; //Declare o início da lista como genérico T
    private Node<T> tail; //Declare o fim da lista como genérico T

    public LinkedList() { //Declare o construtor da lista pública e genérica no dígito
        this.head = null; //Inicialize o início da lista como nulo
        this.tail = null; //Inicialize o fim da lista como nulo
    }

    public boolean isEmpty() { //Declare o método estaVazia público e retorna um boolean
        return head == null; //Retorne true se o início da lista for nulo, caso contrário, retorne false
    }

    public void add(T value) { //Declare o método adicionar público e genérico no dígito T
        Node<T> newNode = new Node<>(value); //Crie um novo nó com o valor passado
        if (isEmpty()) { //Se a lista estiver vazia
            head = tail = newNode; //Defina o início e o fim da lista como o novo nó
        } else { //Se a lista não estiver vazia
            tail.next = newNode; //Defina o próximo nó do fim da lista como o novo nó
            tail = newNode; //Defina o fim da lista como o novo nó
        }
    }

    public void clear() { //Declare o método esvaziar público
        this.head = null; //Inicialize o início da lista como nulo
        this.tail = null; //Inicialize o fim da lista como nulo
    }

    /** Percorre do início ao fim (para efeitos colaterais em cada elemento). */
    public void forEach(Consumer<T> action) {
        for (Node<T> node = head; node != null; node = node.next) {
            action.accept(node.value);
        }
    }
} //Fim da classe LinkedList
