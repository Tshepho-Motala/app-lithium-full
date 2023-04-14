package lithium.service.xp.client.objects;

import java.util.List;

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
public class Scheme {
	private Long id;
	private int version;
	private Domain domain;
	private String name;
	private String description;
	private Status status;
	private Long wagerPercentage;
	private List<Level> levels;
	
	public Level findLevel(Integer number) {
		return levels.stream().filter(l -> {
			if (l.getNumber() == number) {
				return true;
			}
			return false;
		}).findFirst().orElse(null);
	}
}
