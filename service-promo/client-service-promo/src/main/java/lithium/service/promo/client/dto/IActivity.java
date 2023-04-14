package lithium.service.promo.client.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.io.Serializable;

/**
 * This should be implemented by all ActivityDto enums defined in promo providers
 */
@JsonDeserialize(as = ActivityDto.class)
public interface IActivity extends Serializable {

  @JsonValue
  String getActivity() throws IOException;
}
