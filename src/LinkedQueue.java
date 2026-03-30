public class LinkedQueue<T> {
    private Node<T> head;
    private Node<T> tail;

    public LinkedQueue() {
        this.head = null;
        this.tail = null;
    }

    public void enqueue(T value) {
        Node<T> newNode = new Node<>(value);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    public T dequeue() {
        T value = head.value;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        return value;
    }

    public boolean isEmpty() {
        return head == null;
    }
}
