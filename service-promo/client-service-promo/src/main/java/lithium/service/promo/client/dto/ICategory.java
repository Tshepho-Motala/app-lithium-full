package lithium.service.promo.client.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.io.Serializable;

/**
 * This should be implemented by all CategoryDto enums defined in promo providers
 */
@JsonDeserialize(as = CategoryDto.class)
public interface ICategory extends Serializable {

  @JsonValue
  String getCategory() throws IOException;
}
