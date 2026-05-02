package kfupm.clinic.ds;

import java.util.List;
import java.util.ArrayList;

/** Students implement. */
public class LinkedQueue<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> front;
    private Node<T> rear;
    private int size;

    public void enqueue(T item) {
        Node<T> newNode = new Node<>(item);

        if (front == null) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }

        size++;
    }

    public T dequeue() {
        if (front == null) {
            return null;
        }

        T value = front.data;
        front = front.next;
        size--;

        if (front == null) {
            rear = null;
        }

        return value;
    }

    public boolean isEmpty() {
        return front == null;
    }

    /** Non-destructive view for printing. */
    public List<T> toList() {
        List<T> result = new ArrayList<>();

        Node<T> current = front;
        while (current != null) {
            result.add(current.data);
            current = current.next;
        }

        return result;
    }

    public void addFirst(T item) {
        Node<T> newNode = new Node<>(item);

        if (front == null) {
            front = newNode;
            rear = newNode;
        } else {
            newNode.next = front;
            front = newNode;
        }

        size++;
    }

    public T removeLast() {
        if (front == null) {
            return null;
        }

        if (front == rear) {
            T value = front.data;
            front = null;
            rear = null;
            size--;
            return value;
        }

        Node<T> current = front;
        while (current.next != rear) {
            current = current.next;
        }

        T value = rear.data;
        current.next = null;
        rear = current;
        size--;

        return value;
    }

    public int size() {
        return size;
    }
}
