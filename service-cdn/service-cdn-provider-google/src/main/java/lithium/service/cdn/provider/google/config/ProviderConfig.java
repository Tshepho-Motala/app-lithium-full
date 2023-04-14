package lithium.service.cdn.provider.google.config;

import javax.validation.constraints.Pattern;
import lombok.Data;

/**
 *
 */
@Data
public class ProviderConfig {

  private String bucket;
  private String bucketPrefix; // TODO: This should be named something like "bucketTemplatePrefix"
  private String bucketImagePrefix;
  private String bucketCmsAssetPrefix;
  private String bucketCmsImagePrefix;
  private String cacheLength;
  private String uri;
  private String type;
  private String projectId;
  private String privateKeyId;
  private String privateKey;
  private String clientEmail;
  private String clientId;
  private String authUri;
  private String tokenUri;
  private String authProviderCertUrl;
  private String clientCertUrl;
}
