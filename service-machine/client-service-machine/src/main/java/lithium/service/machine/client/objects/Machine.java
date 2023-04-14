package lithium.service.machine.client.objects;

import java.util.Date;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Machine {
	private Long id;
	private String guid;
	private Status status;
	private String name;
	private String description;
	private Domain domain;
	private Date createdDate;
	private Date updatedDate;
	private Date lastPing;
	private Location location;
	private Set<Relationship> relationships;
}
