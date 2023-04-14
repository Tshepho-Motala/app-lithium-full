package lithium.service.games.data.entities.supplier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.service.games.client.objects.supplier.GameVerticalEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierGameMetaVertical {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(columnDefinition = "enum('LIVE')")
  @Enumerated(EnumType.STRING)
  private GameVerticalEnum name;

}
