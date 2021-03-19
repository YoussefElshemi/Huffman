import java.io.*;
import java.util.*;

public class Huffman {

  /**
   * Input data for compression
   */

  private String data;

  /**
   * Character frequencies in input data
   */

  private Map<Character, Integer> frequencies;

  /**
   * Output data for compression
   */

  private String output;

  /**
   * Huffman class
   * @param path File path to input data
   */

  public Huffman(String path) {
    this.data = FileHelper.readFile(path);
  }

  /**
   * This function calculates all of the frequencies in the input data
   * It then creates a tree using these frequencies
   * It then creates a map of all the characters, which map to their encoded values
   * eg. 'a': 011 for example
   * It then encodes the string using this, and returns it 
   * @return Compressed data
   */

  public String compress() {
    this.frequencies = getFrequencies(data);
    Node tree = makeTree(frequencies);
    Map<Character, String> encodingMap = makeEncodingMap(tree);
    this.output = encodeString(encodingMap, data);

    return this.output;
  }

  /**
   * This function creates a map of frequencies of each character
   * @param data The input string
   * @return A Hashmap which maps a character to a frequency
   */

  public static Map<Character, Integer> getFrequencies(String data) {
    Map<Character, Integer> frequencyMap = new HashMap<>(); // make hashmap
    for (char i:data.toCharArray()) { // iterate over each letter in data

      // calculate frequency for each letter
      if (frequencyMap.get(i) != null) {
        frequencyMap.put(i, frequencyMap.get(i) + 1); 
      } else {
        frequencyMap.put(i, 1);
      }
    }

    return frequencyMap;
  }

  /**
   * This function makes the tree based on the character frequencies
   * @param frequencies A hashmap which maps characters to their frequencies
   * @return A sorted Huffman tree
   */

  public static Node makeTree(Map<Character, Integer> frequencies) {
    PriorityQueue<Node> nodes = new PriorityQueue<>(frequencies.size()); // make priority queue with size of frequency hashmap

    // make a node for each character in the hashmap
    for (Map.Entry<Character, Integer> entry:frequencies.entrySet()) {
      Node node = new Node(entry.getValue(), entry.getKey());
      nodes.add(node);
    }

    // iterate until there is one node left (Huffman tree is completed)
    while (nodes.size() > 1) {
      Node left = nodes.remove(); // get smallest node
      Node right = nodes.remove(); // get second smallest node

      // merge nodes together
      Node newNode = new Node(left.count + right.count);
      newNode.left = left;
      newNode.right = right;

			nodes.add(newNode); // add back to priority queue
    }

    return nodes.remove(); // return the Huffman tree
  }

  /**
   * This function makes the encoding map
   * @param map The existing map of encodings
   * @param tree The Huffman tree (or its children)
   * @param prefix An empty or partially complete encoding
   * @return A map of encodings
   */

  public static Map<Character, String> makeEncodingMap(Map<Character, String> map, Node node, String prefix) {
    if (node.left == null && node.right == null) { // if the current node is a leaf
      map.put(node.letter, prefix); // complete encoding and save it into map
    } else {
      // continue to traverse the node
      makeEncodingMap(map, node.left, prefix + "0"); 
      makeEncodingMap(map, node.right, prefix + "1");
    }

    return map;
  }

  /**
   * This function makes the encoding map, but only requires one parameter, as we pass default ones through this
   * @param tree The Huffman tree
   * @return A map of encodings
   */

  public static Map<Character, String> makeEncodingMap(Node tree) {
    Map<Character, String> map = new HashMap<>(); // make empty hashmap
    return makeEncodingMap(map, tree, ""); // pass them to main function with empty prefix
   }

  /**
   * This function encodes a string using the encoding map
   * @param encodingMap A map of encodings
   * @param data The string to encode
   * @return An encoded string
   */

  public static String encodeString(Map<Character, String> encodingMap, String data) {
    StringBuilder builder = new StringBuilder();
    for (Character i:data.toCharArray()) {
      builder.append(encodingMap.get(i)); // get the encoding from the map, and add it to output string
    }
    
    return builder.toString(); // return encoded string
  }

  /**
   * This function decodes a string using the tree
   * @param tree The Huffman tree
   * @param data The encoded string
   * @return The decoded string
   */

  public static String decodeString(Node tree, String data) {
    StringBuilder builder = new StringBuilder();
    Node currentNode = tree;

    for (Character i:data.toCharArray()) { // iterate over encoded data

      // traverse the tree accordingly 
      if (i.equals('0')) {
        currentNode = currentNode.left;
      } else {
        currentNode = currentNode.right;
      }

      if (currentNode.left == null && currentNode.right == null) { // if the current node is a leaf
        builder.append(currentNode.letter);
        currentNode = tree; // reset tree to the beginning
      }
    }
    return builder.toString(); // return decoded data
  }

  /**
   * This function will save compressed data to a file
   * @param path The path of the file
   */

  public void save(String path) {
    // if its not been compressed yet, compress it
    if (output == null) {
      this.compress(); 
    }  

    BinaryHelper.writeBinaryFile(output, path); // save encoded binary data
    String frequencyPath = path.substring(0, path.lastIndexOf('.')) + ".hashmap"; // path for the frequency map
    try {
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(frequencyPath)); // create object output stream
      oos.writeObject(this.frequencies); // write frequency map to file 
      oos.close();  // close object output stream
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This function will load a Huffman tree from a file by reading the map of frequencies, and recreating the tree
   * @param path The path of the file
   * @return A Huffman tree
   */

  @SuppressWarnings("unchecked")
  public static Node loadTree(String path) {
    Map<Character, Integer> frequencies = null;

    try {
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path)); // create object input stream
      frequencies = (HashMap<Character, Integer>)ois.readObject(); // read frequency map from file
      ois.close(); // close object input stream
    } catch(Exception e) {
      e.printStackTrace();
    }

    return Huffman.makeTree(frequencies); // make tree from frequencies and return it
  }

  /**
   * This function will decompress a compressed file
   * @param path The path of the file
   * @return A string of the decompressed data
   */

  public static String decompress(String path) {
    String output = BinaryHelper.readBinaryFile(path); // read binary data from file
    String paddingLength = output.substring(0, 8); // get first byte (how many padded bits are at end of file)
    output = output.substring(8, output.length()); // remove first byte 
    int padding = Integer.parseInt(paddingLength, 2); // convert bits to int
    output = output.substring(0, output.length() - padding); // remove padded bits from end of file
    Node tree = Huffman.loadTree(path.substring(0, path.lastIndexOf('.')) + ".hashmap"); // load tree from file
    return Huffman.decodeString(tree, output); // decode data and return it
  }

  /**
   * This function prints the menu
   */

  public static void printMenu() {
    System.out.println("Help Menu:");
    System.out.println("[] - required, <> - optional");
    System.out.println("Compression: java Huffman -c [input file] [output file]");
    System.out.println("De-Compression: java Huffman -d [input file] <output file>");
  }

  public static void main(String[] args) {
    final long startTime = System.currentTimeMillis(); // get time the program starts
    if (args.length == 0) {
      Huffman.printMenu(); // if there's missing arguments, it'll print menu
    } else {
      if (args[0].equals("-c")) {
        if (args.length == 3) {
          Huffman huffman = new Huffman(args[1]); // create huffman object with input path
          huffman.save(args[2]); // save data to output path

          long inputSize = FileHelper.getFileSize(args[1]); // get size of input file
          long mapSize = FileHelper.getFileSize(args[2].substring(0, args[2].lastIndexOf('.')) + ".hashmap"); // get size of frequency map file
          long outputSize = FileHelper.getFileSize(args[2]) + mapSize; // get size of compressed file and frequncy map file
          final long endTime = System.currentTimeMillis(); // get time the program ends

          System.out.println("Original Length: " + inputSize);
          System.out.println("Compressed Length: " + (huffman.output.length() + mapSize));
          System.out.println("Total Reduction: " + (float)(inputSize - outputSize) / inputSize * 100 + "%");
          System.out.println("Execution Time: " + (endTime - startTime) + "ms"); // calculate execution time
        } else {
          Huffman.printMenu(); // if there's missing arguments, it'll print menu
        }
      } else if (args[0].equals("-d") && args.length > 1) {
        String output = Huffman.decompress(args[1]); // decompress using input path
        if (args.length == 3) {
          FileHelper.writeFile(output, args[2]); // if theres an output path, it'll save to it
        } else {
          System.out.println(output); // otherwise it'll print the decoded data
        }
        final long endTime = System.currentTimeMillis(); // get time the program ends
        System.out.println("Execution Time: " + (endTime - startTime) + "ms"); // calculate execution time
      } else {
        Huffman.printMenu(); // if there's missing arguments, it'll print menu
      }
    }
  }
}
