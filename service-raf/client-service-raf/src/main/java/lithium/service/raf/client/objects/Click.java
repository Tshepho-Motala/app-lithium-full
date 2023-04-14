package lithium.service.raf.client.objects;

import java.util.Date;

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
public class Click {
	private Long id;
	private int version;
	private Referrer referrer;
	private Date timestamp;
	private String ip;
	private String userAgent;
}
