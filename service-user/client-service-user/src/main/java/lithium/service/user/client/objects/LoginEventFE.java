package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginEventFE implements Serializable {
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
    private String duration;

    public String getSuccessful() {
        if (Boolean.valueOf(successful)) {
            return "success";
        } else {
            return "fail";
        }
    }


    public String getDuration() {
        if (duration == null) return "";

        long timeInMilliSec = Long.valueOf(duration);
        long timeInSec = Math.round(timeInMilliSec / 1000);
        Integer hours = (int) Math.floor(timeInSec / 3600);
        Integer minutes = (int)Math.floor(timeInSec /60 - hours*60);
        Integer seconds = (int)Math.floor(timeInSec - hours*3600 - minutes*60);

        String hoursStr = hours.toString();
        String minutesStr = minutes.toString();
        String secondsStr = seconds.toString();

        if (hoursStr.length()<2) {
            hoursStr = "0" + hoursStr;
        }
        if (minutesStr.length()<2) {
            minutesStr = "0" + minutesStr;
        }
        if (secondsStr.length()<2) {
            secondsStr = "0" + secondsStr;
        }
        return hoursStr + " : " + minutesStr + " : "+ secondsStr;
    }
}
