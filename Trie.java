package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */

	public static TrieNode buildTrie(String[] allWords) {
		TrieNode root = new TrieNode(null, null, null);
		TrieNode current = root;

		for(int arrIndex = 0; arrIndex < allWords.length; arrIndex++) {
			//do this only if the first word is being inputed
			if(current == root) {
				TrieNode next = new TrieNode(null, null, null);
				next.substr = new Indexes(arrIndex, (short) 0, (short) (allWords[arrIndex].length() - 1));
				current.firstChild = next;//root.firstChild = next;
				current = current.firstChild;//current = root.firstChild;
			} else {
				checkAndInsertNode(allWords[arrIndex], allWords, root, arrIndex, current);				
			}
			//print(root, allWords);
		}
		return root;
	}
	
	private static void checkAndInsertNode(String target, String[] allWords, TrieNode root, int arrIndex, TrieNode current) {

		short prefix = getPrefix(target, allWords, current);// todo check if root is needed? instead current
		
		if(prefix == -1) {	//found no prefix match
			if(current.sibling != null) {// Traverse the siblings
				checkAndInsertNode(target, allWords,  root,  arrIndex,  current.sibling);
			} else {// Add as last sibling				
				TrieNode next = new TrieNode(null, null, null);
				next.substr = new Indexes(arrIndex, (short) 0, (short) (target.length() - 1));
				current.sibling = next;				
			}
						
		} else {//Match
/*			if(!target.startsWith(allWords[current.substr.wordIndex].substring(0, current.substr.endIndex))){
				checkAndInsertNode(target, allWords,  root,  arrIndex,  current.sibling);				
			}
*/			if (current.firstChild == null) {// Leaf Node				
				splitAndAdjust(target, allWords, root, arrIndex, current, prefix);
			} else {// Node has children				
				checkAndInsertNode(target, allWords,  root,  arrIndex,  current.firstChild);
			}
		}
		
	}

	private static void splitAndAdjust(String target, String[] allWords, TrieNode root, int arrIndex, TrieNode current, short prefix) {

		current.substr.endIndex = (short) (prefix + current.substr.startIndex);
		
		// SPLIT and Adjust. Make a new node with remaining (prefix+1) data from current node
		TrieNode next = new TrieNode(null, null, null);
		
		// Set the data to prefix+1 to end of current node
		if(current.firstChild != null) {
			next.substr = new Indexes(current.substr.wordIndex,(short) (prefix + 1),(short) (current.firstChild.substr.startIndex - 1));
			next.firstChild = current.firstChild;
			current.firstChild = next;
		} else {
			next.substr = new Indexes(current.substr.wordIndex, (short) (prefix + 1), (short) (allWords[current.substr.wordIndex].length() - 1));
			// Set the First Child of current/matching node to newly created node
			current.firstChild = next;
		}
		
		// Create a new node for target
		TrieNode next1 = new TrieNode(null, null, null);
		// Set the data of target stating prefix+1
		next1.substr = new Indexes(arrIndex, (short) (prefix + 1), (short) (allWords[arrIndex].length() - 1));
		// Set sibling of second most recent node to most recently created node
		next.sibling = next1;
		current = root.firstChild;
	}

	private static short getPrefix(String target, String[] allWords, TrieNode current) {
		short prefix = -1;
		
		for(int targetIndex = current.substr.startIndex; targetIndex < target.length(); targetIndex++) {
			if(current != null) {
				char trieWordIndex = allWords[current.substr.wordIndex].charAt(targetIndex);
				if(current.substr.endIndex < targetIndex) {
					break;
				}
				if(target.charAt(targetIndex) == trieWordIndex) {
					prefix++;
				} else if(target.charAt(targetIndex) != trieWordIndex && prefix != -1) {
					break;
				}
				else break;
			}
		}
		return prefix;
	}
	
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		TrieNode current = root.firstChild;
		if(current == null) current = root;
		ArrayList<TrieNode> fin = new ArrayList<>();

		//fin = completionList(current, allWords, prefix);
		current = traverseToLeaf(current);
		if(allWords[current.substr.wordIndex].contains(prefix)) {
			fin.add(current);
		}
		while(current.sibling != null) {
			current = traverseSibling(current);
			fin.addAll(completionList(current, allWords, prefix));			//to check all the children of this node
			//if(current.firstChild  == null && allWords[current.substr.wordIndex].contains(prefix) && fin.contains(current)) {
			//	fin.add(current);
			//}
		}
		return fin;
	}
	private static TrieNode traverseToLeaf(TrieNode root) {
		TrieNode current = root;
		
		if(current == null) return current;
		
		if(current.firstChild != null) {
			current = traverseToLeaf(current.firstChild);
		}
		
		return current;
	}
	private static TrieNode traverseSibling(TrieNode root) {
		TrieNode current = root;
		
		if(current == null) return current;
		
		if(current.sibling != null) {
			current = current.sibling;
			return current;
		}
		
		return current;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }


