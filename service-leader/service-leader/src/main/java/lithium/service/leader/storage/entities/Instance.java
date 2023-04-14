package lithium.service.leader.storage.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.Date;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
	indexes = {
		@Index(name = "idx_module_instance", columnList = "module_id, instanceId", unique = true),
		@Index(name = "idx_last_heartbeat", columnList = "lastHeartbeat"),
		@Index(name = "idx_module_leader", columnList = "module_id, leader", unique = true)
	}
)
public class Instance {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Version
	private int version;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Module module;

	@Column(nullable = false)
	private String instanceId;

	@Column(nullable = false)
	@Builder.Default
	private Date registered = new Date();

	@Column(nullable = false)
	@Builder.Default
	private Date lastHeartbeat = new Date();

	@Column
	private Boolean leader;
}
