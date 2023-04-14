package lithium.service.shards.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Shard {
	private Long id;
	private int version;
	private Module module;
	private Pool pool;
	private String uuid;
	private boolean shutdown;
	private Date lastHeartbeat;
}
