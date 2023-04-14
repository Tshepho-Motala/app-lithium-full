package lithium.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class PlaceholderReplacer {
	/**
	 * @param text Any text containing the appropriate placeholders,<br>
	 *             i.e. % + the class name + a period + the property name + %<br>
	 *             f.eg, %User.username%, or,<br>
	 *                   %PlayerExclusionV2.expiryDate%<br>
	 *                   <br>
	 * @param objects The objects containing the values that should be used to perform the replacements on the<br>
	 *                provided text<br>
	 * @return The text after performing replacements
	 */
	public static String replace(String text, Object... objects) {
		Assert.notNull(text, "Text should not be null.");
		Assert.notNull(objects, "Objects should not be null.");
		for (Object object: objects) {
			String className = object.getClass().getSimpleName();
			BeanInfo beanInfo = null;
			try {
				beanInfo = Introspector.getBeanInfo(object.getClass());
			} catch (IntrospectionException e) {
				log.error("Unable to get bean info for {}. All property replacements for this bean will be ignored.",
						className, e);
				continue;
			}
			for (PropertyDescriptor pd: beanInfo.getPropertyDescriptors()) {
				String placeholder = "%" + className + "." + pd.getName() + "%";
				String replacement = null;
				try {
					replacement = String.valueOf(pd.getReadMethod().invoke(object));
				} catch (InvocationTargetException | IllegalAccessException e) {
					log.error("Unable to invoke read method on {} for bean {}. Property replacement will be ignored.",
							pd.getName(), className, e);
					continue;
				}
				log.trace("PR.replace | placeholder: {}, replacement: {}", placeholder, replacement);
				text = text.replaceAll(placeholder, replacement);
			}
		}
		return text;
	}
}
