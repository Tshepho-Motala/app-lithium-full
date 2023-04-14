package lithium.util;

import org.apache.commons.lang.StringUtils;

/**
 * Convenience class to handle string conversions.
 *
 * <p>Example Usage: </p>
 * <blockquote><pre>
 *      response.setUsername(StringUtil.nullSafeToLowerCase(user.getUsername()));
 *      response.setUsername(StringUtil.nullSafeToLowerCase(() -> user.getUsername()));
 *      response.setCity(StringUtil.allowNull(() -> user.getResidentialAddress().getCity()));
 * </pre></blockquote>
 */
public class StringUtil {

    /**
     * Convert a string to lowercase. If the string is null, return null.
     *
     * <p>Example Usage: </p>
     * <blockquote><pre>
     *      response.setUsername(StringUtil.nullSafeToLowerCase(user.getUsername()));
     * </pre></blockquote>
     *
     * @param string Any string or null
     * @return A lowercase representation of the string or null
     */
    public static String nullSafeToLowerCase(String string) {
        if (string == null) return null;
        return string.toLowerCase();
    }

    /**
     * A convenience method best used using Lambda's to return a string from a callback. The callback
     * will be wrapped in a try-catch that will allow NullPointerExceptions. If a NPE is thrown, it will
     * be catched and a null returned.
     *
     * <p>Example Usage: </p>
     * <blockquote><pre>
     *      response.setCity(StringUtil.allowNull(() -> user.getResidentialAddress().getCity()));
     * </pre></blockquote>
     *
     * @param callback A class implementing the AllowNullCallback interface.
     * @return Returns the value returned by the callback, or null
     * @see AllowNullCallback
     * @see NullPointerException
     */
    public static String allowNull(AllowNullCallback callback) {
        try {
            return callback.value();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    /**
     * A NullPointerException safe callback implementation that will convert the value returned by the
     * callback to lowercase, or return null if either the callback throws an NPE or if the result from the
     * callback is null.
     *
     * <p>Example Usage: </p>
     * <blockquote><pre>
     *      response.setUsername(StringUtil.nullSafeToLowerCase(() -> user.getUsername()));
     * </pre></blockquote>
     *
     * @param callback A class implementing the AllowNullCallback interface.
     * @return Returns the lowercase value returned by the callback, or null
     * @return
     */
    public static String nullSafeToLowerCase(AllowNullCallback callback) {
        String value = allowNull(callback);
        return nullSafeToLowerCase(value);
    }

    /**
     * A null safe check whether a string contains any value.
     * @param value The string to evaluate (may be null)
     * @return true if the string is empty or null
     */
    public static boolean isEmpty(String value) {
        if (value == null) return true;
        if (value.length() == 0) return true;
        return false;
    }

    /**
     * This is a utility method that will return true if a supplied string contains a string numeric value
     * and false if it's not a numeric value
     * @param stringValue
     * @return boolean
     */
    public static boolean isNumeric(String stringValue) {
        if(stringValue == null || stringValue.isEmpty()) {
            return false;
        }
        try {
            double v = Double.parseDouble(stringValue);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public interface AllowNullCallback {
        public String value();
    }

    public static String normalizeString(String string) {
        if (string == null) return null;
        return string.replaceAll("\n", "").replaceAll("\t", "");
    }

    public static String removeExtraSpacesBetweenWords(String words) {
        if(words == null) return null;
        return StringUtils.normalizeSpace(words);
    }

    public static String replaceUnderScoresInStringWithSpace(String word) {
        return StringUtils.replace(word, "_", " ");
    }
}
