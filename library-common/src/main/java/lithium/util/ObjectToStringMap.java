package lithium.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;

public class ObjectToStringMap {
	
	public static Map<String, String> toStringMap(Object o) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Map<String, String> map = new HashMap<>();
		for (PropertyDescriptor p: BeanUtils.getPropertyDescriptors(o.getClass())) {
			String value = "";
			Object valueO = p.getReadMethod().invoke(o);
			if (valueO != null) value = valueO.toString();
			map.put(p.getName(), value);
		}
		return map;
	}
	
	public static Map<String, String> toStringMapFormMap(Object o) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Map<String, String> map = new HashMap<>();
		for (PropertyDescriptor p: BeanUtils.getPropertyDescriptors(o.getClass())) {
			String value = "";
			FormParam formParam = null;
			try {
				formParam = o.getClass().getDeclaredField(p.getName()).getAnnotation(FormParam.class);
			} catch (NoSuchFieldException | SecurityException e) {
			}
			Object valueO = p.getReadMethod().invoke(o);
			if (valueO != null) {
				value = valueO.toString();
				map.put((formParam!=null)?formParam.value():p.getName(), value);
			}
		}
		if (map.containsKey("class")) map.remove("class");
		return map;
	}
}
