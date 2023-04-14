package lithium.service.raf.client.objects;

import lithium.service.raf.enums.RAFConversionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RAFConversionRequest {
	private String playerGuid;
	private RAFConversionType type;
	private Integer xpLevel;
}
