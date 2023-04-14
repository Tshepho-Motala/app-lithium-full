package lithium.service.games.provider.google.rge.configs;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ProviderConfigProperties {

    TYPE("type"),
    PROJECT_ID("projectId"),
    PRIVATE_KEY_ID("privateKeyId"),
    PRIVATE_KEY("privateKey"),
    CLIENT_EMAIL("clientEmail"),
    CLIENT_ID("clientId"),
    AUTH_URI("authUri"),
    TOKEN_URI("tokenUri"),
    AUTH_PROVIDER_X509_CERT_URL("authProviderX509CertUrl"),
    CLIENT_X509_CERT_URL("clientX509CertUrl"),
    BUCKET_NAME("bucketName"),
    PREDICT_URL("predictURL"),
    PROJECT("project"),
    LOCATION("location"),
    ENDPOINT("endpoint"),
    PAGE_SIZE("pageSize");

    @Getter
    private String name;

}
