package kfupm.clinic.ds;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;


/**
 * MaxHeap starter.
 *
 * Day-1 safety rule:
 * - Constructors must NEVER throw, so the program can start.
 * - Operations may throw UnsupportedOperationException until students implement them;
 *   the command dispatcher will catch and print [NOT SUPPORTED] instead of crashing.
 */
public class MaxHeap<T> {

    protected final Comparator<T> comparator;

    private Object[] heap;
    private int size;

    public MaxHeap(Comparator<T> comparator) {
        if (comparator == null) throw new IllegalArgumentException("comparator is null");
        this.comparator = comparator;
        this.heap = new Object[16];
        this.size = 0;
    }

    public void push(T item) {
        if (item == null) {
            throw new IllegalArgumentException("item is null");
        }

        ensureCapacity();

        heap[size] = item;
        heapifyUp(size);
        size++;
    }

    public T pop() {
        if (size == 0) {
            return null;
        }

        T root = elementAt(0);

        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;

        if (size > 0) {
            heapifyDown(0);
        }

        return root;
    }

    public T peek() {
        if (size == 0) {
            return null;
        }

        return elementAt(0);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** Non-destructive view for printing. */
    public List<T> toListSnapshot() {
        List<T> snapshot = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            snapshot.add(elementAt(i));
        }

        return snapshot;
    }

    public boolean remove(T item) {
        if (item == null) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (heap[i].equals(item)) {
                heap[i] = heap[size - 1];
                heap[size - 1] = null;
                size--;

                if (i < size) {
                    heapifyDown(i);
                    heapifyUp(i);
                }

                return true;
            }
        }

        return false;
    }

    public int size() {
        return size;
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = parent(index);

            T current = elementAt(index);
            T parent = elementAt(parentIndex);

            if (comparator.compare(current, parent) <= 0) {
                break;
            }

            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    private void heapifyDown(int index) {
        while (true) {
            int leftIndex = leftChild(index);
            int rightIndex = rightChild(index);
            int largestIndex = index;

            if (leftIndex < size &&
                    comparator.compare(elementAt(leftIndex), elementAt(largestIndex)) > 0) {
                largestIndex = leftIndex;
            }

            if (rightIndex < size &&
                    comparator.compare(elementAt(rightIndex), elementAt(largestIndex)) > 0) {
                largestIndex = rightIndex;
            }

            if (largestIndex == index) {
                break;
            }

            swap(index, largestIndex);
            index = largestIndex;
        }
    }

    private int parent(int index) {
        return (index - 1) / 2;
    }

    private int leftChild(int index) {
        return 2 * index + 1;
    }

    private int rightChild(int index) {
        return 2 * index + 2;
    }

    private void swap(int i, int j) {
        Object temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void ensureCapacity() {
        if (size < heap.length) {
            return;
        }

        Object[] newHeap = new Object[heap.length * 2];

        for (int i = 0; i < heap.length; i++) {
            newHeap[i] = heap[i];
        }

        heap = newHeap;
    }

    @SuppressWarnings("unchecked")
    private T elementAt(int index) {
        return (T) heap[index];
    }
}
