package lithium.service.user.client.validators;

import lithium.service.user.client.validators.safetext.SafeTextConstraint;
import lithium.service.user.client.validators.safetext.SafeTextValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.validation.ConstraintValidatorContext;
import static org.junit.Assert.assertTrue;

public class SafeTextValidatorTest{

    SafeTextValidator safeTextValidator = new SafeTextValidator();
    SafeTextConstraint safeTextConstraint;
    ConstraintValidatorContext constraintValidatorContext;

    @Before
    public void setup() {
        safeTextConstraint = Mockito.mock(SafeTextConstraint.class);
        constraintValidatorContext = Mockito.mock(ConstraintValidatorContext.class);
    }

    @Test
    public void shouldPassWithoutSpecialCharacters() {
        assertTrue(safeTextValidator.isValid("I really hate the windows OS in all shapes and sizes", constraintValidatorContext));
    }

    @Test
    public void shouldPassWhenApostropheIsPresent() {
        assertTrue(safeTextValidator.isValid("A party at Chris' place tonight.", constraintValidatorContext));
    }

    @Test
    public void shouldFailWithAllUnsupportedCharacters() {
        assertTrue(!safeTextValidator.isValid("[]+$=<>^&\"", constraintValidatorContext));
    }

    @Test
    public void shouldFailWhenSquareBracketsArePresent() {
        assertTrue(!safeTextValidator.isValid("[]", constraintValidatorContext));
    }

    @Test
    public void shouldFailWhenACaretIsPresent() {
        assertTrue(!safeTextValidator.isValid("^", constraintValidatorContext));
    }
    @Test
    public void shouldFailWhenDollarSymbolIsPresent() {
        assertTrue(!safeTextValidator.isValid("$", constraintValidatorContext));
    }

    @Test
    public void shouldFailWhenPlusSymbolIsPresent() {
        assertTrue(!safeTextValidator.isValid("+", constraintValidatorContext));
    }

    @Test
    public void shouldFailWhenLessThanSymbolIsPresent() {
        assertTrue(!safeTextValidator.isValid("<", constraintValidatorContext));
    }

    @Test
    public void shouldFailWhenGreaterThanSymbolIsPresent() {
        assertTrue(!safeTextValidator.isValid(">", constraintValidatorContext));
    }

    @Test
    public void shouldFailWhenEqualSymbolIsPresent() {
        assertTrue(!safeTextValidator.isValid("=", constraintValidatorContext));
    }

    @Test
    public void shouldWhenFailAmpersandIsPresent() {
        assertTrue( !safeTextValidator.isValid("&", constraintValidatorContext));
    }

    @Test
    public void shouldFailDoubleApostropheIsPresent() {
        assertTrue( !safeTextValidator.isValid("\"", constraintValidatorContext));
    }

    //

}
