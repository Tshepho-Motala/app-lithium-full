package lithium.service.promo.client.dto;

import java.io.Serializable;
import java.util.List;
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
public class PromoProviderRegistration implements Serializable {

  private static final long serialVersionUID = -9026931884021447320L;
  private String name;
  private String url;
  private ICategory category;
  private List<PromoActivity> activities;

}