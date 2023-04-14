package lithium.service.domain.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BankingDetails implements Serializable {
	private static final long serialVersionUID = -2666452849619665438L;
	
	private Long id;
	private int version;
	private String orgId; // id provided by bank
	private String bankIdentifierCode;
	private String accountHolder;
	private String accountNumber;
}
