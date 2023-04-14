package lithium.service.promo.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lithium.service.promo.client.dto.FieldDataType;
import lithium.service.promo.client.dto.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "activity")
@Table(indexes = {
        @Index(name = "idx_activity_extra_field_name", columnList = "activity_id, name", unique = true ),} )
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id" )
public class ActivityExtraField implements Serializable {

  @Serial
  private static final long serialVersionUID = -1425235990073222406L;
  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private long id;

  @ManyToOne( fetch = FetchType.EAGER )
  @JoinColumn( nullable = false )
  @JsonBackReference("activity")
  private Activity activity;

  private String name;

  @Enumerated( EnumType.STRING )
  private FieldDataType dataType;

  private String description;

  @Enumerated( EnumType.STRING )
  private FieldType fieldType;

  private boolean fetchExternalData;
  private boolean required;

}
