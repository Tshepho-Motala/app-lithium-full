package lithium.service.casino.exceptions;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class InvalidBonusException extends Exception {
	private static final long serialVersionUID = -4672116854573632856L;
	
	private List<String> errorMessages = Collections.emptyList();
}