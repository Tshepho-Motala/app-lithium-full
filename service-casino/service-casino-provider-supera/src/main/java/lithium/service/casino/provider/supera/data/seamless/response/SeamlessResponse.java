package lithium.service.casino.provider.supera.data.seamless.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SeamlessResponse {
	public static final int HTTP_STATUS_OK = 200;
	public static final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;
	public static final int HTTP_STATUS_UNAUTHORIZED = 403;
	
	@JsonProperty
	private Integer status;
	@JsonProperty
	private BigDecimal balance;
	@JsonProperty("msg")
	private String message;
}