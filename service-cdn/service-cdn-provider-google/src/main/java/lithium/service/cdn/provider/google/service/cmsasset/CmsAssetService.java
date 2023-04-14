package lithium.service.cdn.provider.google.service.cmsasset;

import lithium.service.cdn.provider.google.service.asset.model.BatchProcessingResult;
import lithium.service.cdn.provider.google.service.asset.model.StorageAsset;
import lithium.service.cdn.provider.google.service.storage.utils.CdnBucketType;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface CmsAssetService {
  List<StorageAsset> list(String language, CdnBucketType bucketType);
  List<BatchProcessingResult> upload(String language, List<MultipartFile> images, CdnBucketType bucketType);
  BatchProcessingResult delete(String language, String name, CdnBucketType bucketType);
}
