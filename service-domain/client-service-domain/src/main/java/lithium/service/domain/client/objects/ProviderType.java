package lithium.service.domain.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderType implements Serializable {
	private static final long serialVersionUID = 8512089860398834607L;
	
	public static final String PROVIDER_TYPE_ACCOUNTING = "ACCOUNTING";
	public static final String PROVIDER_TYPE_AFFILIATE = "AFFILIATE";
	public static final String PROVIDER_TYPE_CASHIER = "CASHIER";
	public static final String PROVIDER_TYPE_CASINO = "CASINO";
	public static final String PROVIDER_TYPE_USER = "USER";
	public static final String PROVIDER_TYPE_AUTH = "AUTH";
	public static final String PROVIDER_TYPE_ACCESS = "ACCESS";
	public static final String PROVIDER_TYPE_REWARD = "REWARD";
	private Long id;
	private String name;
}