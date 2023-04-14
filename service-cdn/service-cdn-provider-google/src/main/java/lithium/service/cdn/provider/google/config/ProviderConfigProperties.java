package lithium.service.cdn.provider.google.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 *
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum ProviderConfigProperties {
  BUCKET("bucket"),
  BUCKET_PREFIX("bucketPrefix"),
  BUCKET_IMAGE_PREFIX("bucketImagePrefix"),
  BUCKET_CMS_IMAGE_PREFIX("bucketCmsImagePrefix"),
  BUCKET_CMS_ASSET_PREFIX("bucketCmsAssetPrefix"),
  CACHE_LENGTH("cacheLength"),
  URI("uri"),
  TYPE("type"),
  PROJECT_ID("projectId"),
  PRIVATE_KEY_ID("privateKeyId"),
  PRIVATE_KEY("privateKey"),
  CLIENT_EMAIL("clientEmail"),
  CLIENT_ID("clientId"),
  AUTH_URI("authUri"),
  TOKEN_URI("tokenUri"),
  AUTH_PROVIDER_CERT_URL("authProviderCertUrl"),
  CLIENT_CERT_URL("clientCertUrl");

  @Getter
  private final String value;

}
