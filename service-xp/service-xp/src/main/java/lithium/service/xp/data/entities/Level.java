package lithium.service.xp.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.List;

@Entity
@Data
@ToString(exclude="scheme")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="scheme")
@Table(indexes = {
	@Index(name="idx_scheme_number", columnList="scheme_id, number", unique=true)
})
public class Level {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@JsonBackReference("scheme")
	@ManyToOne(fetch=FetchType.EAGER)
	private Scheme scheme;
	
	@Column(nullable=false)
	private Integer number;
	
	@Column(nullable=false)
	private Long requiredXp;

	@Column(nullable=false)
	private String description;

	@Column(nullable=true)
	private Boolean milestone;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=true)
	@JsonManagedReference("level")
	private LevelBonus bonus;

	@OneToMany(fetch=FetchType.EAGER, mappedBy="level", cascade=CascadeType.ALL)
	@JsonManagedReference("level")
	private List<LevelNotification> notifications;
}
