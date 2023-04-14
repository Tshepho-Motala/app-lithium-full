package lithium.service.user.provider.vipps.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lithium.service.user.provider.vipps.domain.CallbackRequest.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class LoginDetailsResponse {
	private String requestId;
	private Status status;
	private UserDetails userDetails;
	private List<ErrorDetails> errorDetails;
	
	public void addErrorDetail(String code, String message) {
		if (errorDetails==null) errorDetails = new ArrayList<>();
		errorDetails.add(new ErrorDetails(code, message));
	}
	
	public boolean hasErrors() {
		if (errorDetails!=null) return true;
		return false;
	}
	
	public String firstErrorCode() {
		if ((errorDetails!=null)&&(errorDetails.size()>0)) return errorDetails.get(0).getErrorCode();
		return "";
	}
	public String firstErrorMessage() {
		if ((errorDetails!=null)&&(errorDetails.size()>0)) return errorDetails.get(0).getErrorMessage();
		return "";
	}
}
