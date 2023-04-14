package lithium.service.domain.data.objects;

import lombok.Data;

@Data
public class ProviderLinkBasic {

	Long linkId;
	Long ownerLinkId;
	Boolean enabled;
	Boolean deleted;
}
