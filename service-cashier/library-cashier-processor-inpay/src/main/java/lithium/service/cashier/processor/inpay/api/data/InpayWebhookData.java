package lithium.service.cashier.processor.inpay.api.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InpayWebhookData {
	//snake case naming because webhook produces data in x-www-form-urlencoded format
	private String api_version;
	private String bank_owner_name;
	private String checksum;
	private String invoice_amount;
	private String invoice_currency;
	private String invoice_reference;
	private String invoice_status;
	private String invoice_updated_at;
	private String merchant_id;
	private String order_id;
	private String received_sum;
}
