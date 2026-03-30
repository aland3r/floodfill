import java.util.function.Consumer;

public class LinkedList<T> {
    private Node<T> head;
    private Node<T> tail;

    public LinkedList() {
        this.head = null;
        this.tail = null;
    }

    public void add(T value) {
        Node<T> newNode = new Node<>(value);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    public void clear() {
        this.head = null;   
        this.tail = null;
    }

    public void forEach(Consumer<T> action) {
        for (Node<T> node = head; node != null; node = node.next) {
            action.accept(node.value);
        }
    }
}
