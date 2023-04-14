package lithium.service.cdn.provider.google.service.utils;

import lombok.experimental.UtilityClass;
import java.util.function.Predicate;

@UtilityClass
public class PredicateUtils {

  public static <T> Predicate<T> not(Predicate<T> p) {
    return p.negate();
  }
}
