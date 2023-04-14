package lithium.service.casino.client.objects.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor

public class CancelBonusResponse extends Response implements Serializable {
	private static final long serialVersionUID = 1L;
}