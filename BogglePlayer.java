/*
Authors (group members): Alex Thomas, T'Avion Rodgers, Anthony Ciero, Jaylin Ollivierre
Email addresses of group members: thomasa2022@my.fit.edu, rodgerst2021@my.fit.edu, aciero2022@my.fit.edu jollivierre2022@my.fit.edu
Group name: C.A.M.E.R.O.N.
Course: cse2010
Section: 12
Description of the overall algorithm and key data structures:
   - Standard trie for dictionary building
   - Adjacency map for graph representation of board: Getting adjacent children
   - Priority Queues for a: generating a 'heat map' of letters on the board with most adjacent children (reverse order because longest to shortest words)
                         b: returning all words found, with the shortest word being at the front
   - Search algorithm: Recursively checks if each letter in a word appears in the trie, and if at last letter, marks the end of the word
*/

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Comparator;

public class BogglePlayer {
   // class to hold a list of children, and the row and column of the parent letter
   private class Entry implements Comparable<Entry> {
      private ArrayList<String> key;
      private int row;
      private int col;

      /**
       * constructor for an Entry object that sets the key, row, and col fields
       * @param k ArrayList to set k field to
       * @param r int to set row field to
       * @param c int to set col field to
       */
      public Entry (final ArrayList<String> k, final int r, final int c) {
         key = k;
         row = r;
         col = c;
      }

      /**
       * accessor method for key field
       * @return ArrayList of Strings key
       */
      public ArrayList<String> key() {
         return key;
      }

      /**
       * accessor method for row field
       * @return int row
       */
      public int row() {
         return row;
      }

      /**
       * accessor method for col field
       * @return int col
       */
      public int col() {
         return col;
      }

      /**
       * compares Entry objects based on length of key, used for building a priority queue
       * @param e the object to be compared.
       * @return difference in key list lengths
       */
      public int compareTo(final Entry e) {
         return key.size() - e.key().size();
      }
   }

   final Trie dictionary;

   /**
    * constructor for a BogglePlayer object that takes a file name of a list of words to use to build a dictionary
    * @param wordFile file name of list of words
    * @throws IOException
    */
   public BogglePlayer (final String wordFile) throws IOException {
      dictionary = new Trie(wordFile);
   }

   /**
    * method that returns an array of the longest words in board
    * @param board 2d array representing the boggle board
    * */
   public Word[] getWords(final char[][] board) {
      final boardGraph graph = new boardGraph(board); // creates a graph to represent the board
      PriorityQueue<Entry> heatMap = generateHeatMap(board, graph);  // creates a heatmap of most desirable starting positions
      // converts priority queue to array of Entries
      Entry[] entries = new Entry[16];
      entries = heatMap.toArray(entries);
      PriorityQueue<Word> recurr = new PriorityQueue<Word>();  // prepares a priority queue for Word objects
      // loops through all entries
      for (Entry e : entries) {
         final int[][] visited = new int[4][4]; // prepares a 2d array to store it a letter has been visited
         // call that finds all words from starting point e.row() and e.col() and stores the longest ones in recurr
         wordRecurr(recurr, graph, board, visited, e.row(), e.col(), new Word(""));
      }

      // stores recurr in an array and returns it
      final Word[] results = new Word[recurr.size()];
      for (int i = 0; i < results.length; i++) {
         results[i] = recurr.poll();
      }
      return results;
   }

   /**
    * method that recursively searches for valid words in the board, storing them in priority queue wrd that keeps the shortest word at front
    * @param wrd priority queue holding found words, storing shortest word at front
    * @param g adjacency list for board b
    * @param b 2d array representing boggle board
    * @param visited 2d array of ints, 0 for a posistion that has not been visited, 1 if it has
    * @param r row of letter to check
    * @param c column of letter to check
    * @param prev previous word object to build off of
    * */
   private int wordRecurr(PriorityQueue<Word> wrd, boardGraph g, char[][] b, int[][] visited, int r, int c, Word prev) {
      // base case of letter already being visited
      if (visited[r][c] == 1) {
         return 0;
      }
      visited[r][c] = 1;   // sets letter to visited
      final Word w = new Word(prev.getWord() + b[r][c]); // creates new Word object using letter from r, c on the board
      // sets path of word
      w.setPath(getWordPath(prev));
      w.addLetterRowAndCol(r, c);
      // searches for word in dictionary
      final int[] searchData = dictionary.search(w.getWord());
      assert searchData.length == 2;
      // first part of condition checks if wrd is already at 20 words, and if it is, checks if the current word is longer than the shortest word in wrd
      // second part of condition checks if the word exists in dictionary, and if wrd does not already contain it
      // if these are met, adds w to wrd
      if (!(wrd.size() == 20 && wrd.peek().getWord().length() == prev.getWord().length() + 1) && searchData[0] == 1 && !wrd.contains(w)) {
         wrd.add(w);
         // removes shortest word in wrd to make it length 20 again
         if (wrd.size() > 20) {
            wrd.poll();
         }
      }
      // only continues searching if there are more valid words possible down this path
      if (searchData[1] > 0) {
         final ArrayList<Vertex> adj = g.getNeighbors(r, c);   // acquires adjacent letters of board
         for (final Vertex v : adj) {
            wordRecurr(wrd, g, b, visited, v.getX(), v.getY(), w);   // recursive calls to all adjacent letters
         }
      }
      visited[r][c] = 0;   // sets visited marker back to 0
      return 0;
   }

   /**
    * returns an ArrayList of the path in Word w
    * @param w word to obtain path from
    * */
   private ArrayList<Location> getWordPath(final Word w) {
      final ArrayList<Location> locs = new ArrayList<Location>();
      for (int i = 0; i < w.getPathLength(); i++) {
         locs.add(w.getLetterLocation(i));
      }
      return locs;
   }

   /**
    * creates a priority queue of all the letters in the board, keying based on length of list, creating a heatmap of
    * the letters with the most children near it
    * @param b 2d array representing boggle board
    * @return heatmap of which letters have children near them
    */
   private PriorityQueue<Entry> generateHeatMap(final char[][] b, boardGraph graph) {
      // priority queue that will key based on # of children
      final PriorityQueue<Entry> hm = new PriorityQueue<Entry>(Comparator.reverseOrder());
      // loops through all letters on board
      for (int r = 0; r < 4; r++) {
         for (int c = 0; c < 4; c++) {
            // prepares an ArrayList to hold the children of that letter in the trie
            final ArrayList<String> children = dictionary.getChildren(String.valueOf(b[r][c]));
            // prepares an ArrayList to hold the children of that letter in the board
            final ArrayList<Vertex> adj = graph.getNeighbors(r, c);
            // loop through children, remove all that are not adjacent to the current letter
            for (int i = children.size() - 1; i >= 0; i--) {
               if (!adj.contains(children.get(i))) {
                  children.remove(i);
               }
            }
            // appends a new Entry using children list
            hm.add(new Entry(children, r, c));
         }
      }
      return hm;
   }
}
