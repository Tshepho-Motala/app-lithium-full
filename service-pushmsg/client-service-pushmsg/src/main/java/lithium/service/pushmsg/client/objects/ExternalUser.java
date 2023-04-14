package lithium.service.pushmsg.client.objects;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
@ToString(exclude="user")
@EqualsAndHashCode(exclude="user")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class ExternalUser implements Serializable {
	private static final long serialVersionUID = -7476687568705267941L;

	private long id;
	private String uuid;
	private User user;
	
	private Long sessionCount; // 5,
//	private Language language; //": "en",
	private String deviceOs; //": "72",
	private Integer deviceType; //": 5,
	private String deviceModel; //": "MacIntel",
	private Date lastActive; //": 1551074028,
	private Date createdAt; //": 1550764356,
	private String ip; //: "196.22.242.138",
	
	public String deviceTypeStr() {
		log.info("Type : "+DeviceType.fromId(deviceType).type);
		return DeviceType.fromId(deviceType).type;
	}
	
	@AllArgsConstructor(access=AccessLevel.PRIVATE)
	public enum DeviceType implements Serializable {
		iOS(0, "iOS"),
		ANDROID(1, "ANDROID"),
		AMAZON(2, "AMAZON"),
		WINDOWSPHONE(3,"WINDOWSPHONE"),
		CHROME_APPS(4, "CHROME APPS / EXTENSIONS"),
		CHROME_WEB_PUSH(5, "CHROME WEB PUSH"),
		WINDOWS(6, "WINDOWS"),
		SAFARI(7, "SAFARI"),
		FIREFOX(8, "FIREFOX"),
		MACOS(9, "MACOS"),
		ALEXA(10, "ALEXA"),
		EMAIL(11, "EMAIL");
		
		@Getter
		@Accessors(fluent = true)
		private Integer id;
		@Getter
		@Accessors(fluent = true)
		private String type;
		
		@JsonCreator
		public static DeviceType fromId(int id) {
			for (DeviceType dt:DeviceType.values()) {
				if (dt.id == id) {
					return dt;
				}
			}
			return null;
		}
	}
}