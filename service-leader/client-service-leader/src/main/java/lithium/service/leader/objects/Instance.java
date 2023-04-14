package lithium.service.leader.objects;

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
public class Instance {
	private long id;
	private int version;
	private Module module;
	private String instanceId;
	private Date registered;
	private Date lastHeartbeat;
	private Boolean leader;
}
