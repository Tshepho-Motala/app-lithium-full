package lithium.service.leaderboard.client.objects;

import java.io.Serializable;

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
public class Domain implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private int version;
	private String name;
}
