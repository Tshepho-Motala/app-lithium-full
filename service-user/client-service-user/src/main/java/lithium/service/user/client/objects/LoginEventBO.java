package lithium.service.user.client.objects;

import lombok.Data;
import org.apache.commons.lang.BooleanUtils;

import java.sql.Timestamp;

@Data
public class LoginEventBO {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Timestamp date;
    private String ipAddress;
    private String country;
    private String countryCode;
    private String state;
    private String city;
    private String os;
    private String browser;
    private String comment;
    private String userAgent;
    private User user;
    private String successful;
    private Boolean internal;
    private Domain domain;
    private String providerAuthClient;
    private String providerName;
    private String providerUrl;
    private Boolean playerEvent;
    private Integer errorCode;
    private String sessionKey;
    private Timestamp logout;
    private Long duration;
}
