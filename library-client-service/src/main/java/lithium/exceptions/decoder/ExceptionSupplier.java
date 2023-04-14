package lithium.exceptions.decoder;

import java.lang.reflect.InvocationTargetException;

@FunctionalInterface
public interface ExceptionSupplier<S> {
  S get(String message)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException,
          InvocationTargetException;
}
