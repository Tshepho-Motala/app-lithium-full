package lithium.service.access.data.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude= {"accessControlListTransactionData", "externalListTransactionData"})

/**
 * Part of the audit trail for access rule executions.
 * Origination point of a access rule execution.
 * The id here is used for reference lookup from caller systems.
 */
public class AccessRuleTransaction {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private int version;

	private Date creationDate;

	private String ipAddress;

	private String country;

	private String claimedCountry;

	private String state;

	private String claimedState;

	private String city;

	private String claimedCity;

	private String browser;

	private String os;

	@Lob
	private String deviceId; //blackbox

	@JoinColumn
	@ManyToOne
	private User user;

	@OneToMany(fetch=FetchType.EAGER, mappedBy="accessRuleTransaction")
	@JsonManagedReference("accessControlListTransactionData")
	private java.util.List<AccessControlListTransactionData> accessControlListTransactionData;

	@OneToMany(fetch=FetchType.EAGER, mappedBy="accessRuleTransaction")
	@JsonManagedReference("externalListTransactionData")
	private java.util.List<AccessControlListTransactionData> externalListTransactionData;
}
