package lithium.service.cdn.provider.google.service.asset.Impl;

import static lithium.service.cdn.provider.google.service.utils.PredicateUtils.not;

import com.google.cloud.storage.Blob;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lithium.service.cdn.provider.google.service.asset.model.BatchProcessingResult;
import lithium.service.cdn.provider.google.service.asset.model.StorageAsset;
import lithium.service.cdn.provider.google.service.asset.util.EncodingUtil;
import lithium.service.cdn.provider.google.service.storage.StorageFileManager;
import lithium.service.cdn.provider.google.service.storage.exception.AssetValidationException;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class StorageAssetService {

  //TODO: Add all supported types
  private static final Set<String> IMAGE_CONTENT_TYPES = Sets.newHashSet(MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/svg+xml");
  //TODO: figure out what is the possible max size of uploaded image. We'll have a freeze of service if the big(dozens or hundreds Mb) files will uploaded
  public static final String VALIDATION_ERROR_MESSAGE = "This image is larger than 10MB and can not be uploaded.";
  private static final int MAX_FILE_SIZE = 10000000;
  private final static Predicate<MultipartFile> isSupportedType = multipartFile -> IMAGE_CONTENT_TYPES.contains(multipartFile.getContentType());

  private final StorageFileManager storageFileManager;

  public List<StorageAsset> list(String storingPath) {
    return storageFileManager.listFiles(storingPath)
        .stream()
        .map(buildStorageImage(storingPath))
        .collect(Collectors.toList());
  }

  public List<BatchProcessingResult> upload(List<MultipartFile> images, String storingPath) {
    validate(images);
    List<BatchProcessingResult> notUploaded = images.stream()
        .filter(isSupportedType)
        .map(uploadImage(storingPath))
        .filter(not(BatchProcessingResult::isSuccess))
        .collect(Collectors.toList());

    return images.stream()
        .filter(isSupportedType.negate())
        .map(MultipartFile::getOriginalFilename)
        .map(BatchProcessingResult::ofDescription)
        .collect(Collectors.toCollection(() -> notUploaded));
  }

  private static void validate(List<MultipartFile> images) {
    if (images.stream().anyMatch(multipartFile -> multipartFile.getSize() > MAX_FILE_SIZE)) {
      throw new AssetValidationException(VALIDATION_ERROR_MESSAGE);
    }
  }

  public BatchProcessingResult delete(String name, String storingPath) {
    String imageName = String.format("%s%s", storingPath, EncodingUtil.encodeURIComponent(name));
    storageFileManager.delete(imageName);
    return BatchProcessingResult.builder().description(name).success(true).build();
  }

  private Function<Blob, StorageAsset> buildStorageImage(String storingPath) {
    return blob -> {
      String[] splittedName = blob.getName().split("/");
      String imageName = splittedName[splittedName.length - 1];
      return StorageAsset.builder()
          .name(imageName)
          .url(String.format("%s/%s", storingPath, imageName))
          .createTime(blob.getCreateTime())
          .size(blob.getSize())
          .build();
    };
  }

  private Function<MultipartFile, BatchProcessingResult> uploadImage(String storingPath) {
    return image -> {
      String imageFileName = image.getOriginalFilename();
      String imageName = String.format("%s%s", storingPath, imageFileName);
      storageFileManager.upload(imageName, image.getContentType(), image, MultipartFile::getBytes);
      return BatchProcessingResult.builder()
          .description(imageFileName)
          .success(true)
          .build();
    };

  }


}
