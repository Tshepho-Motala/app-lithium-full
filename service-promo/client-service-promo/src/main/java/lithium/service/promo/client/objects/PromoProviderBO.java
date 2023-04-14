package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
//@ToString( exclude = {"extraFields"} )
public class PromoProviderBO implements Serializable {

  @Serial
  private static final long serialVersionUID = -1341016655846915711L;
  private long id;

  @NotEmpty(message = "url is a required field")
  private String url;
  private String name;

  @NotEmpty(message = "category is a required field")
  private String category;

  //  @Singular
  //  @JsonManagedReference("promoProvider")
  //  private List<Activity> activities;
  //
  //  @Singular
  //  @JsonManagedReference( "promoProvider" )
  //  private List<ActivityExtraField> extraFields;
}