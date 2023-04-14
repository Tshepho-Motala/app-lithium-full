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
public class AccountCode implements Serializable {
	private static final long serialVersionUID = -6265454385326569191L;
	private Long id;
	private String code;
}