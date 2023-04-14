package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_user",
    name = "closure_reason",
    indexes = {
        @Index(name = "idx_domain_id", columnList = "domain_id", unique = false),
        @Index(name = "idx_deleted", columnList = "deleted", unique = false)
    }
)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ClosureReason implements Serializable {

  private static final long serialVersionUID = 1L;
  @Column(nullable = false)
  boolean deleted;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Column(nullable = true)
  private String description;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "domain_id")
  @JsonManagedReference("closure_reason_domain")
  private Domain domain;
  @Column(nullable = false)
  private String text;
  @Version
  private int version;
}
