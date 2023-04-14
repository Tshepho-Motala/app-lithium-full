package lithium.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {
	private static final long serialVersionUID = 1882067957239979192L;
	
	private T data = null;
	private Object data2 = null;
	private String message;
	private Status status = Status.OK;
	
	public Response(T data) {
		this.data = data;
	}
	
	public Response(Status status, String message) {
		this.status = status;
		this.message = message;
	}


	public boolean isSuccessful() {
		if (getStatus() == Status.OK || getStatus() == Status.OK_SUCCESS) {
			return true;
		}
		return false;
	}

	public Status getStatus() {
		if (status == null) return Status.OK;
		return status;
	}

	@ToString
	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
	@AllArgsConstructor(access=AccessLevel.PRIVATE)
	public enum Status {
		OK(0, "OK"),
		EXISTS(1, "EXISTS"),
		BAD_REQUEST(400, "BAD_REQUEST"),
		UNAUTHORIZED(401, "UNAUTHORIZED"),
		FORBIDDEN(403, "FORBIDDEN"),
		NOT_FOUND(404, "NOT_FOUND"),
		DISABLED(405, "DISABLED"),
		IP_ADDRESS_BLOCKED(407, "IP_ADDRESS_BLOCKED"),
		CONFLICT(409, "CONFLICT"),
		LOGIN_RESTRICTED(460, "LOGIN_RESTRICTED"),
		INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR"),
		NOT_IMPLEMENTED(501, "NOT_IMPLEMENTED"),
		SERVICE_UNAVAILABLE(503, "SERVICE_UNAVAILABLE"),
		INVALID_DATA(504, "INVALID_DATA"),
		SERVER_TIMEOUT(555, "SERVER_TIMEOUT"),
		OK_SUCCESS(200, "SUCCESS"),
		PENDING (202, "PENDING"),
		CUSTOM(null, null),
		FAILED_LOGIN_BLOCK(900, "FAILED_LOGIN_BLOCK"),
		INVALID_DOCUMENT_TYPE(415, "INVALID_DOCUMENT_TYPE"),
		ACCOUNT_UPGRADE_REQUIRED(430, "ACCOUNT_UPGRADE_REQUIRED"),
		MUTUAL_EXCLUSIVE_DOMAIN_EXIST(432, "MUTUAL_EXCLUSIVE_DOMAIN_EXIST"),
		INCOMPLETE_USER_REGISTRATION(463, "INCOMPLETE_USER_REGISTRATION"),
		DOMAIN_UNKNOWN_COUNTRY_EXCEPTION(465, "DOMAIN_UNKNOWN_COUNTRY_EXCEPTION");

		private Integer id;

		public Status id(Integer id) {
			this.id = id;
			return this;
		}

		@Getter
		@Accessors(fluent = true)
		private String message;

		public Status message(String message) {
			this.message = message;
			return this;
		}
		
		@JsonValue
		public Integer id() {
			return id;
		}

		@JsonCreator
		public static Status fromId(int id) {
			for (Status c : Status.values()) {
				if ((c.id != null) && (c.id == id)) {
					return c;
				}
			}
			return CUSTOM.id(id);
		}
	}
}
