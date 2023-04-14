package lithium.service.cashier.mock.upay.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManualCallbackRequest {
	private String url;
	private String referenceNr;
	private String amountUsdCents;
	private String status;
	private String merno;
	private String key;
}