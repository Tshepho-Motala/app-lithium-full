package lithium.jpa.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static lithium.jpa.util.QueryWithLogicalOperatorsUtil.getFormattedQueryArray;
import static lithium.jpa.util.QueryWithLogicalOperatorsUtil.getAmountInCentsValue;
import static lithium.jpa.util.QueryWithLogicalOperatorsUtil.getQueryConditionValue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class QueryWithLogicalOperatorsUtilTest {

    @Test
    public void getFormattedQueryArrayIsValid() {
        char[] expected = {'<', '>', '!', '-', '1', '0', '.', '0', '0', '=', '=', '0', '0'};
        char[] actual = getFormattedQueryArray("<,>!g-1 0.0z0 = =0a0 ");
        assertArrayEquals(expected, actual);
    }

    @Test
    public void getQueryAccountBalanceCentsValuePositiveLongIsValid() {
        long expected = 1200;
        char[] query = getFormattedQueryArray(">=12");
        long actual = getAmountInCentsValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryAccountBalanceCentsValuePositiveDecimalIsValid() {
        long expected = 1234;
        char[] query = getFormattedQueryArray(">12.34");
        long actual = getAmountInCentsValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryAccountBalanceCentsValueNegativeLongIsValid() {
        long expected = -2400;
        char[] query = getFormattedQueryArray("=!-24");
        long actual = getAmountInCentsValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryAccountBalanceCentsValueNegativeDecimalIsValid() {
        long expected = -43210;
        char[] query = getFormattedQueryArray("=-432.1");
        long actual = getAmountInCentsValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryAccountBalanceCentsZeroIsValid() {
        long expected = 0;
        char[] query = getFormattedQueryArray("<=0.00");
        long actual = getAmountInCentsValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryConditionValueEquals() throws Exception {
        String expected = "=";
        char[] query = getFormattedQueryArray("=0.00");
        String actual = getQueryConditionValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryConditionValueEqualsNegative() throws Exception {
        String expected = "=";
        char[] query = getFormattedQueryArray("=-201.03");
        String actual = getQueryConditionValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryConditionValueNotEquals() throws Exception {
        String expected = "!=";
        char[] query = getFormattedQueryArray("!=123456");
        String actual = getQueryConditionValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryConditionValueNotEqualsNegative() throws Exception {
        String expected = "!=";
        char[] query = getFormattedQueryArray("!=-12.34");
        String actual = getQueryConditionValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryConditionValueLess() throws Exception {
        String expected = "<";
        char[] query = getFormattedQueryArray("<51");
        String actual = getQueryConditionValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryConditionValueLessNegative() throws Exception {
        String expected = "<";
        char[] query = getFormattedQueryArray("<-51");
        String actual = getQueryConditionValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryConditionValueLessOrEquals() throws Exception {
        String expected = "<=";
        char[] query = getFormattedQueryArray("<=1");
        String actual = getQueryConditionValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryConditionValueLessOrEqualsNegative() throws Exception {
        String expected = "<=";
        char[] query = getFormattedQueryArray("<=-3.0");
        String actual = getQueryConditionValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryConditionValueGreater() throws Exception {
        String expected = ">";
        char[] query = getFormattedQueryArray(">1");
        String actual = getQueryConditionValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryConditionValueGreaterNegative() throws Exception {
        String expected = ">";
        char[] query = getFormattedQueryArray(">-0.11");
        String actual = getQueryConditionValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryConditionValueGreaterOrEquals() throws Exception {
        String expected = ">=";
        char[] query = getFormattedQueryArray(">=0");
        String actual = getQueryConditionValue(query);
        assertEquals(expected, actual);
    }

    @Test
    public void getQueryConditionValueGreaterOrEqualsNegative() throws Exception {
        String expected = ">=";
        char[] query = getFormattedQueryArray(">=-11");
        String actual = getQueryConditionValue(query);
        assertEquals(expected, actual);
    }
}