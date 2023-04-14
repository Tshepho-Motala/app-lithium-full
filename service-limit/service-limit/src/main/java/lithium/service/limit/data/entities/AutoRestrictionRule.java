package lithium.service.limit.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lithium.service.limit.converter.EnumConverter;
import lithium.service.limit.enums.AutoRestrictionRuleField;
import lithium.service.limit.enums.AutoRestrictionRuleOperator;
import lithium.service.limit.enums.RestrictionEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
import javax.persistence.Table;
import javax.persistence.Version;

@Data
@Entity
@Builder
@ToString(exclude="ruleset")
@EqualsAndHashCode(exclude="ruleset")
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
	@Index(name="idx_ruleset_field", columnList="ruleset_id, field", unique=true),
	@Index(name="idx_deleted", columnList="deleted", unique=false)
})
public class AutoRestrictionRule {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(nullable=false)
	@JsonBackReference("ruleset")
	private AutoRestrictionRuleSet ruleset;

	@Column(nullable=false)
	private boolean enabled;

	@Column(nullable=false)
	private boolean deleted;

	private Long delay;
	@Column(nullable=true)
	@Convert(converter=EnumConverter.ResrtictionEventConverter.class)
	private RestrictionEvent event;

	@Column(nullable=false)
	@Convert(converter=EnumConverter.FieldConverter.class)
	private AutoRestrictionRuleField field;

	@Column
	@Convert(converter=EnumConverter.OperatorConverter.class)
	private AutoRestrictionRuleOperator operator;

	@Column(nullable=false, length=1000) // When used with the IN operator, this should hold a comma separated list
	private String value;

	@Column(nullable=true)
	private String value2; // Used for the BETWEEN operator
}
