package lithium.service.cdn.provider.google.service.asset.Impl;

import com.google.cloud.storage.Blob;
import lithium.service.cdn.provider.google.service.asset.TemplateAssetService;
import lithium.service.cdn.provider.google.service.asset.model.BatchProcessingResult;
import lithium.service.cdn.provider.google.service.asset.model.StorageAsset;
import lithium.service.cdn.provider.google.service.storage.StorageDetails;
import lithium.service.cdn.provider.google.service.storage.StorageFileManager;
import lithium.service.cdn.provider.google.service.storage.utils.CdnBucketType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class TemplateAssetServiceImpl implements TemplateAssetService {
  private final StorageAssetService imagesService;
  private final StorageDetails storageDetails;

  @Override
  public List<StorageAsset> list(String language, CdnBucketType bucketType) {
    return imagesService.list(getStoringPath(language, bucketType));
  }

  @Override
  public List<BatchProcessingResult> upload(String language, List<MultipartFile> images, CdnBucketType bucketType) {
    return imagesService.upload(images, getStoringPath(language, bucketType));
  }

  @Override
  public BatchProcessingResult delete(String language, String name, CdnBucketType bucketType) {
    return imagesService.delete(name, getStoringPath(language,bucketType));
  }

  private String getStoringPath(String language, CdnBucketType bucketType) {
    return storageDetails.getBucketPrefix(language, bucketType);
  }
}
