package lithium.service.cdn.provider.google.service.cmsasset.impl;

import lithium.service.cdn.provider.google.service.asset.model.BatchProcessingResult;
import lithium.service.cdn.provider.google.service.asset.model.StorageAsset;
import lithium.service.cdn.provider.google.service.cmsasset.CmsAssetService;
import lithium.service.cdn.provider.google.service.storage.StorageDetails;
import lithium.service.cdn.provider.google.service.storage.utils.CdnBucketType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CmsAssetServiceImpl implements CmsAssetService {
  private final StorageDetails storageDetails;
  private final CmsAssetStorageService assetStorageService;

  @Override
  public List<StorageAsset> list(String language, CdnBucketType bucketType) {
    return assetStorageService.list(getStoringPath(language, bucketType));
  }

  @Override
  public List<BatchProcessingResult> upload(String language, List<MultipartFile> images, CdnBucketType bucketType) {
    return assetStorageService.upload(images, getStoringPath(language, bucketType));
  }

  @Override
  public BatchProcessingResult delete(String language, String name, CdnBucketType bucketType) {
    return assetStorageService.delete(name, getStoringPath(language,bucketType));
  }

  private String getStoringPath(String language, CdnBucketType bucketType) {
    return storageDetails.getBucketPrefix(language, bucketType);
  }
}
