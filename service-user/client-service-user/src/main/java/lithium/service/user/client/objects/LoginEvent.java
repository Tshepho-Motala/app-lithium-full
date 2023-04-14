package lithium.service.user.client.objects;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString(exclude = {"user"})
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class LoginEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private Date date;
	private String ipAddress;
	private String country;
	private String countryCode;
	private String state;
	private String city;
	private String os;
	private String browser;
	private String comment;
	private String userAgent;
	@JsonBackReference
	private User user;
	private Domain domain;
	private Boolean successful;
	private Date logout;
	private Long duration;
	private String sessionKey;
	private Date lastActivity;
}



