package lithium.service.casino.mock.all.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Track individual request + responses made to lithium provider implementations.
 * Timing for the executions are also stored and the unique tran id used for execution
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "mockActivity")
@EqualsAndHashCode(exclude = "mockActivity")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class MockActivityExecution implements Serializable {
	private static final long serialVersionUID = -1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(length = 3000)
	private String emulatedProviderRequest; // This will be sent back with response (v2?)

	@Column(length = 3000)
	private String emulatedLithiumResponse; // Raw response from lithium to provider (v2?)

	@Column(length = 3000)
	private String response; //This should come in a set format I think (indicator of the outcome of the request)

	private String transactionId; // This should consist of the mockSessionId + ms from epoch

	private Long lithiumTransactionId; // Transaction id returned from lithium when transactions were successful

	private Long executionDurationMs;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable=false)
	@JsonBackReference("MockActivityExecutions")
	private MockActivity mockActivity;
}