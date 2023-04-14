package lithium.service.cdn.provider.google.storage.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import lombok.Data;

/**
 *
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"type", "project_id", "private_key_id", "private_key", "client_email", "client_id", "auth_uri", "token_uri",
    "auth_provider_x509_cert_url", "client_x509_cert_url"})
public class Key implements Serializable {

  @JsonProperty("type")
  public String type;

  @JsonProperty("project_id")
  public String projectId;

  @JsonProperty("private_key_id")
  public String privateKeyId;

  @JsonProperty("private_key")
  public String privateKey;

  @JsonProperty("client_email")
  public String clientEmail;

  @JsonProperty("client_id")
  public String clientId;

  @JsonProperty("auth_uri")
  public String authUri;

  @JsonProperty("token_uri")
  public String tokenUri;

  @JsonProperty("auth_provider_x509_cert_url")
  public String authProviderCertUrl;

  @JsonProperty("client_x509_cert_url")
  public String clientCertUrl;

  /**
   *
   */
  public Key() {
  }
}
