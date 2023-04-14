package lithium.service.xp.data.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
	@Index(name="idx_domain_name", columnList="domain_id, name", unique=true)
})
public class Scheme {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable=true)
	private String description;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Status status;
	
	@Column(nullable=true)
	private Long wagerPercentage;

	@JsonManagedReference("scheme")
	@Fetch(FetchMode.SELECT)
	@OneToMany(mappedBy="scheme", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<Level> levels;
	
	public Level findLevel(Integer number) {
		return levels.stream().filter(l -> {
			if (l.getNumber() == number) {
				return true;
			}
			return false;
		}).findFirst().orElse(null);
	}
}
