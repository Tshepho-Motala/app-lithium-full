package lithium.service.affiliate.client.objects;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Ad  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int AD_TYPE_LINK = 0;
	public static final int AD_TYPE_IMAGE = 1;
	public static final int AD_TYPE_IFRAME = 2;
	
	private Long id;
	
	int version;

	private String name;

	private String guid;

	private Boolean archived;

	private Boolean deleted;

	private Integer type;

	private Date createdDate;

	private Brand brand;

	private String targetUrl;

	private String entryPoint;
	
}
