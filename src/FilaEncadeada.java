/** Fila FIFO — primeiro a entrar, primeiro a sair. */
public class FilaEncadeada<T> {
    private No<T> head;
    private No<T> tail;

    public FilaEncadeada() {
        this.head = null;
        this.tail = null;
    }

    public void enqueue(T value) {
        No<T> newNode = new No<>(value);
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
