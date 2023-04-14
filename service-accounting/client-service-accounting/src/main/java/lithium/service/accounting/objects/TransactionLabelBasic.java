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
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class TransactionLabelBasic implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String labelName;
	private String labelValue;
	private boolean summarize;
}
