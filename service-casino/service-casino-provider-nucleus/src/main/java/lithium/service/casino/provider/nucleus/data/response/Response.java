package lithium.service.casino.provider.nucleus.data.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.ToString;

@ToString
@XmlTransient
public class Response {
		public static final String RESPONSE_SUCCESS = "OK";
		public static final String RESPONSE_CODE_INTERNAL_ERROR = "399";
		public static final String RESPONSE_CODE_INVALID_HASH = "500";
		public static final String RESPONSE_CODE_INSUFFICIENT_FUNDS = "300";
		public static final String RESPONSE_CODE_OPERATION_FAILED = "301";
		public static final String RESPONSE_CODE_UNKNOWN_TRANSACTIONID = "302";
		public static final String RESPONSE_CODE_UNKNOWN_USERID = "310";
		public static final String RESPONSE_CODE_INVALID_TOKEN = "400";
		public static final String RESPONSE_CODE_INVALID_BONUS_TYPE = "601";
		public static final String RESPONSE_CODE_INVALID_AMOUNT = "602";
		public static final String RESPONSE_CODE_INVALID_MULTIPLIER = "603";
		public static final String RESPONSE_CODE_INVALID_GAME_MODES = "604";
		public static final String RESPONSE_CODE_INVALID_GAME_ID = "605";
		public static final String RESPONSE_CODE_INVALID_EXP_DATE = "606";
		public static final String RESPONSE_CODE_INVALID_ROUNDS = "607";
		public static final String RESPONSE_CODE_INVALID_PARAMS = "610";
		public static final String RESPONSE_CODE_FRBW_INVALID_HASH = "620";
		public static final String RESPONSE_CODE_INVALID_OPERATION = "630";
		public static final String RESPONSE_CODE_INVALID_USER = "631";
		public static final String RESPONSE_CODE_INVALID_EMPTY_BONUS_CODE = "640";
		public static final String RESPONSE_CODE_BONUS_CODE_ALREADY_EXISTS = "641";
		public static final String RESPONSE_CODE_INVALID_DURATION = "642";
		public static final String RESPONSE_CODE_INVALID_START_TIME = "643";
		public static final String RESPONSE_CODE_INVALID_EXPIRATION_TIME = "644";
		public static final String RESPONSE_CODE_EXP_TIME_LESS_EQUALS_START_TIME = "645";
		public static final String RESPONSE_CODE_DURATION_GREATER_TIME_PERIOD = "646";
		public static final String RESPONSE_CODE_INVALID_EXPIRATION_HOURS = "647";
		public static final String RESPONSE_CODE_INVALID_TABLE_CHIPS = "648";
		public static final String RESPONSE_CODE_FRBW_INTERNAL_ERROR = "699";
		
		@XmlElement(name = "RESULT")
		private String result;
		@XmlElement(name = "CODE")
		private String code;
		@XmlElement(name = "DESCRIPTION")
		private String description;

		public Response() {
			this.result = "OK";
			this.code = null;
		};
		
		public Response(String code) {
			this.result = "ERROR";
			this.code = code;
		}

		public Response(String code, String result) {
			this.code = code;
			this.result = result;
		}
		
		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
		
		public String getDescription() {
			return description;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
}