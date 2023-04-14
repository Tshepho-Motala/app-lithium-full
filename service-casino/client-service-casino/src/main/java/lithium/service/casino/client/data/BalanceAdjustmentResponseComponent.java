package lithium.service.casino.client.data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This component is used to hold transaction component responses after adjustments 
 * that form part of a single casino adjustment transaction that was initiated from the remote casino provier.
 */

@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Data
@Builder
@NoArgsConstructor
public class BalanceAdjustmentResponseComponent implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String result; //Can be NEW, DUPLICATE or ERROR
	private String extSystemTransactionId; //This is the transaction id the external system should use for tieback to accounting
}
