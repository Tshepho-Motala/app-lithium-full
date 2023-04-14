package lithium.service.user.provider.vipps.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
@Table(indexes = {@Index(name="idx_uat_requestid", columnList="requestId", unique=true)})
public class CallbackRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	private String requestId;
	
	@Enumerated(EnumType.STRING)
	private Status status;
	private String token;
	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "user_details_id")
	private UserDetails userDetails;
	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "error_info_id")
	private ErrorInfo errorInfo;
	
	public boolean success() {
		if (Status.SUCCESS.equals(status)) return true;
		return false;
	}
	public boolean pendingRemoval() {
		if (Status.PENDING_REMOVAL.equals(status)) return true;
		return false;
	}
	public boolean isNew() {
		if (Status.NEW.equals(status)) return true;
		return false;
	}
	
	@AllArgsConstructor(access=AccessLevel.PRIVATE)
	public enum Status implements Serializable {
		NEW("NEW"),
		SUCCESS("SUCCESS"),
		FAILURE("FAILURE"),
		PENDING("PENDING"),
		PENDING_REMOVAL("PENDING_REMOVAL"),
		DECLINED("DECLINED"),
		REMOVED("REMOVED");
		
		@Getter
		@Accessors(fluent = true)
		private String status;
	}
}
