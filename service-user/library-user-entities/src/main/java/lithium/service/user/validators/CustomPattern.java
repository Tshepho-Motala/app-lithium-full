package lithium.service.user.validators;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = CustomPatternValidator.class)
public @interface CustomPattern {
  String message() default "{javax.validation.constraints.Pattern.message}";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
  String email();
  String telephoneNumber();
  String cellphoneNumber();
  String domain();
  String firstName();
  String lastName();


  @Target({TYPE, ANNOTATION_TYPE})
  @Retention(RUNTIME)
  @Documented
  @interface List
  {
    CustomPattern[] value();
  }
}
