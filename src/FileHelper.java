import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FileHelper {

  private FileHelper() {
    throw new IllegalStateException("File Helper class");
  }

  /**
   * This function reads a file as a string
   * @param path The path of the file
   * @return A string of the contents of the file
   */

  public static String readFile(String path) {
    try {
      StringBuilder builder = new StringBuilder();
      BufferedReader bf = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8)); // read file as UTF-8 using a buffered reader
      String line = bf.readLine();
      while (line != null) {
        builder.append(line + '\r' + '\n'); // add read line to string builder and add CRLF to end
        line = bf.readLine(); // move to next line
      }

      bf.close(); // close buffered reader
      builder.setLength(Math.max(builder.length() - 2, 0)); // remove extra CRLF at end of reading file
      return builder.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return ""; 
    }
  }

  /**
   * This function will get the file size of a specified file
   * @param path The path of a file 
   * @return A long representing the size of file in bits
   */

  public static long getFileSize(String path) {
    try {
      long bytes = Files.size(Paths.get(path)); // get size of file in bytes

      return bytes * Byte.SIZE; // multiply it by size of byte to get in bits
    } catch (Exception e) {
      e.printStackTrace();
      return 1;
    }
  }

  /**
   * This function writes a string to a specified file
   * @param output The data to save
   * @param path The path of the file
   */

  public static void writeFile(String output, String path) {
    try {
      FileWriter writer = new FileWriter(path, StandardCharsets.UTF_8); // create file writer
      writer.write(output); // save output to file
      writer.close(); // close file writer
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
