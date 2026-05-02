package kfupm.clinic.ds;

import java.util.function.BiConsumer;

/**
 * Students implement AVL tree (balanced BST).
 */
public class AVLTree<K extends Comparable<K>, V> {

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;
        int height;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private Node<K, V> root;

    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }

        root = put(root, key, value);
    }

    public V get(K key) {
        if (key == null) {
            return null;
        }

        Node<K, V> current = root;

        while (current != null) {
            int comparison = key.compareTo(current.key);

            if (comparison == 0) {
                return current.value;
            } else if (comparison < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return null;
    }

    public void remove(K key) {
        if (key == null) {
            return;
        }

        root = remove(root, key);
    }

    /** In-order traversal (sorted by key). */
    public void inOrder(BiConsumer<K, V> visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("visitor is null");
        }

        inOrder(root, visitor);
    }

    /** Returns the smallest key/value (leftmost node), or null if empty. */
    public Entry<K, V> minEntry() {
        if (root == null) {
            return null;
        }

        Node<K, V> current = root;

        while (current.left != null) {
            current = current.left;
        }

        return new Entry<>(current.key, current.value);
    }

    private Node<K, V> put(Node<K, V> node, K key, V value) {
        if (node == null) {
            return new Node<>(key, value);
        }

        int comparison = key.compareTo(node.key);

        if (comparison < 0) {
            node.left = put(node.left, key, value);
        } else if (comparison > 0) {
            node.right = put(node.right, key, value);
        } else {
            node.value = value;
            return node;
        }

        updateHeight(node);
        return rebalance(node);
    }

    private Node<K, V> remove(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int comparison = key.compareTo(node.key);

        if (comparison < 0) {
            node.left = remove(node.left, key);
        } else if (comparison > 0) {
            node.right = remove(node.right, key);
        } else {
            if (node.left == null && node.right == null) {
                return null;
            }

            if (node.left == null) {
                return node.right;
            }

            if (node.right == null) {
                return node.left;
            }

            Node<K, V> successor = minNode(node.right);
            node.key = successor.key;
            node.value = successor.value;
            node.right = remove(node.right, successor.key);
        }

        updateHeight(node);
        return rebalance(node);
    }

    private Node<K, V> minNode(Node<K, V> node) {
        Node<K, V> current = node;

        while (current.left != null) {
            current = current.left;
        }

        return current;
    }

    private void inOrder(Node<K, V> node, BiConsumer<K, V> visitor) {
        if (node == null) {
            return;
        }

        inOrder(node.left, visitor);
        visitor.accept(node.key, node.value);
        inOrder(node.right, visitor);
    }

    private Node<K, V> rebalance(Node<K, V> node) {
        int balance = balanceFactor(node);

        if (balance > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }

            return rotateRight(node);
        }

        if (balance < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }

            return rotateLeft(node);
        }

        return node;
    }

    private Node<K, V> rotateRight(Node<K, V> y) {
        Node<K, V> x = y.left;
        Node<K, V> temp = x.right;

        x.right = y;
        y.left = temp;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    private Node<K, V> rotateLeft(Node<K, V> x) {
        Node<K, V> y = x.right;
        Node<K, V> temp = y.left;

        y.left = x;
        x.right = temp;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    private int height(Node<K, V> node) {
        if (node == null) {
            return 0;
        }

        return node.height;
    }

    private void updateHeight(Node<K, V> node) {
        int leftHeight = height(node.left);
        int rightHeight = height(node.right);

        node.height = 1 + Math.max(leftHeight, rightHeight);
    }

    private int balanceFactor(Node<K, V> node) {
        if (node == null) {
            return 0;
        }

        return height(node.left) - height(node.right);
    }

    public record Entry<K, V>(K key, V value) {}
}
