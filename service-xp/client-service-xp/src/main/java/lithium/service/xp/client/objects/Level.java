package lithium.service.xp.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Level {
	private Long id;
	private int version;
	private Scheme scheme;
	private Integer number;
	private Long requiredXp;
	private Boolean milestone;
	private String description;
	private LevelBonus bonus;
	private List<LevelNotification> notifications;
}
