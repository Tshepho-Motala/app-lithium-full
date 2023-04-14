package lithium.service.cashier.processor.cc.qwipi.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import lithium.service.cashier.processor.cc.qwipi.data.enums.ErrorCode;
import lithium.service.cashier.processor.cc.qwipi.data.enums.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder(builderMethodName="builderS2S")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Slf4j
public class PaymentRequestS2S {
	
	private String resType = "JSON";
	private String merNo;
	private String cardNum;
	private String cvv2;
	private String month;
	private String year;
	private String cardHolderIp;
	private String dateTime;
	private String billNo;
	private String currency;
	private String amount;
	private String language;
	private String md5Info;
	private String firstName;
	private String middleName;
	private String lastName;
	private String dob;
	private String ssn;
	private String email;
	private String phone;
	private String zipCode;
	private String address;
	private String city;
	private String state;
	private String country; 
	private String userAgent;
	
	public String calculateMd5Info(String md5Key) {
		HashCalculator calculator = new HashCalculator(md5Key);
		calculator.addItem(merNo).addItem(billNo).addItem(currency).addItem(amount).addItem(dateTime);
		return calculator.calculateHash();
	}
	
	public PaymentRequestS2S saveMd5Info(String md5Key) {
		this.md5Info = calculateMd5Info(md5Key);
		return this;
	}
	
	public void validate(String md5Key) throws ValidationException {
		
		if ((resType != null) && (ResponseType.valueOf(resType) != ResponseType.JSON))  throw new ValidationException(ErrorCode.I0000002);
		
		if ((merNo == null) || (merNo.isEmpty())) throw new ValidationException(ErrorCode.E1001320);
		if ((cardNum == null) || (cardNum.isEmpty())) throw new ValidationException(ErrorCode.E1001800);
		if ((cvv2 == null) || (cvv2.isEmpty())) throw new ValidationException(ErrorCode.E1000410);
		if ((month == null) || (month.isEmpty())) throw new ValidationException(ErrorCode.E1000430);
		if ((year == null) || (year.isEmpty())) throw new ValidationException(ErrorCode.E1000450);
		if ((cardHolderIp == null) || (cardHolderIp.isEmpty())) throw new ValidationException(ErrorCode.E1000200);
		if ((dateTime == null) || (dateTime.isEmpty())) throw new ValidationException(ErrorCode.E1000270);
		if ((billNo == null) || (billNo.isEmpty())) throw new ValidationException(ErrorCode.E1000240);
		if ((currency == null) || (currency.isEmpty())) throw new ValidationException(ErrorCode.E1000300);
		if ((amount == null) || (amount.isEmpty())) throw new ValidationException(ErrorCode.E1000320);
		if ((language == null) || (language.isEmpty())) throw new ValidationException(ErrorCode.E1000220);
		if ((firstName == null) || (firstName.isEmpty())) throw new ValidationException(ErrorCode.E1000570);
		if ((lastName == null) || (lastName.isEmpty())) throw new ValidationException(ErrorCode.E1000610);
		if ((dob == null) || (dob.isEmpty())) throw new ValidationException(ErrorCode.E1000950);
		if ((email == null) || (email.isEmpty())) throw new ValidationException(ErrorCode.E1000680);
		if ((phone == null) || (phone.isEmpty())) throw new ValidationException(ErrorCode.E1000700);
		if ((zipCode == null) || (zipCode.isEmpty())) throw new ValidationException(ErrorCode.E1000740);
		if ((address == null) || (address.isEmpty())) throw new ValidationException(ErrorCode.E1000790);
		if ((city == null) || (city.isEmpty())) throw new ValidationException(ErrorCode.E1000820);
		if ((state == null) || (state.isEmpty())) throw new ValidationException(ErrorCode.E1000860);
		if ((country == null) || (country.isEmpty())) throw new ValidationException(ErrorCode.E1000920);
		if ((userAgent == null) || (userAgent.isEmpty())) throw new ValidationException(ErrorCode.I0000001);
		
		if (cardHolderIp.length() > 34) throw new ValidationException(ErrorCode.E1000210);
		if (language.length() > 3) throw new ValidationException(ErrorCode.E1000230);
		if (billNo.length() > 40) throw new ValidationException(ErrorCode.E1000250);
		if (dateTime.length() > 14) throw new ValidationException(ErrorCode.E1000280);
		if (dateTime.length() < 14) throw new ValidationException(ErrorCode.E1000290);

		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			LocalDateTime local = LocalDateTime.parse(dateTime, dtf);
			if (!dateTime.equals(dtf.format(local))) throw new ValidationException(ErrorCode.E1000290);
		} catch (DateTimeParseException e) {
			log.error(e.toString(), e);
			throw new ValidationException(ErrorCode.E1000290, e);
		}
		
		try {
			Double d = Double.parseDouble(amount);
			if (d < 0) throw new ValidationException(ErrorCode.E1000330);
		} catch (NumberFormatException e) {
			throw new ValidationException(ErrorCode.E1000340, e);
		}
		
		if (cardNum.length() > 16) throw new ValidationException(ErrorCode.E1000400);
		if (!Pattern.compile("[0-9]{16,16}").matcher(cardNum).matches()) throw new ValidationException(ErrorCode.E1001970);
		if (cvv2.length() > 4) throw new ValidationException(ErrorCode.E1000420);
		if (month.length() > 2) throw new ValidationException(ErrorCode.E1000440);
		if (year.length() > 4) throw new ValidationException(ErrorCode.E1000460);
		if (firstName.length() > 100) throw new ValidationException(ErrorCode.E1000580);
		if (Pattern.compile("[!@#\\$%^&*\\(\\)\\\\\\-\\+\\{\\}\"'\\.,\\?]+").matcher(firstName).matches()) throw new ValidationException(ErrorCode.E1000600);
		if (lastName.length() > 100) throw new ValidationException(ErrorCode.E1000620);
		if (Pattern.compile("[!@#\\$%^&*\\(\\)\\\\\\-\\+\\{\\}\"'\\.,\\?]+").matcher(lastName).matches()) throw new ValidationException(ErrorCode.E1000640);
		if (firstName.equalsIgnoreCase(lastName)) throw new ValidationException(ErrorCode.E1000650);
		if (middleName != null) {
			if (middleName.length() > 100) throw new ValidationException(ErrorCode.E1000660);
			if (Pattern.compile("[!@#\\$%^&*\\(\\)\\\\\\-\\+\\{\\}\"'\\.,\\?]+").matcher(middleName).matches()) throw new ValidationException(ErrorCode.E1000670);
		}
		if (email.length() > 150) throw new ValidationException(ErrorCode.E1000690);
		if (phone.length() > 50) throw new ValidationException(ErrorCode.E1000710);
		if (Pattern.compile("[^0-9]+").matcher(phone).matches()) throw new ValidationException(ErrorCode.E1000720);
		if (zipCode.length() > 100) throw new ValidationException(ErrorCode.E1000750);
		if (country.equals("US") && (zipCode.length() != 5)) throw new ValidationException(ErrorCode.E1000760);
		if (Pattern.compile("[^0-9]+").matcher(zipCode).matches()) throw new ValidationException(ErrorCode.E1000770);
		if (address.length() > 400) throw new ValidationException(ErrorCode.E1000800);
		if (city.length() > 100) throw new ValidationException(ErrorCode.E1000830);
		if (Pattern.compile("[0-9!@#\\$%^&*\\(\\)\\\\\\-\\+\\{\\}\"'\\.,\\?]+").matcher(city).matches()) throw new ValidationException(ErrorCode.E1000850);
		if (country.equals("US") && (state.length() != 2)) throw new ValidationException(ErrorCode.E1000890);
		if (Pattern.compile("[0-9!@#\\$%^&*\\(\\)\\\\\\-\\+\\{\\}\"'\\.,\\?]+").matcher(state).matches()) throw new ValidationException(ErrorCode.E1000900);
		if (country.length() > 100) throw new ValidationException(ErrorCode.E1000930);

		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
			LocalDate dobDate = LocalDate.parse(dob, dtf);
			if (!dob.equals(dtf.format(dobDate))) throw new ValidationException(ErrorCode.E1000980);
			if (Period.between(dobDate, LocalDate.now()).getYears() < 18) throw new ValidationException(ErrorCode.E1000960);
			if (Period.between(dobDate, LocalDate.now()).getYears() > 113) throw new ValidationException(ErrorCode.E1000970);
		} catch (DateTimeParseException e) {
			throw new ValidationException(ErrorCode.E1000980, e);
		}
		
//		if ((ssn != null) && (ssn.length() < 3)) throw new ValidationException(ErrorCode.E1001000);
		if ((md5Info == null) || (md5Info.isEmpty())) throw new ValidationException(ErrorCode.E1000170);
		if (md5Info.length() > 32) throw new ValidationException(ErrorCode.E1000180);
		if (!md5Info.equals(calculateMd5Info(md5Key))) throw new ValidationException(ErrorCode.E1000190);
		
//		E1000310(1000310, "Wrong currency"),
//		E1000910(1000910, "State does not meet the country "),

//		private String middleName;
//		private String ssn;
		
		
	}

}
