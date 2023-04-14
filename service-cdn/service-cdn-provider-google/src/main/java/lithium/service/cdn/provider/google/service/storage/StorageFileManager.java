package lithium.service.cdn.provider.google.service.storage;

import com.google.cloud.storage.Blob;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface StorageFileManager {

  <T> Blob upload(String storingPath, String contentType, T source, CheckedFunction<T, byte[]> toByte);

  void delete(String storingPath);

  Optional<Blob> get(String storingPath);

  List<Blob> listFiles(String storingPath);

  Optional<String> readFileContent(String storingPath);

  @FunctionalInterface
  interface CheckedFunction<T, R> {

    R apply(T t) throws IOException;
  }

}
