package lithium.service.cashier.processor.cc.qwipi.data;

import java.net.MalformedURLException;
import java.net.URL;

import lithium.service.cashier.processor.cc.qwipi.data.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder()
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
public class PaymentRequest3DS extends PaymentRequestS2S {
	private String returnUrl;
	private String bgReturnUrl;
	
	@Override
	public void validate(String md5Key) throws ValidationException {
		
		super.validate(md5Key);
		
		if ((returnUrl == null) || (returnUrl.isEmpty())) throw new ValidationException(ErrorCode.I0000008);
		if ((bgReturnUrl == null) || (bgReturnUrl.isEmpty())) throw new ValidationException(ErrorCode.I0000011);
		
		if (returnUrl.length() > 200) throw new ValidationException(ErrorCode.I0000009);
		if (bgReturnUrl.length() > 200) throw new ValidationException(ErrorCode.I0000012);
		
		try { new URL(returnUrl); } catch (MalformedURLException mfue) { throw new ValidationException(ErrorCode.I0000010, mfue); };
		try { new URL(bgReturnUrl); } catch (MalformedURLException mfue) { throw new ValidationException(ErrorCode.I0000013, mfue); };
		
	}
	
	
}
