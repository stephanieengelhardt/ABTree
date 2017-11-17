public interface BSTNode<E> {
	int count();

	E data();

	BSTNode<E> left();

	BSTNode<E> parent();

	BSTNode<E> right();

	String toString();
}
