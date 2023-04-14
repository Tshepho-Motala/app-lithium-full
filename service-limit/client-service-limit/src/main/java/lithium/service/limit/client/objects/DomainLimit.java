package lithium.service.limit.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DomainLimit implements Serializable {
	private static final long serialVersionUID = -1;
	
	private Long id;

	private int version;

	private String domainName;

	private int granularity;
	
	private long amount; //Net loss/win amount
	
	private int type;
}