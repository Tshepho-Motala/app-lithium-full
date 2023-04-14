package lithium.service.casino.mock.all.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Track individual request + responses made to lithium provider implementations.
 * Timing for the executions are also stored and the unique tran id used for execution
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class MockActivity implements Serializable {
	private static final long serialVersionUID = -1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(nullable = false)
	private MockSession mockSession;

	@Column(length = 3000)
	private String request;  //Might not be relevant

	private String roundId; //Keep this in here for easy lookup in case tie-back is needed

	@Transient
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JsonManagedReference("MockActivityExecutions")
	private List<MockActivityExecution> mockActivityExecutionList;
}