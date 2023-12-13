import com.sun.tools.javac.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ElfHelper {

  static List<String> readInputLines(String fileName) {
    InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(fileName);
    List<String> lines = new ArrayList<>();
    if (inputStream != null) {
      try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
        String line;
        while ((line = br.readLine()) != null) {
          lines.add(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.err.println("File not found: " + fileName);
    }
    return lines;
  }
}
