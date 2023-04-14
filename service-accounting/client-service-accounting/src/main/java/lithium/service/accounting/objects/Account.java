package lithium.service.accounting.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private Long balanceCents;
	private Currency currency;
	private User owner;
	private Domain domain;
	private AccountType accountType;
	private AccountCode accountCode;
}