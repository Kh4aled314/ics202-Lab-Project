package kfupm.clinic.ds;

/** Students implement. */
public class LinkedStack<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> top;
    private int size;


    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = top;
        top = newNode;
        size++;

    }

    public T pop() {
        if (top == null) {
            return null;
        }

        T value = top.data;
        top = top.next;
        size--;

        return value;
    }

    public T peek() {
        if (top == null) {
            return null;
        }

        return top.data;

    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }
}
