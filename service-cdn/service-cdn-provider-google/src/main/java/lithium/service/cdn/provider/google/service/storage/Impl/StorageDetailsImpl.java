package lithium.service.cdn.provider.google.service.storage.Impl;

import com.google.cloud.storage.Storage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lithium.modules.ModuleInfo;
import lithium.service.cdn.provider.google.builders.CacheBuilder;
import lithium.service.cdn.provider.google.builders.StorageSupplier;
import lithium.service.cdn.provider.google.config.ProviderConfig;
import lithium.service.cdn.provider.google.config.ProviderConfigService;
import lithium.service.cdn.provider.google.config.Status500ProviderNotConfiguredException;
import lithium.service.cdn.provider.google.service.storage.RequestInitializable;
import lithium.service.cdn.provider.google.service.storage.StorageDetails;
import lithium.service.cdn.provider.google.service.storage.StorageProvider;
import lithium.service.cdn.provider.google.service.storage.utils.BucketPrefixProcessor;
import lithium.service.cdn.provider.google.service.storage.utils.CdnBucketType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
@RequiredArgsConstructor
public class StorageDetailsImpl implements StorageDetails, StorageProvider, RequestInitializable {

  private final Map<CdnBucketType, String> bucketConfigMap = new HashMap<>();
  private final ProviderConfigService providerConfigService;
  private final ModuleInfo moduleInfo;
  private final StorageSupplier storageSupplier;

  @Getter
  private Storage storage;

  private ProviderConfig config;

  @Override
  public void initialize(String domainName) throws Status500ProviderNotConfiguredException, IOException {
    config = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);
    storage = storageSupplier.get(config);

    bucketConfigMap.put(CdnBucketType.TEMPLATE, config.getBucketPrefix());
    bucketConfigMap.put(CdnBucketType.IMAGE, config.getBucketImagePrefix());
    bucketConfigMap.put(CdnBucketType.CMS_ASSET, config.getBucketCmsAssetPrefix());
    bucketConfigMap.put(CdnBucketType.CMS_IMAGE, config.getBucketCmsImagePrefix());
  }

  @Override
  public String getBucket() {
    return config.getBucket();
  }

  @Override
  public String getBucketPrefix() {
    // Keeping this here for backwards compatability, the only use is for TEMPLATE
    return getBucketPrefix("", CdnBucketType.TEMPLATE);
  }

  @Override
  public String getBucketPrefix(String language, CdnBucketType bucketType) {
    return BucketPrefixProcessor.prepare(bucketConfigMap.get(bucketType), language);
  }

  @Override
  public String getCacheControl() {
    return CacheBuilder.build(config.getCacheLength());
  }

  @Override
  public String getURI() {
    return config.getUri();
  }

}
