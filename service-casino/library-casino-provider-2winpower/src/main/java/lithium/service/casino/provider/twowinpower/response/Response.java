package lithium.service.casino.provider.twowinpower.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Response {
	private String balance;
	@JsonProperty("transaction_id")
	private String transactionId;
	
	@JsonProperty("error_code")
	private String errorCode;
	@JsonProperty("error_description")
	private String errorDescription;
	
	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
	@AllArgsConstructor(access=AccessLevel.PRIVATE)
	public enum ErrorCode {
		INSUFFICIENT_FUNDS ("INSUFFICIENT_FUNDS"),
		INTERNAL_ERROR ("INTERNAL_ERROR");	
		
		@Setter
		@Getter
		@Accessors(fluent = true)
		private String value;
		
		public String toString() {
			return value;
		}
	}
}