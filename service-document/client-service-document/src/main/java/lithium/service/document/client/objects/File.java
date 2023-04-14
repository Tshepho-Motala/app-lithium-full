package lithium.service.document.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class File implements Serializable {

	private static final long serialVersionUID = 1L;

	private byte[] data;
	
	private long size;
	
	private String md5Hash;
	
	private String mimeType;
	
	private String name;
}
