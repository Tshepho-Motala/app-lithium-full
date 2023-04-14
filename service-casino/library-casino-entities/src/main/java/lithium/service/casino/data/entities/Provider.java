package lithium.service.casino.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.jpa.entity.EntityWithUniqueGuid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Table(indexes = {
    @Index(name="idx_guid", columnList="guid", unique=true)
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Provider implements EntityWithUniqueGuid {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version
  private int version;

  @Column(nullable = false)
  private String guid;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Domain domain;
}
