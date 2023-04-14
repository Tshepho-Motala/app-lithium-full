package lithium.service.affiliate.services;

import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
public class ReportFilterService {
	public static final String FIELD_PLAYER_BIRTHDAY = "playerBirthday";
	public static final String FIELD_PLAYER_DEPOSIT_COUNT = "playerDepositCount";
	public static final String FIELD_PLAYER_LAST_LOGIN_DATE = "playerLastLoginDate";
	public static final String FIELD_PLAYER_CASINO_BET_AMOUNT_CENTS = "playerCasinoBetAmountCents";
	public static final String FIELD_PLAYER_CREATED_DATE = "playerCreatedDate";
	public static final String FIELD_PLAYER_DEPOSIT_AMOUNT_CENTS = "playerDepositAmountCents";
	public static final String FIELD_PLAYER_STATUS = "playerStatus";
	
	public static final String OPERATOR_EQUAL_TO = "equalTo";
	public static final String OPERATOR_LESS_THAN = "lessThan";
	public static final String OPERATOR_GREATER_THAN = "greaterThan";
	
	public static final String USER_STATUS_ENABLED = "enabled";
	
	// equalTo
	public boolean filter(String fOperator, String fValue, String value) {
		switch (fOperator) {
			case OPERATOR_EQUAL_TO: if (value.equalsIgnoreCase(fValue)) return true; break;
			default: throw new IllegalArgumentException("Illegal filter operation for filter type String");
		}
		return false;
	}
	
	// equalTo, lessThan, greaterThan
	public boolean filter(String fOperator, String fValue, Integer value) throws Exception {
		Integer fValueInteger = null;
		try {
			fValueInteger = Integer.parseInt(fValue);
		} catch (NumberFormatException e) {
			throw new Exception("Could not convert String fValue to Integer");
		}
		switch (fOperator) {
			case OPERATOR_EQUAL_TO: if (value.compareTo(fValueInteger) == 0) return true; break;
			case OPERATOR_LESS_THAN: if (value.compareTo(fValueInteger) < 0) return true; break;
			case OPERATOR_GREATER_THAN: if (value.compareTo(fValueInteger) > 0) return true; break;
			default: throw new IllegalArgumentException("Illegal filter operation for filter type Integer");
		}
		return false;
	}
	
	// equalTo, lessThan, greaterThan
	public boolean filter(String fOperator, String fValue, Long value) throws Exception {
		Long fValueLong = null;
		try {
			fValueLong = Long.parseLong(fValue);
		} catch (NumberFormatException e) {
			throw new Exception("Could not convert String fValue to Long");
		}
		switch (fOperator) {
			case OPERATOR_EQUAL_TO: if (value.compareTo(fValueLong) == 0) return true; break;
			case OPERATOR_LESS_THAN: if (value.compareTo(fValueLong) < 0) return true; break;
			case OPERATOR_GREATER_THAN: if (value.compareTo(fValueLong) > 0) return true; break;
			default: throw new IllegalArgumentException("Illegal filter operation for filter type Long");
		}
		return false;
	}
	
	// equalTo, lessThan, greaterThan x days ago
	public boolean filter(String fOperator, String fValue, Date value) throws Exception {
		DateTime valueDt = new DateTime(value);
		Integer xDaysAgoInt = null;
		try {
			xDaysAgoInt = Integer.parseInt(fValue);
		} catch (NumberFormatException e) {
			throw new Exception("Could not convert String fValue to Integer");
		}
		DateTime xDaysAgoDt = new DateTime();
		if (xDaysAgoInt.intValue() > 0)
			xDaysAgoDt = xDaysAgoDt.minusDays(xDaysAgoInt);
		switch (fOperator) {
			case OPERATOR_EQUAL_TO:
				if (valueDt.withTimeAtStartOfDay().isEqual(xDaysAgoDt.withTimeAtStartOfDay()))
					return true;
				break;
			case OPERATOR_LESS_THAN:
				if (valueDt.isBefore(xDaysAgoDt))
					return true;
				break;
			case OPERATOR_GREATER_THAN:
				if (valueDt.isAfter(xDaysAgoDt))
					return true;
				break;
			default: throw new IllegalArgumentException("Illegal filter operation for filter type Date");
		}
		return false;
	}
}
