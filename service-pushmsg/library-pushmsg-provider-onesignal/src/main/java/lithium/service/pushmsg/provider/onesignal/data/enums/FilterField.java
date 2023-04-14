package lithium.service.pushmsg.provider.onesignal.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FilterField {
	LAST_SESSION("last_session"), //relation = ">" or "<" hours_ago = number of hours before or after the users last session. Example: "1.1"
	FIRST_SESSION("first_session"), //relation = ">" or "<" hours_ago = number of hours before or after the users first session. Example: "1.1"
	SESSION_COUNT("session_count"), //relation = ">", "<", "=" or "!=" value = number sessions. Example: "1"
	SESSION_TIME("session_time"), //relation = ">" or "<" value = Time in seconds the user has been in your app. Example: "3600"
	AMOUNT_SPENT("amount_spent"), //relation = ">", "<", or "=" value = Amount in USD a user has spent on IAP (In App Purchases). Example: "0.99"
	BOUGHT_SKU("bought_sku"), //relation = ">", "<" or "=" key = SKU purchased in your app as an IAP (In App Purchases). Example: "com.domain.100coinpack" value = value of SKU to compare to. Example: "0.99"
	TAG("tag"), //relation = ">", "<", "=", "!=", "exists" or "not_exists" key = Tag key to compare. value = Tag value to compare. Not required for "exists" or "not_exists". Example: See Formatting Filters
	LANGUAGE("language"), //relation = "=" or "!=" value = 2 character language code. Example: "en". For a list of all language codes go here 
	APP_VERSION("app_version"), //relation = ">", "<", "=" or "!=" value = app version. Example: "1.0.0"
	LOCATION("location"), //radius = in meters lat = latitude long = longitude
	EMAIL("email"), //value = email address For email targeting only, not used to send push notifications
	COUNTRY("country"); //relation = "=" value = 2-digit Country code Example: "field": "country", "relation": "=", "value", "US"
	
	@Getter
	@Setter
	@Accessors(fluent = true)
	private String field;
	
	@JsonCreator
	public static FilterField fromFilter(String field) {
		for (FilterField f : FilterField.values()) {
			if (f.field.equalsIgnoreCase(field)) {
				return f;
			}
		}
		return null;
	}
}