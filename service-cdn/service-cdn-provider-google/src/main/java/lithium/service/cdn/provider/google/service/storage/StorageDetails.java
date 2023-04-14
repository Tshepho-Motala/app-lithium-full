package lithium.service.cdn.provider.google.service.storage;

import lithium.service.cdn.provider.google.service.storage.utils.CdnBucketType;

public interface StorageDetails {

  String getBucketPrefix();

  String getBucketPrefix(String language, CdnBucketType bucketType);

  String getURI();
}
