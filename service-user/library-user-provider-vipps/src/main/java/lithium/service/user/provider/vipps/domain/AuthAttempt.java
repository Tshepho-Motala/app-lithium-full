package lithium.service.user.provider.vipps.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lithium.service.user.provider.vipps.domain.CallbackRequest.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@Table(indexes = {@Index(name="idx_uat_requestid", columnList="xRequestId", unique=true)})
public class AuthAttempt {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private DateTime created;
	
	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "callback_request_id")
	private CallbackRequest callbackRequest;
	private DateTime expiresOn;
	private String providedAccessToken;
	private String xRequestId;
	private String url;
	
	public boolean success() {
		return callbackRequest.success();
	}
	public boolean pendingRemoval() {
		return callbackRequest.pendingRemoval();
	}
	public boolean isNew() {
		return callbackRequest.isNew();
	}
	public void setStatus(Status status) {
		callbackRequest.setStatus(status);
	}
	public void setUserId(String userId) {
		if (callbackRequest.getUserDetails() == null) callbackRequest.setUserDetails(UserDetails.builder().userId(userId).build());
		else callbackRequest.getUserDetails().setUserId(userId);
	}
}