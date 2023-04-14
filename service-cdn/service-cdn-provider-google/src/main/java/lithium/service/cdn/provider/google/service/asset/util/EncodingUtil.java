package lithium.service.cdn.provider.google.service.asset.util;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Utility class for JavaScript compatible UTF-8 encoding.
 */
public class EncodingUtil {

  private EncodingUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static String encodeURIComponent(String str) {
    try {
      return URLEncoder.encode(str, "UTF-8")
          .replaceAll("\\+", "%20")
          .replaceAll("\\%21", "!")
          .replaceAll("\\%27", "'")
          .replaceAll("\\%28", "(")
          .replaceAll("\\%29", ")")
          .replaceAll("\\%7E", "~");
    } catch (UnsupportedEncodingException e) {
      return str;
    }
  }
}
