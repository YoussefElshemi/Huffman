import java.util.Objects;

class Node implements Comparable<Node> {

  /**
   * The count of the node
   */

  public int count;

  /**
   * The letter of the node
   */

  public Character letter;

  /**
   * Left child node
   */
  
  public Node left;

  /**
   * Right child node
   */

  public Node right;

  /**
   * Instantiate a node with only count and letter
   * @param count The count
   * @param letter The letter
   */
  
  public Node(int count, Character letter) {
    this.count = count;
    this.letter = letter;
    this.left = null;
    this.right = null;
  }

  /**
   * Instantiate a node with only count
   * @param count The count
   */

  public Node(int count) {
    this.count = count;
    this.letter = null;
    this.left = null;
    this.right = null;
  }

  /**
   * Printing a node
   */

  @Override
  public String toString() {
    return (letter != null ? letter : "") + ": " + count;
  }

  /**
   * For the implementation of comparable 
   */

  @Override
  public int compareTo(Node o) {
    return count - o.count;
  }

  /**
   * Checking if two nodes are equal to eachother (required due to overriding compareTo)
   */

  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (this.getClass() != o.getClass()) return false;

    Node n = (Node)o;
    return letter.equals(n.letter) && count == n.count;
  }

  /**
   * Calculating the hash code of a node (required due to overriding equals)
   */

  @Override
  public int hashCode() {
    return Objects.hash(letter, count);
  }
}
