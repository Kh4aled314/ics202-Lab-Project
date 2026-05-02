package kfupm.clinic.ds;

/**
 * Students implement a hash table.
 * Recommended collision handling: separate chaining.
 */
public class HashTable<K, V> {

    private static final int INITIAL_TABLE_SIZE = 13;
    private static final double MAX_LOAD_FACTOR = 1.0;

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V>[] table;
    private int size;

    @SuppressWarnings("unchecked")
    public HashTable() {
        table = (Node<K, V>[]) new Node[INITIAL_TABLE_SIZE];
        size = 0;
    }

    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }

        int index = hash(key);

        Node<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }

            current = current.next;
        }
        Node<K, V> newNode = new Node<>(key, value);
        newNode.next = table[index];
        table[index] = newNode;
        size++;

        if (loadFactor() > MAX_LOAD_FACTOR) {
            rehash();
        }
    }

    public V get(K key) {
        if (key == null) {
            return null;
        }

        int index = hash(key);

        Node<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }

            current = current.next;
        }

        return null;
    }

    public V remove(K key) {
        if (key == null) {
            return null;
        }

        int index = hash(key);

        Node<K, V> current = table[index];
        Node<K, V> previous = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (previous == null) {
                    table[index] = current.next;
                } else {
                    previous.next = current.next;
                }

                size--;
                return current.value;
            }

            previous = current;
            current = current.next;
        }

        return null;
    }

    public int size() {
        return size;
    }

    private double loadFactor() {
        return (double) size / table.length;
    }

    private int hash(K key) {
        int hashValue = 0;

        if (key instanceof Number number) {
            hashValue = number.intValue();
        } else {
            String s = key.toString();

            for (int i = 0; i < s.length(); i++) {
                hashValue += s.charAt(i);
            }
        }

        hashValue = hashValue % table.length;

        if (hashValue < 0) {
            hashValue += table.length;
        }

        return hashValue;
    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        Node<K, V>[] oldTable = table;

        int newTableSize = nextPrime(oldTable.length * 2);
        table = (Node<K, V>[]) new Node[newTableSize];

        int oldSize = size;
        size = 0;

        for (int i = 0; i < oldTable.length; i++) {
            Node<K, V> current = oldTable[i];

            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }

        size = oldSize;
    }

    private int nextPrime(int number) {
        while (!isPrime(number)) {
            number++;
        }

        return number;
    }

    private boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }

        if (number == 2) {
            return true;
        }

        if (number % 2 == 0) {
            return false;
        }

        for (int i = 3; i * i <= number; i += 2) {
            if (number % i == 0) {
                return false;
            }
        }

        return true;
    }
}
