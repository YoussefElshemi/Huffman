import java.io.*;
import java.nio.file.*;

public class BinaryHelper {

  private BinaryHelper() {
    throw new IllegalStateException("Binary Helper class");
  }

  /**
   * This function reads bits from a file
   * @param path The path of the file
   * @return A string of bits in the file
   */

  public static String readBinaryFile(String path) {
    try {
      byte[] allBytes = Files.readAllBytes(Paths.get(path)); // get all bytes
      StringBuilder builder = new StringBuilder(allBytes.length * Byte.SIZE);

      for (int i = 0; i < Byte.SIZE * allBytes.length; i++) {
        builder.append((allBytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1'); // add bits to string builder
      }

      return builder.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }
  
  /**
   * This function saves bits to a file
   * @param s The string of bits
   * @param path The path of the file
   */

  public static void writeBinaryFile(String s, String path) {
    StringBuilder builder = new StringBuilder();
    int paddingLength = 8 - (s.length() % 8); // how many padded bits to add at end of string
    String bits = Integer.toBinaryString(paddingLength); // convert this to binary

    while (bits.length() % 8 != 0) { 
      bits = '0' + bits; // pad bits to make full byte
    }

    builder.append(bits); // store first byte as how many padded bits are added to end of file
    builder.append(s);
    while (builder.length() % 8 != 0) { // pad bits to make full bytes
      builder.append('0');
    }

    s = builder.toString();
    byte[] data = new byte[s.length() / 8]; // make empty array of bytes

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '1') {
        data[i >> 3] |= 0x80 >> (i & 0x7); // thank chico for this
      }
    }

    try {
      OutputStream outputStream = new FileOutputStream(path); // create file output stream
      outputStream.write(data); // save bytes to file
      outputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
