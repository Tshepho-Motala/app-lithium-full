package lithium.service.avatar.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Avatar {
	private Long id;
	private Domain domain;
	private String name;
	private String description;
	private Graphic graphic;
	private GraphicBasic graphicBasic;
	private Boolean enabled;
	private Boolean isDefault;
}
