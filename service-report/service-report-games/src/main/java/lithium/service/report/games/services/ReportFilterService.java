package lithium.service.report.games.services;

import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
public class ReportFilterService {
	public static final String FIELD_GAME_CASINO_BET_AMOUNT_CENTS = "gameCasinoBetAmountCents";
	public static final String FIELD_GAME_CASINO_BET_COUNT = "gameCasinoBetCount";
	public static final String FIELD_GAME_CASINO_WIN_AMOUNT_CENTS = "gameCasinoWinAmountCents";
	public static final String FIELD_GAME_CASINO_WIN_COUNT = "gameCasinoWinCount";
	public static final String FIELD_GAME_CASINO_NET_AMOUNT_CENTS = "gameCasinoNetAmountCents";
	public static final String FIELD_GAME_CASINO_BONUS_BET_AMOUNT_CENTS = "gameCasinoBonusBetAmountCents";
	public static final String FIELD_GAME_CASINO_BONUS_BET_COUNT = "gameCasinoBonusBetCount";
	public static final String FIELD_GAME_CASINO_BONUS_WIN_AMOUNT_CENTS = "gameCasinoBonusWinAmountCents";
	public static final String FIELD_GAME_CASINO_BONUS_WIN_COUNT = "gameCasinoBonusWinCount";
	public static final String FIELD_GAME_CASINO_BONUS_NET_AMOUNT_CENTS = "gameCasinoBonusNetAmountCents";
	
	public static final String OPERATOR_EQUAL_TO = "equalTo";
	public static final String OPERATOR_LESS_THAN = "lessThan";
	public static final String OPERATOR_GREATER_THAN = "greaterThan";
	
	public static final String USER_STATUS_ENABLED = "enabled";
	
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