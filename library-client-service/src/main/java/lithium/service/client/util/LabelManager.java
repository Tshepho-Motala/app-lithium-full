package lithium.service.client.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A helper class to make transaction label list management more contained.
 * @author Chris
 *
 */
public class LabelManager {
	public static final String TRANSACTION_ID = "transaction_id";
	public static final String ADDITIONAL_REFERENCE_ID = "additional_reference_id";
	public static final String PROVIDER_GUID = "provider_guid";
	public static final String GAME_GUID = "game_guid";
	public static final String PLAYER_BONUS_HISTORY_ID = "player_bonus_history_id";
	public static final String LOGIN_EVENT_ID = "login_event_id";
	public static final String BONUS_REVISION_ID = "bonus_revision_id";
	public static final String TRANSACTION_TIEBACK_ID = "transaction_tieback_id";
	public static final String GAME_SESSION_ID = "game_session_id";
	public static final String PLAYER_BONUS_TOKEN_ID = "player_bonus_token_id";
	public static final String ORIGINAL_TRANSACTION_ID = "original_transaction_id";
	public static final String REVERSE_TRANSACTION_ID = "reverse_transaction_id";
	public static final String COMMENT = "comment";
	public static final String ROUND_ID = "round_id";
	public static final String EXTERNAL_TIMESTAMP = "external_timestamp";
	public static final String PLAYER_REWARD_TYPE_HISTORY_ID = "player_reward_type_history_id";
	public static final String REWARD_REVISION_ID = "reward_revision_id";


//	public static final String = "";

	private Map<String, String> kvStore = new HashMap<>();

	/**
	 * Provide an instance of the label manager
	 * @return
	 */
	public static LabelManager instance() {
		return new LabelManager();
	}

	/**
	 * Add a label to the current label manager instance
	 * @param key
	 * @param value
	 * @return The updated label manager instance
	 */
	public LabelManager addLabel(String key, String value) {
		kvStore.put(key, value);
		return this;
	}

	public LabelManager addComment(String comment) {
		kvStore.put(COMMENT, comment);
		return this;
	}

	/**
	 * Add a previously constructed label value array to the label manager
	 *
	 * @param lvArray Following an element format of "key=value"
	 * @return Updated instance of the label value instance
	 */
	public LabelManager addLabelArray(String[] lvArray) {

		for (String kvString : Arrays.asList(lvArray)) {
			String[] kvArray = kvString.split("=");
			if (kvArray.length == 2) {
				kvStore.put(kvArray[0], kvArray[1]);
			} else if (kvArray.length == 1) {
				kvStore.put(kvArray[0], "");
			}
		}

		return this;
	}

	/**
	 * Constructs a string array with element format "key=value"
	 * @return String array of label elements
	 */
	public String[] getLabelArray() {
		String[] labelArray = new String[kvStore.size()];
		int counter = 0;
		for (Entry<String, String> kvEntry: kvStore.entrySet()) {
			labelArray[counter] = kvEntry.getKey()+"="+kvEntry.getValue();
			counter++;
		}
		return labelArray;
	}

	/**
	 * Returns the value of a specific label key or null if it does not exist.
	 * @param labelKey
	 * @return String or null
	 */
	public String findValueForLabelKey(final String labelKey) {
		return kvStore.get(labelKey);
	}
}
