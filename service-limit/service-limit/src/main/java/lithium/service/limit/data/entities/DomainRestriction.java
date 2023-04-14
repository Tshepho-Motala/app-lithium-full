package lithium.service.limit.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

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

@Data
@Entity
@Builder
@ToString(exclude="set")
@EqualsAndHashCode(exclude="set")
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes={
	@Index(name="idx_enabled", columnList="enabled", unique=false),
	@Index(name="idx_deleted", columnList="deleted", unique=false),
	@Index(name="idx_set_restriction", columnList="set_id, restriction_id", unique=true)
})
public class DomainRestriction {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference("set")
	private DomainRestrictionSet set;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Restriction restriction;

	@Column(nullable=false)
	@ColumnDefault(value="true")
	@Builder.Default
	private boolean enabled = true;

	@Column(nullable=false)
	@ColumnDefault(value="false")
	@Builder.Default
	private boolean deleted = false;

	// TODO: Other domain specific properties go here
}
