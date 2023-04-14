package lithium.service.casino.mock.all.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Holds the session data for mock initialisation requests.
 * The id field will be used by subsequent "action" requests as part of their parameters.
 *
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(indexes = {
		@Index(name = "idx_ms_all", columnList = "userGuid, authToken, providerGuid, providerGameId", unique = true)
})
public class MockSession implements Serializable {
	private static final long serialVersionUID = -1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String userGuid;

	@Column(length = 3000)
	private String authToken;

	private String gameStartUrl;

	private String providerGuid;

	private String providerGameId;

	private String currency;
}