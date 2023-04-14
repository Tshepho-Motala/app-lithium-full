package lithium.service.cdn.provider.google.builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import lithium.service.cdn.provider.google.config.ProviderConfig;
import lithium.service.cdn.provider.google.storage.objects.Key;
import org.modelmapper.ModelMapper;

/**
 *
 */
public class CredentialsBuilder {

  /**
   * @param config
   * @return
   * @throws IOException
   */
  public static GoogleCredentials build(ProviderConfig config) throws IOException {
    //Map config file to credentials file
    ModelMapper modelMapper = new ModelMapper();
    Key key = modelMapper.map(config, Key.class);

    //Convert credentials file to byte array
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonKey = objectMapper.writeValueAsString(key);

    //Due to new lines being escaped, we need to do some regex to clean up
    jsonKey = jsonKey.replaceAll("\\\\n", "\\n");

    //Create Google Credentials object required for GCP
    GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(jsonKey.getBytes()))
        .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

    return credentials;
  }
}
