package lithium.service.avatar.client.objects;

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
public class Graphic {
	private Long id;
	private int version;
	private byte[] image;
	private String name;
	private Long size;
	private String type;
}
