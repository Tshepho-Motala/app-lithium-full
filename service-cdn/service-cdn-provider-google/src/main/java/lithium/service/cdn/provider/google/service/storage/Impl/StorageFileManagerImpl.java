package lithium.service.cdn.provider.google.service.storage.Impl;

import static java.util.Optional.ofNullable;
import static lithium.service.cdn.provider.google.service.utils.PredicateUtils.not;


import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lithium.service.cdn.provider.google.service.storage.StorageFileManager;
import lithium.service.cdn.provider.google.service.storage.StorageProvider;
import lithium.service.cdn.provider.google.service.storage.exception.GoogleStoreException;
import lithium.service.cdn.provider.google.service.storage.utils.Gzip;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Component
@AllArgsConstructor
@Slf4j
public class StorageFileManagerImpl implements StorageFileManager {

  private static final String DEFAULT_CONTENT_ENCODING = "gzip";
  private static final Predicate<Blob> isDirectoryContentType = blob -> "text/plain".equals(blob.getContentType());

  private final StorageProvider storageProvider;

  @Override
  public void delete(String storingPath) {
    getStorage().delete(BlobId.of(getBucket(), storingPath));
  }

  @Override
  public Optional<Blob> get(String storingPath) {
    return ofNullable(getStorage().get(getBucket(), storingPath));
  }

  @Override
  public List<Blob> listFiles(String storingPath) {
    Iterable<Blob> blobs = getStorage().list(getBucket(), Storage.BlobListOption.prefix(storingPath)).iterateAll();
    return StreamSupport.stream(blobs.spliterator(), false)
        .filter(s -> s.getSize() > 0)  // remove folders from stream. s.isDirectory() doesnt work
        .filter(not(isDirectoryContentType)) // remove folders from stream. s.isDirectory() doesnt work
        .sorted(Comparator.comparingLong(BlobInfo::getCreateTime).reversed())
        .collect(Collectors.toList());
  }

  @Override
  public Optional<String> readFileContent(String storingPath) {
    return Optional.ofNullable(getStorage().get(getBucket(), storingPath))
        .filter(Blob::exists)
        .map(s -> new String(s.getContent()));
  }


  @Override
  public <T> Blob upload(String storingPath, String contentType, T source, CheckedFunction<T, byte[]> toByte) {
    try {
      BlobInfo blobInfo = BlobInfo.newBuilder(getBucket(), storingPath)
          .setContentType(contentType)
          .setContentEncoding(DEFAULT_CONTENT_ENCODING)
          .setCacheControl(getCacheControl())
          .build();
      return getStorage().create(blobInfo, Gzip.compress(toByte.apply(source)));
    } catch (Exception e) {
      log.warn("Failed upload file " + storingPath + "  to Google store", e);
      throw new GoogleStoreException("Failed upload file to storage");
    }
  }

  private Storage getStorage() {
    return storageProvider.getStorage();
  }

  private String getCacheControl() {
    return storageProvider.getCacheControl();
  }

  private String getBucket() {
    return storageProvider.getBucket();
  }
}
