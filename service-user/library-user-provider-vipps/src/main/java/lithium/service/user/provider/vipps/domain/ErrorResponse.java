package lithium.service.user.provider.vipps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ErrorResponse {
	String error; //  "error": "unauthorized_client",
	@JsonProperty("error_description")
	String errorDescription; //  "error_description": "AADSTS70001: Application with identifier 'e9b6c99d-2442-4a5d-84a2-c53a807fe0c4' was not found in the directory testapivipps.no\r\nTrace ID: 3bc2b2a0-d9bb-4c2e-8367-5633866f1300\r\nCorrelation ID: bb2f4093-70af-446a-a26d-ed8becca1a1a\r\nTimestamp: 2017-05-19 09:21:28Z",
	@JsonProperty("error_codes")
	String[] errorCodes;//  "error_codes": [
	//    70001
	//  ],
	String timestamp; //  "timestamp": "2017-05-19 09:21:28Z",
	@JsonProperty("trace_id")
	String traceId; //  "trace_id": "3bc2b2a0-d9bb-4c2e-8367-5633866f1300",
	@JsonProperty("correlation_id")
	String correlationId; //  "correlation_id": "bb2f4093-70af-446a-a26d-ed8becca1a1a"
}
