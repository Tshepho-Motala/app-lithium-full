package lithium.service.limit.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PlayerLimit implements Serializable {
	private static final long serialVersionUID = -1;

	private Long id;

	private int version;

	private String playerGuid;
	
	private String domainName;

	private int granularity;
	
	private long amount;

	private int type;

	private long createdDate;
	private long modifiedDate;

	private long amountUsed;
}
