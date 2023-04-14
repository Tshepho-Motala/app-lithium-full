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
/**
 * a component used to perform a rollback on a transaction.
 * @author Chris
 *
 */
public class RollbackRequestComponent implements Serializable {
	private static final long serialVersionUID = 1L;
	private String[] labels;
	private String authorGuid;
	private Boolean allowNegativeAdjust;
	private CompleteTransaction originalTransaction;
}
