package lithium.service.games.data.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "game_supplier",
    indexes = {
	    @Index(name = "idx_domain_name", columnList = "domain_id, name", unique = true),
      @Index(name = "idx_all", columnList = "domain_id, name, deleted", unique = false)
    }
)
public class GameSupplier implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Domain domain;

	@Column(nullable = false)
  private String name;

	@Column(nullable = false)
  @Builder.Default
  private Boolean deleted = false;

  @Column
  private Integer playersOnline;

	@PrePersist
  public void prePersist() {
	  if (deleted == null) deleted = false;
  }
}
