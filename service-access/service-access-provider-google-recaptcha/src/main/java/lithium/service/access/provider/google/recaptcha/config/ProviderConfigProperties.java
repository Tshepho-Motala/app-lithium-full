package lithium.service.access.provider.google.recaptcha.config;

import lombok.Getter;

public enum ProviderConfigProperties {
    SECRET_KEY("secret_key"),
    SITE_KEY("site_key"),
    RECAPTCHA_SERVICE_URL("recaptcha_service_url"),
    SCORE("score"),
    CONNECTION_REQUEST_TIMEOUT("connectionRequestTimeout"),
    CONNECT_TIMEOUT("connectTimeout"),
    SOCKET_TIMEOUT("socketTimeout");

    @Getter
    private final String value;

    ProviderConfigProperties(String valueParam) {
        value = valueParam;
    }
}
