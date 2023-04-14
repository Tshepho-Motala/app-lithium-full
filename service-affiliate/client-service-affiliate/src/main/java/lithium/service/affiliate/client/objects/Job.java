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
public class Job  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String domain;

	private int period;
}
