import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
  public static final ArrayBlockingQueue<String> A_QUEUE = new ArrayBlockingQueue<>(100);
  public static final ArrayBlockingQueue<String> B_QUEUE = new ArrayBlockingQueue<>(100);
  public static final ArrayBlockingQueue<String> C_QUEUE = new ArrayBlockingQueue<>(100);
  public static final int TEXTS_COUNT = 10_000;
  public static final int TEXTS_LENGTH = 100_000;

  public static void main(String[] args) {
    new Thread(() -> {
      for (int i = 0; i < TEXTS_COUNT; i++) {
        String text = generateText("abc", TEXTS_LENGTH);
        try {
          A_QUEUE.put(text);
          B_QUEUE.put(text);
          C_QUEUE.put(text);
        } catch (InterruptedException e) {
          return;
        }
      }
    }).start();

    createThread(A_QUEUE, 'a').start();
    createThread(B_QUEUE, 'b').start();
    createThread(C_QUEUE, 'c').start();
  }

  public static int findMaxSize(String text, char letter) {
    int maxSize = 0;
    for (int i = 0; i < text.length(); i++) {
      for (int j = 0; j < text.length(); j++) {
        if (i >= j) {
          continue;
        }
        boolean letterFound = false;
        for (int k = i; k < j; k++) {
          if (text.charAt(k) == letter) {
            letterFound = true;
            break;
          }
        }
        if (!letterFound && maxSize < j - i) {
          maxSize = j - i;
        }
      }
    }
    return maxSize;
  }

  public static Thread createThread(ArrayBlockingQueue<String> queue, char letter) {
    return new Thread(() -> {
      int maxSize;
      String text;
      for (int i = 0; i < TEXTS_COUNT; i++) {
        try {
          text = queue.take();
        } catch (InterruptedException e) {
          return;
        }
        maxSize = findMaxSize(text, letter);
        System.out.println(text.substring(0, 100) + " letter " + letter + " -> " + maxSize);
      }
    });
  }

  public static String generateText(String letters, int length) {
    Random random = new Random();
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < length; i++) {
      text.append(letters.charAt(random.nextInt(letters.length())));
    }
    return text.toString();
  }
}