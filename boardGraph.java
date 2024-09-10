/*
Authors (group members): Alex Thomas, T'Avion Rodgers, Anthony Ciero, Jaylin Ollivierre
Email addresses of group members: thomasa2022@my.fit.edu, rodgerst2021@my.fit.edu, aciero2022@my.fit.edu jollivierre2022@my.fit.edu
Group name: C.A.M.E.R.O.N.
Course: cse2010
Section: 12
Description: holds an adjacency list of all the elements in a 2d array, allows for quick access to all adjacent cells
*/

import java.util.LinkedList;
import java.util.ArrayList;

// Vertex/Node representation for each position on board
class Vertex {
	private int x;
	private int y;
	// Coordinate pair as a single integer
	private int mapNum;
	private char character;
	// Neighbors using a linked list
	private LinkedList<Vertex> neighbors = new LinkedList<>();

	public Vertex (int x, int y, char character, int mapNum) {
		this.x = x;
		this.y = y;
		this.character = character;
		this.mapNum = mapNum;
	}

	// Getters for x, y, and mapNum
	public int getX () {
		return x;
	}

	public int getY () {
		return y;
	}

	public int getmapNum () {
		return mapNum;
	}

	public char getChar () {
		return character;
	}

	// Neighbors methods
	public void setNeighbor (Vertex v) {
		neighbors.add(v);
		return;
	}

	public LinkedList<Vertex> getNeighborList () {
		return neighbors;
	}

	@java.lang.Override
	public boolean equals(Object o) {
		if (o instanceof String) {
			final String s = (String) o;
			return s.equals(String.valueOf(character));
		}
		return false;
	}

	@java.lang.Override
	public String toString() {
		return "" + getChar() + " ";
	}

}

// Adjacency List Implementation of Graph
public class boardGraph {
	// Map to store all vertices
	private Vertex[] vertices = new Vertex[16];
	// Keep track of list size
	private int size = 0;
	// Sets board and creates adjacency list out of it
	public boardGraph (char[][] boardMatrix) {
		for (int i = 0; i < boardMatrix.length; i++) {
			for (int j = 0; j < boardMatrix[0].length; j++) {
				char curr = boardMatrix[i][j];
				Vertex v = addVertex(i, j, curr);
			}
		}

	}

	// Get adjacent vertices in a graph as array list
	public ArrayList<Vertex> getNeighbors (int x, int y) {
		Vertex v = vertices[mapCoords(x,y)];
		ArrayList<Vertex> vList = new ArrayList<>(v.getNeighborList());
		return vList;
	}
	
	// Get size of graph
	public int size () {
		return size;
	}

	// Tell whether or not graph is empty
	public boolean isEmpty () {
		if (size == 0) {
			return true;
		}
		return false;
	}

	// Method to print graph -- DEBUGGING
	public void printGraph() {
		for (int i = 0; i < vertices.length; i++) {
			if (i % 4 == 0) {
				System.out.println();
			}
			Vertex temp = vertices[i];
			System.out.print(temp);
		}
		System.out.println();
	}

	// Add vertex to map and connect with adjacent neighbors, if present
	private Vertex addVertex (int x, int y, char character) {
		// Use cantor pairing for hashmap key
		int mapNum = mapCoords(x,y);
		Vertex newVertex = new Vertex (x, y, character, mapNum);
		vertices[mapNum] = newVertex;

		// If top left exists, add as neighbor
		int adj = mapCoords(x-1,y-1);
		Vertex neighbor = isExisting(adj);
		if (neighbor != null) {
			newVertex.setNeighbor(neighbor);
			neighbor.setNeighbor(newVertex);
		}
		// If top (above) exists, add as neighbor
		adj = mapCoords(x-1,y);
		neighbor = isExisting(adj);
		if (neighbor != null) {
			newVertex.setNeighbor(neighbor);
			neighbor.setNeighbor(newVertex);
		}
		// If top right exists, add as neighbor
		adj = mapCoords(x-1,y+1);
		neighbor = isExisting(adj);
		if (neighbor != null) {
			newVertex.setNeighbor(neighbor);
			neighbor.setNeighbor(newVertex);
		}
		// If left exists, add as neighbor
		adj = mapCoords(x,y-1);
		neighbor = isExisting(adj);
		if (neighbor != null) {
			newVertex.setNeighbor(neighbor);
			neighbor.setNeighbor(newVertex);
		}
		// If right exists, add as neighbor
		adj = mapCoords(x,y+1);
		neighbor = isExisting(adj);
		if (neighbor != null) {
			newVertex.setNeighbor(neighbor);
			neighbor.setNeighbor(newVertex);
		}
		// If bottom left exists, add as neighbor
		adj = mapCoords(x+1,y-1);
		neighbor = isExisting(adj);
		if (neighbor != null) {
			newVertex.setNeighbor(neighbor);
			neighbor.setNeighbor(newVertex);
		}
		// If bottom (below) exists, add as neighbor
		adj = mapCoords(x+1,y);
		neighbor = isExisting(adj);
		if (neighbor != null) {
			newVertex.setNeighbor(neighbor);
			neighbor.setNeighbor(newVertex);
		}
		// If bottom right exists, add as neighbor
		adj = mapCoords(x+1,y+1);
		neighbor = isExisting(adj);
		if (neighbor != null) {
			newVertex.setNeighbor(neighbor);
			neighbor.setNeighbor(newVertex);
		}
		size++;
		return newVertex;
	}

	// x-y mapping function to convert coordinate pair to single int
	private int mapCoords (int x, int y) {
		// Performs boundchecking to ensure only values within the board are dealt with
		if (x < 0 || y < 0 || x > 3 || y > 3) {
			return -1;
		}
		return y * 4 + x;
	}

	// Tell whether a vertex exists in the graph. If so, return it
	private Vertex isExisting (int pair) {
		if (pair > -1 && vertices[pair] != null) {
			return vertices[pair];
		}
		return null;
	}
}