import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;


/**
 * @author Stephanie Engelhardt
 */
public class ABTreeSet<E extends Comparable<? super E>> extends AbstractSet<E> {
	

	final class ABTreeIterator implements Iterator<E> {
		private Node current;
		private Node pending;

		ABTreeIterator() {
			current=root;
			if(current==null)
				return;
			while(current.left!=null)
				current=current.left;
		}

		@Override
		public boolean hasNext() {
			return current!=null;
		}

		@Override
		public E next() {
			if(!hasNext())
				throw new NoSuchElementException();
			pending=current;
			if(current.right!=null){
				current=current.right;
				while(current.left!=null)
					current=current.left;
				return pending.data;
			}
			else{
				while(true){
					if(current.parent==null){
						current=null;
						return pending.data;
					}
					if(current.parent.left==current){
						current=current.parent;
						return pending.data;
					}
					current=current.parent;
				}
				
			}
		}

		@Override
		public void remove() {
			if(pending==null)
				throw new IllegalStateException();
			if (pending.left!=null && pending.right!=null)
				current=pending;
			unlinkNode(pending);
			pending=null;

		}
	}

	final class Node implements BSTNode<E> {
		private E data;
		private Node left;
		private Node right;
		private Node parent;
		private int count;

		Node(E data) {
			this.data=data;
			left=null;
			right=null;
			parent=null;
			count=1;
		}

		/**
		 * Returns the number of nodes in that subtree, including the node itself
		 */
		@Override
		public int count() {
			return count;
		}

		@Override
		public E data() {
			return data;
		}

		@Override
		public BSTNode<E> left() {
			return left;
		}

		@Override
		public BSTNode<E> parent() {
			return parent;
		}

		@Override
		public BSTNode<E> right() {
			return right;
		}

		@Override
		public String toString() {
			return data.toString();
		}
	}

	private boolean selfbalance;
	private int top;
	private int bottom;
	private int size;
	private Node root;
	private List<BSTNode<E>> inorder;
	private List<BSTNode<E>> preorder;

	/**
	 * Default constructor. Builds a non-self-balancing tree.
	 */
	public ABTreeSet() {
		selfbalance=false;
		size=0;
		root=null;
		

	}

	/**
	 * If <code>isSelfBalancing</code> is <code>true</code> <br>
	 * builds a self-balancing tree with the default value alpha = 2/3.
	 * <p>
	 * If <code>isSelfBalancing</code> is <code>false</code> <br>
	 * builds a non-self-balancing tree.
	 * 
	 * @param isSelfBalancing
	 */
	public ABTreeSet(boolean isSelfBalancing) {
		selfbalance=isSelfBalancing;
		if(selfbalance){
			top=2;
			bottom=3;
		}
		root=null;
		size=0;

	}

	/**
	 * If <code>isSelfBalancing</code> is <code>true</code> <br>
	 * builds a self-balancing tree with alpha = top / bottom.
	 * <p>
	 * If <code>isSelfBalancing</code> is <code>false</code> <br>
	 * builds a non-self-balancing tree (top and bottom are ignored).
	 * 
	 * @param isSelfBalancing
	 * @param top
	 * @param bottom
	 * @throws IllegalArgumentException
	 *             if (1/2 < alpha < 1) is false
	 */
	public ABTreeSet(boolean isSelfBalancing, int top, int bottom) {
		selfbalance=isSelfBalancing;
		if(selfbalance){
			this.top=top;
			this.bottom=bottom;
		}
		root=null;
		size=0;

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws NullPointerException
	 *             if e is null.
	 */
	@Override
	public boolean add(E e) {
		if(e==null)
			throw new NullPointerException();
		if(root==null){
			root=new Node(e);
			root.count=1;
			size++;
			return true;
			
		}
		Node current=root;
		while(true){
			int comp=current.data.compareTo(e);
			if(comp==0){
				return false;
			}
			else if(comp>0){
				if(current.left!=null)
					current=current.left;
				else{
					Node node=new Node(e);
					node.parent=current;
					current.left=node;
					size++;
					updateCount(node);
					Node tester=node;
					Boolean unbalanced=false;
					Node highestUnbalanced=null;
					while(tester.parent()!=null){
						tester=tester.parent;
						tester.count=tester.count+1;
						//Test for inequality
						if(selfbalance){
							if(tester.left!=null && tester.right!=null){
								if(((tester.left.count()*bottom>=size*top)||(tester.right.count()*bottom>=size*top))){
									unbalanced=true;
									highestUnbalanced=tester;
								}
							}
							else if (tester.left!=null && tester.right==null){
								if(tester.left.count()*bottom>=size*top){
									unbalanced=true;
									highestUnbalanced=tester;
								}
							}
							else if (tester.left==null && tester.right!=null){
								if(tester.right.count()*bottom>=size*top){
									unbalanced=true;
									highestUnbalanced=tester;
								}
							}
							
						}
					}
					if(unbalanced)
						rebalance(highestUnbalanced);
					
					return true;
				}
			}
			else{
				if(current.right!=null)
					current=current.right;
				else{
					Node node=new Node(e);
					node.parent=current;
					current.right=node;
					size++;
					updateCount(node);
					Node tester=node;
					Boolean unbalanced=false;
					Node highestUnbalanced=null;
					while(tester.parent()!=null){
						tester=tester.parent;
						tester.count=tester.count+1;
						if(selfbalance){
							if(tester.left!=null && tester.right!=null){
								if(((tester.left.count()*bottom>=size*top)||(tester.right.count()*bottom>=size*top))){
									unbalanced=true;
									highestUnbalanced=tester;
								}
							}
							else if (tester.left!=null && tester.right==null){
								if(tester.left.count()*bottom>=size*top){
									unbalanced=true;
									highestUnbalanced=tester;
								}
							}
							else if (tester.left==null && tester.right!=null){
								if(tester.right.count()*bottom>=size*top){
									unbalanced=true;
									highestUnbalanced=tester;
								}
							}
						}
					}
					if(unbalanced)
						rebalance(highestUnbalanced);
				return true;
				}
			}	
		}
	}

	/**
	 * Looks for a given object within 
	 * the tree and returns if it exists
	 * within the tree or not
	 * @return boolean of if the given object exists within the tree
	 */
	@Override
	public boolean contains(Object o) {
		E key= (E) o;
		return getBSTNode(key)!=null;
	}

	/**
	 * @param e
	 * @return BSTNode that contains e, null if e does not exist
	 */
	public BSTNode<E> getBSTNode(E e) {
		Node current=root;
		Node compare=new Node(e);
		while(current!=null){
			int comp=current.data.compareTo(compare.data());
			if(comp ==0)
				return current;
			else if (comp>0)
				current=current.left;
			else
				current=current.right;
		}
		return null;
	}

	/**
	 * Returns an in-order list of all nodes in the given sub-tree.
	 * 
	 * @param root
	 * @return an in-order list of all nodes in the given sub-tree.
	 */
	public List<BSTNode<E>> inorderList(BSTNode<E> root) {
		inorder=new ArrayList<BSTNode<E>>();
		inorderHelper(root);
		return inorder;
	}
	
	/**
	 * Helper method that recursively sorts subtrees of the overall tree in order
	 * @param root of the subtree
	 */
	private void inorderHelper(BSTNode<E> root){
		if(root==null)
			return;
		if(root.left()!=null)
			inorderHelper(root.left());
		inorder.add(root);
		if(root.right()!=null)
			inorderHelper(root.right());
	}

	/**
	 * Creates an iterator that goes through the 
	 * tree in order
	 * @return iterator<E> that can iteratate through the tree
	 */
	@Override
	public Iterator<E> iterator() {
		return new ABTreeIterator();
	}

	/**
	 * Returns an pre-order list of all nodes in the given sub-tree.
	 * @param root
	 * @return an pre-order list of all nodes in the given sub-tree.
	 */
	public List<BSTNode<E>> preorderList(BSTNode<E> root) {
		preorder=new ArrayList<BSTNode<E>>();
		preorderHelp(root);
		return preorder;
		
	}
	/**
	 * Helper method that recursively sorts subtrees of the 
	 * overall tree in preorder
	 * @param root
	 */
	private void preorderHelp(BSTNode<E> root){
		preorder.add(root);
		if(root.left()!=null)
			preorderHelp(root.left());
		if(root.right()!=null)
			preorderHelp(root.right());	
	}

	/**
	 * Performs a re-balance operation on the subtree rooted at the given node.
	 * @param bstNode
	 */
	public void rebalance(BSTNode<E> bstNode) {
		inorderList(bstNode);
		boolean left=false;
		if(bstNode.parent()!=null)
			if(bstNode.parent().left()==bstNode)
				left=true;
		root=null;
		rebalanceHelper(0,bstNode.count()-1);
		if(bstNode.parent()!=null){
			root.parent=(ABTreeSet<E>.Node) bstNode.parent();
			if(left)
				root.parent.left=root;
			else
				root.parent.right=root;
		}
	}

	
	/**
	 * Helper method to rebalance a given subtree of the overall tree
	 */
	private void rebalanceHelper(int start, int end){
		int middle=(start+end)/2;
		add((inorder.get(middle).data()));
		if(middle!=start)
			rebalanceHelper(start, middle-1);
		if(middle!=end)
			rebalanceHelper(middle+1,end);
	}
	
	/**
	 * Removes a node from the tree, and if the tree is selfbalancing,
	 * checks to see whether any nodes along the path of the node removed
	 * has become unbalanced. The node highest to the root that is unbalanced
	 * it the node that has rebalanced call on it 
	 * @return boolean if it was able to remove the object from the tree or not
	 */
	@Override
	public boolean remove(Object o) {
		E key= (E) o;
		Node n=(ABTreeSet<E>.Node) getBSTNode(key);
		if(n==null)
			return false;
		Node parent=n.parent;
		unlinkNode(n);
		Boolean unbalanced=false;
		Node highestUnbalanced = null;
		while(parent!=null){
			if(selfbalance){
				if(parent.left!=null && parent.right!=null){
					if(((parent.left.count()*bottom>=size*top)||(parent.right.count()*bottom>=size*top))){
						unbalanced=true;
						highestUnbalanced=parent;
					}
				}
				else if (parent.left!=null &&parent.right==null){
					if(parent.left.count()*bottom>=size*top){
						unbalanced=true;
						highestUnbalanced=parent;
					}
				}
				else if (parent.left==null && parent.right!=null){
					if(parent.right.count()*bottom>=size*top){
						unbalanced=true;
						highestUnbalanced=parent;
					}
				}
			
			}
			parent=parent.parent;
		}
		if(unbalanced)
			rebalance(highestUnbalanced);	
		return true;
	}
	


	/**
	 * Returns the root of the tree.
	 * 
	 * @return the root of the tree.
	 */
	public BSTNode<E> root() {
		return root;
	}
	/**
	 * Allows the user to change the functionality of the tree by 
	 * making it self balancing
	 * @param isSelfBalance
	 */
	public void setSelfBalance(boolean isSelfBalance) {
		this.selfbalance=isSelfBalance;

	}
	/**
	 * Returns the number of nodes in the tree
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Finds the successor of a node within the tree
	 * @param node
	 * @return successor of given node
	 */
	public BSTNode<E> successor(BSTNode<E> node) {
		if(node.right()!=null){
			Node current=(ABTreeSet<E>.Node) node.right();
			while(current.left()!=null){
				current=(ABTreeSet<E>.Node) current.left();
			}
			return current;
		}
		Node n=(ABTreeSet<E>.Node) node.parent();
		while(n!=null && node==n.right()){
				node=n;
				n=(ABTreeSet<E>.Node) n.parent();
		}
		return n;
	}

	/**
	 * Makes a string of a given tree 
	 * @return sting of the tree
	 */
	@Override
	public String toString() {
		StringBuilder build= new StringBuilder();
		level(root,build,0);
		return build.toString();
	}
	
	/**
	 * Helper method to recursively make a string of the tree
	 * @param root
	 * @param height
	 * @return
	 */
	private void level(Node root, StringBuilder build, int height){
		for(int i=0; i<height; i++){
			build.append("    ");
		}
		if(root==null){
			build.append("null" + "\n");
			return;
		}
		build.append(root.data.toString() + "\n");
		if(root.left!=null || root.right!=null){
			level(root.left, build, height+1);
			level(root.right, build, height+1);
		}
	}

	/**
	 * Helper method that unlinks the
	 * given node from the tree
	 * @param n node that is going to be unlinked from the tree
	 */
	protected void unlinkNode(Node n){
		Boolean leftChild=false;
		if(n.parent!=null)
			if(n.parent.left()!=null)
				leftChild=(n.parent.left.data.compareTo(n.data)==0);
		if(n.right()==null&& n.left()==null){
			if(leftChild)
				n.parent.left=null;
			else
				n.parent.right=null;
			Node parent=n.parent;
			while(parent!=null){
				updateCount(parent);
				parent=parent.parent;
			}
			n.parent=null;
		}
		else if(n.right()==null && n.left()!=null){
			if(leftChild){
				n.parent.left=n.left;
			}
			else
				n.parent.right=n.right;
			n.left.parent=n.parent;
			Node parent=n.parent;
			while(parent!=null){
				updateCount(parent);
				parent=parent.parent;
			}
			n.parent=null;
			n.left=null;
		}
		else if(n.right()!=null && n.left()==null){
			if(leftChild)
				n.parent.left=n.right;
			else
				n.right.parent=n.parent;
			Node parent=n.parent;
			while(parent!=null){
				updateCount(parent);
				parent=parent.parent;
			}
			n.right=null;
			n.parent=null;
		}
		else{
			 Node s = (ABTreeSet<E>.Node) successor(n);
		     n.data = s.data;
		     unlinkNode(s);
		}
	}
	
	/**
	 * Helper method that updates the count for
	 * a given node- called during the add or remove
	 * or rebalance method when the subtrees of nodes
	 * have been changed
	 * @param n
	 */
	private void updateCount(Node n){
		n.count=1;
		Node left=n;
		Node right=n;
		while(left.left()!=null){
			n.count=n.count+1;
			left=(ABTreeSet<E>.Node) left.left();
		}
		while(right.right()!=null){
			n.count=n.count+1;
			right=(ABTreeSet<E>.Node) right.right();
		}
	}

}

