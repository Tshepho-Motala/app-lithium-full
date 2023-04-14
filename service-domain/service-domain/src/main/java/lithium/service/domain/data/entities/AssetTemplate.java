package lithium.service.domain.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Version;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
@ToString
@EqualsAndHashCode
public class AssetTemplate {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable=false)
  private Boolean deleted;

  @Version
  private int version;

  @ManyToOne(fetch= FetchType.EAGER)
  @JoinColumn(nullable=false)
  private Domain domain;

  @Column(nullable=false)
  private String name;
  @Column()
  private String description;

  @Column(nullable = false)
  private String lang;

  @ManyToOne(fetch=FetchType.EAGER)
  @JoinColumn(nullable=true)
  private User createdBy;

  @PrePersist
  public void prePersist() {
    if (deleted == null) deleted = false;
  }
}
