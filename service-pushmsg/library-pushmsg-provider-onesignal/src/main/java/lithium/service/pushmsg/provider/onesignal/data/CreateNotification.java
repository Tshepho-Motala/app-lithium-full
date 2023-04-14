package lithium.service.pushmsg.provider.onesignal.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class CreateNotification {
	//Segments
	@JsonProperty(value="included_segments", required=false)
	private List<String> includedSegments;
	@JsonProperty(value="excluded_segments", required=false)
	private List<String> excludedSegments;
	
	//Filters
	@JsonProperty(value="filters", required=false)
	private List<Filter> filters;
	
	//Send to Specific Devices
	/**
	 * Specific players to send your notification to. Does not require API Auth Key.
	 * Do not combine with other targeting parameters. Not compatible with any other targeting parameters. Example: ["1dd608f2-c6a1-11e3-851d-000c2940e62c"]
	 * Limit of 2,000 entries per REST API call
	 */
	@JsonProperty(value="include_player_ids", required=false)
	private List<String> includePlayerIds;
	/**
	 * Target specific email addresses. If an email does not correspond to an existing user, a new user will be created. Example: nick@catfac.ts
	 * Limit of 2,000 entries per REST API call
	 */
	@JsonProperty(value="include_email_tokens", required=false)
	private List<String> includeEmailTokens;
	
	/**
	 * Target specific players by custom user IDs assigned via API. Not compatible with any other targeting parameters
	 * Example: [“custom-id-assigned-by-api”]
	 * Limit of 2,000 entries per REST API call
	 */
	@JsonProperty(value="include_external_user_ids", required=false)
	private List<String> includeExternalUserIds;
	
	//Common Parameters
	/**
	 * Your OneSignal application ID, which can be found in Keys & IDs. It is a UUID and looks similar to 8250eaf6-1a58-489e-b136-7c74a864b434.
	 */
	@JsonProperty(value="app_id", required=true)
	private String appId;
	/**
	 * Correlation and idempotency key.
	 * A request received with this parameter will first look for another notification with the same external_id. 
	 * If one exists, a notification will not be sent, and result of the previous operation will instead be returned. 
	 * Therefore, if you plan on using this feature, it's important to use a good source of randomness to generate the UUID passed here.
	 * This key is only idempotent for 30 days. After 30 days, the notification could be removed from our system and a notification with the same external_id will be sent again.
	 */
	@JsonProperty(value="external_id", required=false)
	private String externalId;
	
	//Notification Content
	
	/**
	 * A custom map of data that is passed back to your app.
	 * 
	 * Example: {"abc": "123", "foo": "bar"}
	 */
	@Default
	@JsonProperty(value="data", required=false)
	private Map<String, String> placeholders = new HashMap<>();
	
	/**
	 * REQUIRED unless content_available=true or template_id is set.
	 * The notification's content (excluding the title), a map of language codes to text for each language.
	 * 
	 * Each hash must have a language code string for a key, mapped to the localized text you would like users to receive for that language.
	 * This field supports inline substitutions.
	 * English must be included in the hash.
	 * 
	 * Example: {"en": "English Message", "es": "Spanish Message"}
	 */
	@JsonProperty(value="contents", required=false)
	private String contents;
	
	/**
	 * The notification's title, a map of language codes to text for each language. 
	 * Each hash must have a language code string for a key, mapped to the localized text you would like users to receive for that language.
	 * This field supports inline substitutions.
	 * 
	 * Example: {"en": "English Title", "es": "Spanish Title"}
	 */
	@JsonProperty(value="headings", required=false)
	private String headings;
	
	/**
	 * The notification's subtitle, a map of language codes to text for each language. 
	 * Each hash must have a language code string for a key, mapped to the localized text you would like users to receive for that language.
	 * This field supports inline substitutions.
	 * 
	 * Example: {"en": "English Subtitle", "es": "Spanish Subtitle"}
	 */
	@JsonProperty(value="subtitle", required=false)
	private String subtitle;
	
	/**
	 * Use a template you setup on our dashboard. You can override the template values by sending other parameters with the request. 
	 * The template_id is the UUID found in the URL when viewing a template on our dashboard.
	 * 
	 * Example: be4a8044-bbd6-11e4-a581-000c2940e62c
	 */
	@JsonProperty(value="template_id", required=false)
	private String templateId;
	
	//Delivery
	/**
	 * Schedule notification for future delivery.
	 * Examples: All examples are the exact same date & time.
	 *   "Thu Sep 24 2015 14:00:00 GMT-0700 (PDT)"
	 *   "September 24th 2015, 2:00:00 pm UTC-07:00"
	 *   "2015-09-24 14:00:00 GMT-0700"
	 *   "Sept 24 2015 14:00:00 GMT-0700"
	 *   "Thu Sep 24 2015 14:00:00 GMT-0700 (Pacific Daylight Time)"
	 */
	@JsonProperty(value="send_after", required=false)
	private String sendAfter;
	
	/**
	 * Possible values are:
	 *   timezone (Deliver at a specific time-of-day in each users own timezone)
	 *   last-active Same as Intelligent Delivery . (Deliver at the same time of day as each user last used your app).
	 *   
	 * If send_after is used, this takes effect after the send_after time has elapsed.
	 */
	@JsonProperty(value="delayed_option", required=false)
	private String delayedOption;
	
	/**
	 * Use with delayed_option=timezone.
	 * Example: "9:00AM"
	 */
	@JsonProperty(value="delivery_time_of_day", required=false)
	private String deliveryTimeOfDay;
	
	/**
	 * Time To Live - In seconds. The notification will be expired if the device does not come back online within this time. The default is 259,200 seconds (3 days).
	 * Max value to set is 2419200 seconds (28 days).
	 */
	@JsonProperty(value="ttl", required=false)
	private String ttl;
	
	/**
	 * Delivery priority through the push server (example GCM/FCM). Pass 10 for high priority. 
	 * Defaults to normal priority for Android and high for iOS. For Android 6.0+ devices setting priority to high will wake the device out of doze mode.
	 */
	@JsonProperty(value="priority", required=false)
	private String priority;
	
	//Platform to Deliver To
	/**
	 * Indicates whether to send to all devices registered under your app's Apple iOS platform.
	 */
	@JsonProperty(value="isIos", required=false)
	private Boolean isIos;
	/**
	 * Indicates whether to send to all devices registered under your app's Google Android platform.
	 */
	@JsonProperty(value="isAndroid", required=false)
	private Boolean isAndroid;
	/**
	 * Indicates whether to send to all subscribed web browser users, including Chrome, Firefox, and Safari.
	 * You may use this instead as a combined flag instead of separately enabling isChromeWeb, isFirefox, and isSafari, though the three options are equivalent to this one.
	 */
	@JsonProperty(value="isAnyWeb", required=false)
	private Boolean isAnyWeb;
	/**
	 * Indicates whether to send to users with email.
	 */
	@JsonProperty(value="isEmail", required=false)
	private Boolean isEmail;
	/**
	 * Indicates whether to send to all Google Chrome, Chrome on Android, and Mozilla Firefox users registered under your Chrome & Firefox web push platform.
	 */
	@JsonProperty(value="isChromeWeb", required=false)
	private Boolean isChromeWeb;
	/**
	 * Indicates whether to send to all Mozilla Firefox desktop users registered under your Firefox web push platform.
	 */
	@JsonProperty(value="isFirefox", required=false)
	private Boolean isFirefox;
	/**
	 * DOES NOT SUPPORT IOS SAFARI Indicates whether to send to all Apple's Safari desktop users registered under your Safari web push platform. Read more: iOS Safari
	 */
	@JsonProperty(value="isSafari", required=false)
	private Boolean isSafari;
	/**
	 * Indicates whether to send to all devices registered under your app's Windows platform.
	 */
	@JsonProperty(value="isWP_WNS", required=false)
	private Boolean isWP_WNS;
	/**
	 * Indicates whether to send to all devices registered under your app's Amazon Fire platform.
	 */
	@JsonProperty(value="isAdm", required=false)
	private Boolean isAdm;
	/**
	 * THIS FLAG IS NOT USED FOR WEB PUSH Please see isChromeWeb for sending to web push users. This flag only applies to Google Chrome Apps & Extensions.
	 * Indicates whether to send to all devices registered under your app's Google Chrome Apps & Extension platform.
	 */
	@JsonProperty(value="isChrome", required=false)
	private Boolean isChrome;


}