package lithium.service.shards.storage.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
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
import java.util.Date;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
	indexes = {
		@Index(name = "idx_shard", columnList = "module_id, pool_id, uuid", unique = true),
		@Index(name = "idx_shard_shutdown_last_heartbeat", columnList = "shutdown, lastHeartbeat", unique = false),
		@Index(name = "idx_shard_uuid", columnList = "uuid", unique = true)
	}
)
public class Shard {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Version
	private int version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Module module;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Pool pool;

	@Column(nullable = false)
	private String uuid;

	@Column(nullable = false)
	@Builder.Default
	private boolean shutdown = false;

	@Column(nullable = false)
	@Builder.Default
	private Date lastHeartbeat = new Date();
}
