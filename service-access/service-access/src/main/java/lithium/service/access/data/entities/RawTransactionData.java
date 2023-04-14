package lithium.service.access.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor

/**
 * Storage of raw data responses from provider implementations.
 * Will be wiped periodically by scheduled script.
 */
@Table(indexes = {
    @Index(name="idx_creation_date", columnList="creationDate")
})
public class RawTransactionData {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Date creationDate; //When certain threshold is reached, data will be wiped

	@OneToOne
	@JoinColumn(nullable = true)
	private AccessControlListTransactionData accessControlListTransactionData;

	@OneToOne
	@JoinColumn(nullable = true)
	private ExternalListTransactionData externalListTransactionData;

	@Lob
	private String rawRequestData;

	@Lob
	private String rawResponseData;

}
