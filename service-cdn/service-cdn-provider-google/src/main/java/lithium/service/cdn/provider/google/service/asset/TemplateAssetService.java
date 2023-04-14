package lithium.service.cdn.provider.google.service.asset;

import com.google.cloud.storage.Blob;
import lithium.service.cdn.provider.google.service.asset.model.BatchProcessingResult;
import lithium.service.cdn.provider.google.service.asset.model.StorageAsset;
import lithium.service.cdn.provider.google.service.storage.utils.CdnBucketType;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;


public interface TemplateAssetService {
  List<StorageAsset> list(String language, CdnBucketType bucketType);

  List<BatchProcessingResult> upload(String language, List<MultipartFile> images, CdnBucketType bucketType);

  BatchProcessingResult delete(String language, String name, CdnBucketType bucketType);



}
