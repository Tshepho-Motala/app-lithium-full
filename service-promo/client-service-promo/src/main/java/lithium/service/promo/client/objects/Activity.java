package lithium.service.promo.client.objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serial;
import java.io.Serializable;
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
public class Activity implements Serializable {

  @Serial
  private static final long serialVersionUID = 5830819194758562313L;
  private long id;

  @JsonBackReference( "promoProvider" )
  private PromoProvider promoProvider;

  private String name;
}