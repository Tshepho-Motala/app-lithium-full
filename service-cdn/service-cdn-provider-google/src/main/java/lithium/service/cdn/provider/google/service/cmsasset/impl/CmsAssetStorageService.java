package lithium.service.cdn.provider.google.service.cmsasset.impl;

import static lithium.service.cdn.provider.google.service.utils.PredicateUtils.not;

import com.google.cloud.storage.Blob;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lithium.service.cdn.provider.google.service.asset.model.BatchProcessingResult;
import lithium.service.cdn.provider.google.service.asset.model.StorageAsset;
import lithium.service.cdn.provider.google.service.asset.util.EncodingUtil;
import lithium.service.cdn.provider.google.service.storage.StorageFileManager;
import lithium.service.cdn.provider.google.service.storage.exception.AssetValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("cmsasset")
@RequiredArgsConstructor
public class CmsAssetStorageService {

  public static final String VALIDATION_ERROR_MESSAGE = "This file is larger than 10MB and can not be uploaded.";
  private static final int MAX_FILE_SIZE = 10000000;

  private final StorageFileManager storageFileManager;

  public List<StorageAsset> list(String storingPath) {
    return storageFileManager.listFiles(storingPath)
        .stream()
        .map(buildStorageAsset(storingPath))
        .collect(Collectors.toList());
  }

  public List<BatchProcessingResult> upload(List<MultipartFile> images, String storingPath) {
    validate(images);
    List<BatchProcessingResult> notUploaded = images.stream()
        .map(uploadAsset(storingPath))
        .filter(not(BatchProcessingResult::isSuccess))
        .collect(Collectors.toList());

    return images.stream()
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

  private Function<Blob, StorageAsset> buildStorageAsset(String storingPath) {
    return blob -> {
      String[] splittedName = blob.getName().split("/");
      String assetName = splittedName[splittedName.length - 1];
      return StorageAsset.builder()
          .name(assetName)
          .url(String.format("%s/%s", storingPath, assetName))
          .createTime(blob.getCreateTime())
          .size(blob.getSize())
          .build();
    };
  }

  private Function<MultipartFile, BatchProcessingResult> uploadAsset(String storingPath) {
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
