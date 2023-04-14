package lithium.service.promo.client.objects;


import java.io.Serial;
import java.io.Serializable;
import lithium.service.promo.client.dto.FieldDataType;
import lithium.service.promo.client.dto.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ActivityExtraFieldBO implements Serializable {

  @Serial
  private static final long serialVersionUID = 3789418120886943837L;
  private long id;
  private String name;
  private FieldDataType dataType;
  private FieldType fieldType;
  private String description;
  private boolean fetchExternalData;
  private boolean required;
}
