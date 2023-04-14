package lithium.service.pushmsg.provider.onesignal.data;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class EditDevice {
	@JsonProperty("id")
	private String uuid;
	@JsonProperty("app_id")
	private String appId; //REQUIRED Your OneSignal App Id found in Keys & IDs
	@JsonProperty("identifier")
	private String identifier; //Push notification identifier from Google or Apple. For Apple push identifiers, you must strip all non alphanumeric characters. Example: ce777617da7f548fe7a9ab6febb56
	@JsonProperty("language")
	private String language; //Language code. Typically lower case two letters, except for Chinese where it must be one of zh-Hans or zh-Hant. Example: en
	@JsonProperty("timezone")
	private Integer timezone; //Number of seconds away from UTC. Example: -28800
	@JsonProperty("game_version")
	private String gameVersion; //Version of your app. Example: 1.1
	@JsonProperty("device_model")
	private String deviceModel; // Device make and model. Example: iPhone5,1
	@JsonProperty("device_os")
	private String deviceOs; // Device operating system version. Example: 7.0.4
	@JsonProperty("ad_id")
	private String adId; // The ad id for the device's platform:
	@JsonProperty("sdk")
	private String sdk; //Name and version of the sdk/plugin that's calling this API method (if any)
	@JsonProperty("session_count")
	private Integer sessionCount; //Number of times the user has played the game, defaults to 1
	@JsonProperty("tags")
	private Map<String, String> tags; // Custom tags for the player. Only support string key value pairs. Does not support arrays or other nested objects. Example: {"foo":"bar","this":"that"}
	@JsonProperty("amount_spent")
	private String amountSpent; //Amount the user has spent in USD, up to two decimal places
	@JsonProperty("created_at")
	private Integer createdAt; // Unixtime when the player joined the game
	@JsonProperty("playtime")
	private Integer playtime; // Seconds player was running your app.
	@JsonProperty("badge_count")
	private Integer badgeCount; //Current iOS badge count displayed on the app icon
	@JsonProperty("last_active")
	private Integer lastActive; // Unixtime when the player was last active
	@JsonProperty("notification_types")
	private Integer notificationTypes; // 1 = subscribed / -2 = unsubscribed
	@JsonProperty("test_type")
	private Integer testType; // This is used in deciding whether to use your iOS Sandbox or Production push certificate when sending a push when both have been uploaded. Set to the iOS provisioning profile that was used to build your app. 1 = Development 2 = Ad-Hoc Omit this field for App Store builds.
	@JsonProperty("long")
	private Double longitude; //Longitude of the device, used for geotagging to segment on.
	@JsonProperty("lat")
	private Double latitude; // Latitude of the device, used for geotagging to segment on.
	@JsonProperty("country")
	private String country; //Country code in the ISO 3166-1 Alpha 2 format
	@JsonProperty("external_user_id")
	private String externalUserId; //A custom user ID
}