package lithium.service.limit.data.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
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
import lithium.service.limit.converter.EnumConverter;
import lithium.service.limit.enums.AutoRestrictionRuleSetOutcome;
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
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes={
	@Index(name="idx_domain_name", columnList="domain_id, name", unique=true),
	@Index(name="idx_last_updated", columnList="lastUpdated", unique=false),
	@Index(name="idx_last_updated_by", columnList="lastUpdatedBy", unique=false),
	@Index(name="idx_enabled", columnList="enabled", unique=false),
	@Index(name="idx_deleted", columnList="deleted", unique=false)
})
public class AutoRestrictionRuleSet {
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
	private boolean enabled;

	@Column(nullable=false)
	private boolean deleted;

	@Column(nullable=false)
	private boolean skipTestUser;

	@Column(nullable=false)
	@ColumnDefault(value="false")
	@Builder.Default
	private boolean rootOnly = false;

	@Column(nullable=false)
	@ColumnDefault(value="false")
	@Builder.Default
	private boolean allEcosystem = false;

	@Column(nullable=false)
	@DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
	private Date lastUpdated;

	@Column(nullable=false)
	private String lastUpdatedBy;

	@Column(nullable=false)
	@Convert(converter=EnumConverter.OutcomeConverter.class)
	private AutoRestrictionRuleSetOutcome outcome;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	private DomainRestrictionSet restrictionSet;

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.EAGER, mappedBy="ruleset", cascade=CascadeType.MERGE)
	@Where(clause="deleted=false")
	@JsonManagedReference("ruleset")
	private List<AutoRestrictionRule> rules;
}
