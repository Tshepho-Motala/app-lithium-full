package lithium.service.accounting.objects;

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
public class AdjustmentTransaction implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long transactionId;
	private AdjustmentResponseStatus status;
	
	public boolean isNew() {
		if (status == AdjustmentResponseStatus.NEW) return true;
		return false;
	}
	
	public enum AdjustmentResponseStatus {
		NEW,
		DUPLICATE,
		ERROR
	}
}
