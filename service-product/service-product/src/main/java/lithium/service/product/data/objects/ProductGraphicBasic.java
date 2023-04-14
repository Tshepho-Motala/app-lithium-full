package lithium.service.product.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude="image")
public class ProductGraphicBasic {
	private long productId;
	private byte[] image;
	private String graphicFunctionName;
	@Default
	private boolean deleted = false;
	@Default
	private boolean enabled = true;
	private String domainName; //Just for an extra check internally
}