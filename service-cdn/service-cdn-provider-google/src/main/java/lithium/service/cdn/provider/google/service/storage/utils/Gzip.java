package lithium.service.cdn.provider.google.service.storage.utils;

import lombok.experimental.UtilityClass;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

@UtilityClass
public class Gzip {
  public static byte[] compress(byte[] data) throws IOException {
    try (ByteArrayOutputStream obj = new ByteArrayOutputStream()) {
      GZIPOutputStream gzip = new GZIPOutputStream(obj);
      gzip.write(data);
      gzip.close();

      return obj.toByteArray();
    }
  }
}
