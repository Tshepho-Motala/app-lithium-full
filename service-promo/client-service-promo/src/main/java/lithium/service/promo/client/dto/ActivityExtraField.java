package lithium.service.promo.client.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ActivityExtraField implements Serializable {

  private static final long serialVersionUID = -1L;
  private String name; // e.g. league / odds
  private FieldDataType type; // e.g. string / number, money
  private FieldType fieldType; // e.g input, singleselect, multiselect
  private String description;

  @Builder.Default
  private Boolean fetchExternalData = Boolean.FALSE;

  @Builder.Default
  private Boolean required = Boolean.FALSE;
}