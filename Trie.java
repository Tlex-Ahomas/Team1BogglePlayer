/*
Authors (group members): Alex Thomas, T'Avion Rodgers, Anthony Ciero, Jaylin Ollivierre
Email addresses of group members: thomasa2022@my.fit.edu, rodgerst2021@my.fit.edu, aciero2022@my.fit.edu jollivierre2022@my.fit.edu
Group name: C.A.M.E.R.O.N.
Course: cse2010
Section: 12
Description: a linked structure used to store words based on a provided list of valid words that can be quickly searched through
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Trie {
   // class to hold an element, word flag, and parent/child pointers
   private class Node {
      private String data;
      private Node parent;
      private boolean isWord;
      private ArrayList<Node> children;

      // constructor that takes a string to set to data field, isWord is false by default
      public Node(final String d) {
         data = d;
         isWord = false;
         children = new ArrayList<Node>();
      }

      /**
       * appends node c to this node's children list, sets c's parent pointer to this node
       * @param c node to append as child
       */
      public void addChild(final Node c) {
         children.add(c);
         c.setParent(this);
      }

      /**
       * accessor method for data field
       * @return string data
       */
      public String getData() {
         return data;
      }

      /**
       * accessor method for the data in the list of children
       * @return ArrayList of Strings containing all the data in the children list
       */
      public ArrayList<String> getChildren() {
         final ArrayList<String> ch = new ArrayList<String>();
         for (final Node c : children) {
            ch.add(c.getData());
         }
         return ch;
      }

      /**
       * accessor method for isWord
       * @return boolean isWord
       */
      public boolean isWord() {
         return isWord;
      }

      /**
       * setter method for parent pointer
       * @param p Node to set parent pointer to
       */
      private void setParent(final Node p) {
         parent = p;
      }

      /**
       * setter method for isWord field
       * @param siw boolean to set isWord to
       */
      public void setIsWord(final boolean siw) {
         isWord = siw;
      }

      /**
       * Overridden equals method used to compare Nodes to other Nodes and Strings
       * @param o object to test equality to
       * @return true if this is equal to o, false otherwise
       */
      @Override
      public boolean equals(final Object o) {
         if (o instanceof Node) {
            final Node n = (Node) o;
            return data.equals(n.getData());
         }
         if (o instanceof String) {
            final String s = (String) o;
            return data.equals(s);
         }
         return false;
      }
   }

   private Node head;

   /**
    * constructor for building a new Trie from a list of words
    * @param wordList filename of list of words to use
    * @throws IOException if file is not found
    */
   public Trie(final String wordList) throws IOException {
      final BufferedReader bfr = new BufferedReader(new FileReader(wordList));   // used to read through file of words
      head = new Node(""); // head holds no data
      String word = bfr.readLine().toLowerCase();
      while (word != null) {  // while there are words in the list
         // only stores words that are at least 3 letters long: minimum length required to score points
         // only stores words that are at most 9 letters long: statistically unlikely to find longer words on the board
         if (word.length() < 3 || word.length() > 9) {
            word = bfr.readLine();
            continue;
         }
         // if word is within previous length bounds, add it to the trie
         trieBuilder(word, head);
         word = bfr.readLine();
      }
   }

   /**
    * inserts a new string into the trie
    * @param toAdd String to be inserted into the Trie
    * @param parentNode Node currently being appended to
    * */
   private void trieBuilder(final String toAdd, final Node parentNode) {
      // base case of toAdd being 0 characters long
      if (toAdd.length() == 0) {
         parentNode.setIsWord(true);   // marks end of word
         return;
      }
      Node child = new Node(toAdd.substring(0,1)); // creates a new Node containing first character of toAdd

      // determines if parentNode already has Node with that character, if so, refrains from appending it
      final int ind = parentNode.children.indexOf(child);
      if (ind == -1) {
         parentNode.addChild(child);
      } else {
         child = parentNode.children.get(ind);
      }
      trieBuilder(toAdd.substring(1), child);   // recursive call with first character of toAdd removed and child passed as parentNode
   }

   /**
    * method to return the list of children in the trie for the last character in key
    * @param key String to find children of
    * */
   public ArrayList<String> getChildren(final String key) {
      return childRecur(key, head);
   }

   /**
    * method to find the list of children of the last character in String key in the trie
    * @param key String to find the children of the last character of
    * @param curr Node to check the children of
    * */
   private ArrayList<String> childRecur(final String key, final Node curr) {
      // base case of key being 1 character long
      if (key.length() == 1) {
         return curr.getChildren(); // returns number of children key has
      }
      // determines if next character of key exists in curr's children list, returns empty list if not
      final int ind = curr.children.indexOf(new Node(key.substring(0,1)));
      if (ind == -1) {
         return new ArrayList<String>();
      }
      // recursive call, with first character of key removed and curr's child passed as curr
      return childRecur(key.substring(1), curr.children.get(ind));
   }

   /**
    * used for determining if a string exists as a word in the Trie
    * @param query String to search for
    * @return 2 element array of ints, first element is 1 or 0 if query exists or doesn't exist respectively, second element is # of children
    * the last node searched has
    */
   public int[] search(final String query) {
      return searchRecur(query.toLowerCase(), head);
   }

   /**
    * method that recursively traverses trie to determine if String q exists in it
    * @param q String being checked in the trie
    * @param c node to check the children of
    * @return 2 element array of ints, first element is 1 or 0 if query exists or doesn't exist respectively, second element is # of children
    * the last node searched has
    * */
   private int[] searchRecur(final String q, Node c) {
      // base case of q being length 0
      if (q.length() == 0) {
         // isWord determines if first element is 1 or 0
         if (c.isWord()) {
            return new int[] {1, c.getChildren().size()};
         }
         return new int[] {0, c.getChildren().size()};
      }
      // determines if next character of q exists in c's children list, if not, returns {0, 0}
      final int ind = c.children.indexOf(new Node(q.substring(0,1)));
      if (ind != -1) {
         return searchRecur(q.substring(1), c.children.get(ind)); // recursive call with first character of q removed and c's child passed as c
      }
      return new int[] {0, 0};
   }

   /**
    * method used to print all the children of head
    */
   public void printChildren() {
      for (Node n : head.children) {
         System.out.print(n.getData());
      }
      System.out.println();
   }

   /**
    * FOR DEBUGGING PURPOSES ONLY
    * @param args
    * @throws IOException
    */
   public static void main(final String[] args) throws IOException {
      final Trie test = new Trie(args[0]);
      System.out.println("done building");
      test.printChildren();
      System.out.println(test.search("apple"));
   }
}
