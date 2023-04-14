package lithium.jpa.util;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.regex.Pattern;

public class QueryWithLogicalOperatorsUtil {

    private static final Pattern CONDITION_PATTERN = Pattern.compile("[^<>!=]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[^\\d]");
    private static final Pattern QUERY_PATTERN = Pattern.compile("[^<>!=.\\-\\d]");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("[<>!=]");

    public static Predicate resolveConditionalExpressionLong(CriteriaBuilder cb, Expression<Long> variableToCompare, String conditionExpressionRaw) {
        String condition = replaceAllWithPrecompiled(CONDITION_PATTERN, conditionExpressionRaw, "");
        Long queryValue = Long.valueOf(replaceAllWithPrecompiled(NUMBER_PATTERN, conditionExpressionRaw, ""));
        switch (condition) {
            case "<":
                return cb.lessThan(variableToCompare, queryValue);
            case ">":
                return cb.greaterThan(variableToCompare, queryValue);
            case "=":
                return cb.equal(variableToCompare, queryValue);
            case "<=":
                return cb.lessThanOrEqualTo(variableToCompare, queryValue);
            case ">=":
                return cb.greaterThanOrEqualTo(variableToCompare, queryValue);
            case "!=":
                return cb.notEqual(variableToCompare, queryValue);
            default:
                throw new IllegalArgumentException("Invalid query condition value");
        }
    }

    public static char[] getFormattedQueryArray(String currentAccountBalanceQuery) {
        return replaceAllWithPrecompiled(QUERY_PATTERN, currentAccountBalanceQuery, "").toCharArray();
    }

    public static String getQueryConditionValue(char[] formattedQueryArray) throws Exception {
        char[] query = new String(formattedQueryArray)
                .replaceAll("-", "")
                .toCharArray();

        if (query[0] == '=' && Character.isDigit(query[1])) {
            return "=";
        } else if (query[0] == '<' || query[0] == '>') {
            if (query[1] == '=') {
                if (Character.isDigit(query[2])) {
                    if (query[0] == '<') return "<=";
                    if (query[0] == '>') return ">=";
                }
            } else if (Character.isDigit(query[1])) {
                if (query[0] == '<') return "<";
                if (query[0] == '>') return ">";
            }
        } else if (query[0] == '!' && query[1] == '=' && Character.isDigit(query[2])) {
            return "!=";
        }
        throw new Exception("Invalid query condition value");
    }

    public static long getAmountInCentsValue(char[] formattedQueryArray) {
        return new BigDecimal(replaceAllWithPrecompiled(AMOUNT_PATTERN, String.valueOf(formattedQueryArray), "")).movePointRight(2).longValue();
    }

    private static String replaceAllWithPrecompiled(Pattern pattern, String value, String replacement) {
        return pattern.matcher(value).replaceAll(replacement);
    }
}
