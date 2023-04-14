package lithium.service.access.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity to maintain a list of access rule execution responses and whether
 * they can be possible access service response outcomes used for rule interpretation.
 * Setting output to true shows it to be a possible output response from the rule execution.
 * Setting outcome to true shows it is a possible option as a final rule interpretation response to calling systems.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
/**
 * Contains the possible values of outputs from providers and/or
 * outputs allowed to proceed to callers of authorization methods.
 */
public class AccessRuleResultStatusOptions {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	
	@Column( name="name", nullable=false)
	private String name ;

	/**
	 * True of it is a possible output to the caller system
	 */
	@Column( name="output", nullable=false)
	private boolean output ;

	/**
	 * True if it is a possible outcome from the provider
	 */
	@Column( name="outcome", nullable=false)
	private boolean outcome ;
	
}
