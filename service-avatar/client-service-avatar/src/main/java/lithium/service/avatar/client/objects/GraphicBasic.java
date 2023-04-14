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
public class GraphicBasic {
	private String filetype;
	private String filename;
	private long filesize;
	private byte[] base64;
}
