package lithium.service.cashier.mock.btc.clearcollect.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManualCallbackRequest {
	private String url;
	private String referenceNr;
	private String confirmations;
	private String amountUsdCents;
	private String status;
}