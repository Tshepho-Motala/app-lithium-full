package lithium.service.casino.client.data;

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
	private static final long serialVersionUID = 1L;
	private int version;
	private long id;
	private byte[] image;
	private long size;
	private String md5Hash;
	private boolean deleted;
	private String fileType;
}
