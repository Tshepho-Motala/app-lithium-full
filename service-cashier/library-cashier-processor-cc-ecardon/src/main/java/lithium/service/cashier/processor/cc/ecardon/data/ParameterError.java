package lithium.service.cashier.processor.cc.ecardon.data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParameterError implements Serializable {
	private static final long serialVersionUID = 335500011785598806L;
	private String name;
	private String value;
	private String message;
}