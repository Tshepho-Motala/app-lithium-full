package lithium.service.client.provider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProviderConfig implements Serializable {
	private static final long serialVersionUID = -548050676153260537L;
	private String name;
	private ProviderType type;
	@Singular
	private List<ProviderConfigProperty> properties;
	
	public void addProperty(ProviderConfigProperty property) {
		if (properties == null) properties = new ArrayList<>();
		properties.add(property);
	}
	
	@AllArgsConstructor(access=AccessLevel.PRIVATE)
	public enum ProviderType implements Serializable {
		USER(1, "user"),
		AUTH(2, "auth"),
		CASINO(3,"casino"),
		CASHIER(4, "cashier"),
		AFFILIATE(5, "affiliate"),
		ACCOUNTING(6, "accounting"),
		ACCESS(7, "access"),
		KYC(8, "kyc"),
		CDN(9, "cdn"),
		DOCUMENT(10, "document"),
		PUB_SUB(11,"pub-sub"),
		DELIVERY(12, "delivery"),
		VERIFICATION(13, "verification"),
		REGISTER(14, "register"),
		REWARD(15, "reward"),
		GAMES(16, "games"),
		THRESHOLD(17, "threshold");


		@Getter
		@Accessors(fluent = true)
		private Integer id;
		@Getter
		@Accessors(fluent = true)
		private String type;
	}
}
