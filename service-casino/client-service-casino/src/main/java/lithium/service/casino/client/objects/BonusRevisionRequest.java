package lithium.service.casino.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.bind.annotation.PathVariable;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BonusRevisionRequest {
	private String domainName;
	private Integer bonusType;
	private String bonusCode;
}
