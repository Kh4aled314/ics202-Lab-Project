package kfupm.clinic.ds;

import java.util.ArrayList;
import java.util.List;

/** Students implement. */
public class SinglyLinkedList<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;


    public void addLast(T item) {
        Node<T> newNode = new Node<>(item);

        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }

        size++;
    }


    public List<T> toList() {
        List<T> result = new ArrayList<>();

        Node<T> current = head;
        while (current != null) {
            result.add(current.data);
            current = current.next;
        }

        return result;
    }

    public T removeLast() {
        if (head == null) {
            return null;
        }

        if (head == tail) {
            T data = head.data;
            head = null;
            tail = null;
            size--;
            return data;
        }

        Node<T> current = head;
        while (current.next != tail) {
            current = current.next;
        }

        T data = tail.data;
        current.next = null;
        tail = current;
        size--;

        return data;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
}
