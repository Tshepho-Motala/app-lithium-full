package lithium.service.accounting.objects;

import java.io.Serializable;
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

public class Transaction implements Serializable {

	static final long serialVersionUID = 1L;
	
	private long id;
	
	int version;
	
	private Boolean open;

	private Boolean cancelled;

	private Date createdOn;
	
	private Date closedOn;

	private User author;

	private TransactionType transactionType;

}
