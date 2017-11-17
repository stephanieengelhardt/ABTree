import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import ABTreeSet.Node;

/**
 * @author Stephanie Engelhardt
 */
public class ABTreeMap<K extends Comparable<? super K>, V> {

	final class Entry implements Comparable<Entry>, Map.Entry<K, V> {
		private final K key; // immutable
		private V value; // mutable

		Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public int compareTo(Entry o) {
			if((int) o.getKey()>(int)key)
				return 1;
			else if((int)o.getKey()<(int)key)
				return -1;
			else
				return 0;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V newValue) {
			value=newValue;
			return newValue;
		}

	}

	private ABTreeSet<Entry> entrySet;
	private int size;
	private boolean selfbalance;
	private int top;
	private int bottom;
	private Node root;

	/**
	 * Default constructor. Builds a map that uses a non-self-balancing tree.
	 */
	public ABTreeMap() {
		entrySet=new ABTreeSet<Entry>();
		size=0;
		selfbalance=false;
		root=null;

	}

	/**
	 * If isSelfBalancing is true, <br>
	 * builds a map that uses self-balancing tree with alpha = 2/3. <br>
	 * If isSelfBalancing is false, <br>
	 * builds a map that uses a non-self-balancing tree.
	 * 
	 * @param isSelfBalancing
	 */
	public ABTreeMap(boolean isSelfBalancing) {
		entrySet=new ABTreeSet<Entry>();
		selfbalance=isSelfBalancing;
		if(selfbalance){
			top=2;
			bottom=3;
		}
		size=0;
		root=null;
	}

	/**
	 * If isSelfBalancing is true, <br>
	 * builds a map that uses a self-balancing tree with alpha = top/bottom.
	 * <br>
	 * If isSelfBalancing is false, <br>
	 * builds a map that uses a non-self-balancing tree.
	 * 
	 * @param isSelfBalancing
	 * @param top
	 * @param bottom
	 * @throws IllegalArgumentException
	 *             if (1/2 < alpha < 1) is false
	 */
	public ABTreeMap(boolean isSelfBalancing, int top, int bottom) {
		entrySet=new ABTreeSet<Entry>();
		selfbalance=isSelfBalancing;
		if(selfbalance){
			this.top=top;
			this.bottom=bottom;
		}
		size=0;
		root=null;
	}

	/**
	 * Returns true if this map contains a mapping for key; <br>
	 * otherwise, it returns false.
	 * 
	 * @param key
	 * @return true if this map contains a mapping for key; <br>
	 *         otherwise, it returns false.
	 */
	public boolean containsKey(K key) {
		return entrySet.getBSTNode(new Entry(key,null))!=null;
	}

	/**
	 * Returns the value to which key is mapped, <br>
	 * or null if this map contains no mapping for key.
	 * 
	 * @param key
	 * @return
	 */
	public V get(K key) {
		ABTreeSet<Entry>.Node n= (ABTreeSet<ABTreeMap<K, V>.Entry>.Node) entrySet.getBSTNode(new Entry(key,null));
		if(n!=null)
			return n.data().value;
		return null;
	}

	/**
	 * Returns a ABTreeSet storing the keys (not the values)
	 * 
	 * Example. Suppose this map consists of the following (key, value) pairs:
	 * (10, Carol), (21, Bill), (45, Carol), (81, Alice), (95, Bill). <br>
	 * Then, the ABTreeSet returned should consist of 10, 21, 45, 81, 91.
	 * <p>
	 * The keySet should have the same tree structure as entrySet.
	 * 
	 * @return a ABTreeSet storing the keys (not the values)
	 */
	public ABTreeSet<K> keySet() {
		ABTreeSet<K> result=new ABTreeSet<K>();
		Iterator iter= entrySet.iterator();
		while(iter.hasNext()){
			result.add(((Entry) iter.next()).getKey());
		}
		return result;
	}



	/**
	 * Associates value with key in this map. <br>
	 * Returns the previous value associated with key, <br>
	 * or null if there was no mapping for key.
	 * 
	 * @param key
	 * @param value
	 * @return the previous value associated with key, <br>
	 *         or null if there was no mapping for key.
	 * @throws NullPointerException
	 *             if key or value is null.
	 */
	public V put(K key, V value) {
		ABTreeSet<Entry>.Node n=(ABTreeSet<ABTreeMap<K, V>.Entry>.Node) entrySet.getBSTNode(new Entry(key,null));
		V result=null;
		if(n!=null){
			result=n.data().value;
			n.data().value=value;
		}
		else{
			entrySet.add(new Entry(key, value));
			size++;
		}
		return result;
	}

	/**
	 * Removes the mapping for key from this map if it is present. <br>
	 * Returns the previous value associated with key, <br>
	 * or null if there was no mapping for key.
	 * 
	 * @param key
	 * @return the previous value associated with key, <br>
	 *         or null if there was no mapping for key.
	 */
	public V remove(K key) {
		ABTreeSet<Entry>.Node n=(ABTreeSet<ABTreeMap<K, V>.Entry>.Node) entrySet.getBSTNode(new Entry(key, null));
		V result=null;
		if(n!=null){
			result=n.data().value;
			entrySet.unlinkNode(n);
		}

		return result;
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 * 
	 * @return the number of key-value mappings in this map.
	 */
	public int size() {
		return size;
	}

	@Override
	public String toString() {
		return entrySet.toString();
	}

	/**
	 * Returns an ArrayList storing the values contained in this map. <br>
	 * Note that there may be duplicate values. <br>
	 * The ArrayList should contain the values in ascending order of keys.
	 * <p>
	 * Example. <br>
	 * Suppose this map consists of the following (key, value) pairs: <br>
	 * (10, Carol), (21, Bill), (45, Carol), (81, Alice), (95, Bill). <br>
	 * Then, the ArrayList returned should consist of the strings <br>
	 * Carol, Bill, Carol, Alice, Bill, in that order.
	 * 
	 * @return
	 */
	public ArrayList<V> values() {
		ArrayList<V> result=new ArrayList<V>();
		Iterator iter= entrySet.iterator();
		while(iter.hasNext()){
			result.add(((Entry) iter.next()).getValue());
		}
		return result;
	}
}

