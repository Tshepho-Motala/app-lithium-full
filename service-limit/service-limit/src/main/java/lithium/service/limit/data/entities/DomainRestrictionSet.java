package lithium.service.limit.data.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

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
@Table(indexes={
	@Index(name="idx_domain_name", columnList="domain_id, name", unique=true),
	@Index(name="idx_enabled", columnList="enabled", unique=false),
	@Index(name="idx_deleted", columnList="deleted", unique=false),
	@Index(name="idx_system_restriction", columnList="systemRestriction", unique=false)
})
public class DomainRestrictionSet {
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

	@Column(nullable=false)
	@ColumnDefault(value="false")
	@Builder.Default
	private boolean systemRestriction = false;

	@Column(nullable=false)
	@ColumnDefault(value="true")
	@Builder.Default
	private boolean enabled = true;

	@Column(nullable=false)
	@ColumnDefault(value="false")
	@Builder.Default
	private boolean dwhVisible = false;

	@Column(nullable=false)
	@ColumnDefault(value="false")
	@Builder.Default
	private boolean deleted = false;

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.EAGER, mappedBy="set", cascade=CascadeType.MERGE)
	@Where(clause="deleted=false")
	@JsonManagedReference("set")
	private List<DomainRestriction> restrictions;

	@Column
	@ColumnDefault(value = "0")
	private int altMessageCount;

	public String errorType() {
		if (this.systemRestriction) {
			return "ERROR_DICTIONARY.SYSTEM_RESTRICTION";
		}
		return "ERROR_DICTIONARY.NORMAL_RESTRICTION";
	}

	public String errorMessageKey() {
		return this.errorType() + "." + this.id;
	}

	@Column(nullable=false)
	@ColumnDefault(value="false")
	@Builder.Default
	private boolean communicateToPlayer = false;

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.EAGER, mappedBy="set", cascade = CascadeType.MERGE)
	@JsonManagedReference("set")
	private List<RestrictionOutcomePlaceAction> placeActions;

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.EAGER, mappedBy="set", cascade = CascadeType.MERGE)
	@JsonManagedReference("set")
	private List<RestrictionOutcomeLiftAction> liftActions;

	@Column(name = "exclude_tag_id")
	private Long excludeTagId;

    @Column
    private String placeMailTemplate;

    @Column
    private String liftMailTemplate;
}
