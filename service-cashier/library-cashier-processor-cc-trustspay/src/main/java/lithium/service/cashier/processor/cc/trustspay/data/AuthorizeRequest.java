package lithium.service.cashier.processor.cc.trustspay.data;

import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.joda.time.DateTime;

import lithium.service.cashier.processor.cc.trustspay.data.enums.ErrorCode;
import lithium.service.cashier.processor.cc.trustspay.util.HashCalculator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder(builderMethodName="builderS2S")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AuthorizeRequest {
	
	private String merNo;
	private String gatewayNo;
	private String orderNo;
	private String orderCurrency;
	private String orderAmount;
	private String cardNo;
	private String cardSecurityCode;
	private String cardExpireMonth;
	private String cardExpireYear;
	private String issuingBank;
	private String firstName;
	private String lastName;
	private String email;
	private String ip;
	private String phone;
	private String country; 
	private String state;
	private String city;
	private String address;
	private String zip;
	private String signInfo;
	private String remark;
	private String returnUrl;
	private String csid;
		
	public String calculateSignInfo(String key) {
		HashCalculator calculator = new HashCalculator(key);
		calculator.addItem(merNo).addItem(gatewayNo).addItem(orderNo).addItem(orderCurrency).addItem(orderAmount).addItem(firstName)
			.addItem(lastName).addItem(cardNo).addItem(cardExpireYear).addItem(cardExpireMonth).addItem(cardSecurityCode).addItem(email);
		return calculator.calculateHash();
	}
	
	public AuthorizeRequest saveSignInfo(String key) {
		this.signInfo = calculateSignInfo(key);
		return this;
	}
	
	public void validate(String key) throws ValidationException {
		
		if ((merNo == null) || (merNo.length() == 0)) throw new ValidationException(ErrorCode.I0001);
		if ((gatewayNo == null) || (gatewayNo.length() == 0)) throw new ValidationException(ErrorCode.I0002);
		if ((orderNo == null) || (orderNo.length() == 0)) throw new ValidationException(ErrorCode.I0017);
		if (orderNo.length() > 50) throw new ValidationException(ErrorCode.I0018);
		if ((orderAmount == null) || (orderAmount.length() == 0)) throw new ValidationException(ErrorCode.I0019);
		if ((orderCurrency == null) || (orderCurrency.length() == 0)) throw new ValidationException(ErrorCode.I0022);
		if ((returnUrl == null) || (returnUrl.length() == 0)) throw new ValidationException(ErrorCode.I0024);
		if (returnUrl.length() > 1000) throw new ValidationException(ErrorCode.I0025);
		if ((cardNo == null) || (cardNo.length() == 0)) throw new ValidationException(ErrorCode.I0026);
		if ((cardNo.length() != 13) && (cardNo.length() != 16)) throw new ValidationException(ErrorCode.I0027);
		if (Pattern.compile("[^0-9]+").matcher(cardNo).matches()) throw new ValidationException(ErrorCode.I0028);
		if (!cardNo.startsWith("4") && !cardNo.startsWith("5")) throw new ValidationException(ErrorCode.I0029); 
		if ((cardExpireMonth == null) || (cardExpireMonth.length() == 0)) throw new ValidationException(ErrorCode.I0031);
		if (cardExpireMonth.length() != 2) throw new ValidationException(ErrorCode.I0032);
		if (Pattern.compile("[^0-9]+").matcher(cardExpireMonth).matches()) throw new ValidationException(ErrorCode.I0033);
		if ((cardExpireYear == null) || (cardExpireYear.length() == 0)) throw new ValidationException(ErrorCode.I0035);
		if (cardExpireYear.length() != 4) throw new ValidationException(ErrorCode.I0036);
		if (Pattern.compile("[^0-9]+").matcher(cardExpireYear).matches()) throw new ValidationException(ErrorCode.I0037);
		if ((cardSecurityCode == null) || (cardSecurityCode.length() == 0)) throw new ValidationException(ErrorCode.I0039);
		if (cardSecurityCode.length() != 3) throw new ValidationException(ErrorCode.I0040);
		if (Pattern.compile("[^0-9]+").matcher(cardSecurityCode).matches()) throw new ValidationException(ErrorCode.I0041);
		if ((issuingBank == null) || (issuingBank.length() == 0)) throw new ValidationException(ErrorCode.I0042);
		if (issuingBank.length() < 2 || issuingBank.length() > 50) throw new ValidationException(ErrorCode.I0043);
		if ((firstName == null) || (firstName.length() == 0)) throw new ValidationException(ErrorCode.I0044);
		if (firstName.length() < 2 || firstName.length() > 50) throw new ValidationException(ErrorCode.I0045);
		if ((lastName == null) || (lastName.length() == 0)) throw new ValidationException(ErrorCode.I0046);
		if (lastName.length() < 2 || lastName.length() > 50) throw new ValidationException(ErrorCode.I0047);
		if ((email == null) || (email.length() == 0)) throw new ValidationException(ErrorCode.I0048);
		if (email.length() < 2 || email.length() > 100) throw new ValidationException(ErrorCode.I0049);
		if ((phone == null) || (phone.length() == 0)) throw new ValidationException(ErrorCode.I0051);
		if (phone.length() < 2 || phone.length() > 50) throw new ValidationException(ErrorCode.I0052);
		if ((country == null) || (country.length() == 0)) throw new ValidationException(ErrorCode.I0053);
		if (country.length() < 2 || country.length() > 50) throw new ValidationException(ErrorCode.I0054);
		if ((address == null) || (address.length() == 0)) throw new ValidationException(ErrorCode.I0055);
		if (address.length() < 2 || address.length() > 100) throw new ValidationException(ErrorCode.I0056);
		if ((zip == null) || (zip.length() == 0)) throw new ValidationException(ErrorCode.I0057);
		if (zip.length() < 2 || zip.length() > 50) throw new ValidationException(ErrorCode.I0058);
		if ((state == null) || (state.length() == 0)) throw new ValidationException(ErrorCode.I0063);
		if (state.length() < 2 || state.length() > 50) throw new ValidationException(ErrorCode.I0064);
		if ((city == null) || (city.length() == 0)) throw new ValidationException(ErrorCode.I0065);
		if (city.length() < 2 || city.length() > 100) throw new ValidationException(ErrorCode.I0066);
		
		try {
			new InternetAddress(email).validate();
		} catch (AddressException ae) {
			throw new ValidationException(ErrorCode.I0050, ae);
		}

		try {
			Integer d = Integer.parseInt(cardExpireYear);
			int currentYear = DateTime.now().getYear();
			if ((d < currentYear) || (d > currentYear + 10)) throw new ValidationException(ErrorCode.I0038);
		} catch (NumberFormatException e) {
			throw new ValidationException(ErrorCode.I0034, e);
		}
		
		try {
			Integer d = Integer.parseInt(cardExpireMonth);
			if ((d < 1) || (d > 12)) throw new ValidationException(ErrorCode.I0034);
		} catch (NumberFormatException e) {
			throw new ValidationException(ErrorCode.I0034, e);
		}
		
		try {
			Double d = Double.parseDouble(orderAmount);
			if (d < 0) throw new ValidationException(ErrorCode.I0020);
		} catch (NumberFormatException e) {
			throw new ValidationException(ErrorCode.I0020, e);
		}

		if (!signInfo.equals(calculateSignInfo(key))) throw new ValidationException(ErrorCode.I0013);
		
	}

}
