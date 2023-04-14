package lithium.service.product.client.objects;

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
public class ProductPurchase {
//	[Purchase. Json:
//	{
//	    "orderId":"GPA.3317-8176-4258-98671",
//	    "packageName": package.name.test,
//	    "productId":"gas",
//	    "purchaseTime":1522934705928,
//	    "purchaseState":0,
//	    "purchaseToken":"ihincppnajieemhjhmkfekm.AO-J1OziSaRStKMr6GaGXyZWddwBnzWFqIGZtDQcV5t8Hw25989tQ9MeTTK6uURqkA2GJJNJiDBMUGpuTW2ZUoRqu-W3dz_E0KwS9gtosbuGGrXpori3GSs"
//	}]
	private String orderId;
	private String packageName;
	private String productId;
	private String purchaseTime;
	private String purchaseState;
	private String purchaseToken;
	private String consumptionState;
	private String developerPayload;
	private String playerGuid;
	private CashierTransaction cashierTransaction;
}
