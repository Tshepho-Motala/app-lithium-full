package lithium.service.promo.client.objects;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString( exclude = {"extraFields"} )
public class PromoProvider implements Serializable {

  @Serial
  private static final long serialVersionUID = -1902329575774709341L;
  private long id;

  private String url;
  private String name;
  private String category;

  @Singular
  @JsonManagedReference("promoProvider")
  private List<Activity> activities;

  @Singular
  @JsonManagedReference( "promoProvider" )
  private List<ActivityExtraField> extraFields;
}