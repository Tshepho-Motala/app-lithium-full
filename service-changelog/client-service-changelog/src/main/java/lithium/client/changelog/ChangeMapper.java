package lithium.client.changelog;

import lithium.client.changelog.objects.ChangeLogFieldChange;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.ObjectUtils;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class ChangeMapper {

	public static List<ChangeLogFieldChange> copy(Object source, Object target, String[] fields) throws Exception {
		return mapOrCompare(source, target, fields, false);
	}
	
	public static List<ChangeLogFieldChange> compare(Object source, Object target, String[] fields) throws Exception {
		return mapOrCompare(source, target, fields, true);
	}

	public static ChangeLogFieldChange compare(Object source, Object target, String field, String display) throws Exception {

		if (ObjectUtils.isEmpty(source) && !ObjectUtils.isEmpty(target)) {
			return ChangeLogFieldChange.builder().field(field)
					.toValue("")
					.fromValue((String) PropertyAccessorFactory.forBeanPropertyAccess(target).getPropertyValue(display))
					.build();
		}

		if (ObjectUtils.isEmpty(target) && !ObjectUtils.isEmpty(source)) {
			return ChangeLogFieldChange.builder().field(field)
					.toValue((String) PropertyAccessorFactory.forBeanPropertyAccess(source).getPropertyValue(display))
					.fromValue("")
					.build();
		}

		if (!ObjectUtils.nullSafeEquals(PropertyAccessorFactory.forBeanPropertyAccess(source).getPropertyValue("id"),
				PropertyAccessorFactory.forBeanPropertyAccess(target).getPropertyValue("id"))) {
			return ChangeLogFieldChange.builder().field(field)
					.toValue((String) PropertyAccessorFactory.forBeanPropertyAccess(source).getPropertyValue(display))
					.fromValue((String) PropertyAccessorFactory.forBeanPropertyAccess(target).getPropertyValue(display))
					.build();
		}

		return ChangeLogFieldChange.builder().build();
	}
	
	private static List<ChangeLogFieldChange> mapOrCompare(Object source, Object target, String[] fields, boolean compareOnly) throws Exception {
		List<ChangeLogFieldChange> changes = new ArrayList<>();

		for (String field: fields) {
			PropertyDescriptor sourceProperty = BeanUtils.getPropertyDescriptor(source.getClass(), field);
			PropertyDescriptor targetProperty = BeanUtils.getPropertyDescriptor(target.getClass(), field);

			String sourceValue;
			String targetValue;
			Object sourceObject = null;
			boolean sourceIsDate = false;
			Object targetObject = null;
			boolean targetIsDate = false;

			if (ObjectUtils.isEmpty(sourceProperty)) {
				sourceValue = org.apache.commons.beanutils.BeanUtils.getProperty(source, field);
				if (sourceValue == null) throw new Exception("Property " + field + " not found on " + source.getClass().getName());
			} else {
				sourceObject = sourceProperty.getReadMethod().invoke(source);
				sourceValue = (sourceObject == null)? "": sourceObject.toString();
			}

			if (ObjectUtils.isEmpty(targetProperty)) {
				targetValue = org.apache.commons.beanutils.BeanUtils.getProperty(target, field);
				if (targetValue == null) throw new Exception("Property " + field + " not found on " + target.getClass().getName());
			} else {
				targetObject = targetProperty.getReadMethod().invoke(target);
				targetValue = (targetObject == null) ? "": targetObject.toString();
			}

			if (sourceObject instanceof Date) {
				sourceValue = Long.toString(((Date) sourceObject).getTime());
				sourceIsDate = true;
			} else if (sourceObject instanceof DateTime) {
				sourceValue = String.valueOf(((DateTime)sourceObject).getMillis());
				sourceIsDate = true;
			} else if (sourceObject instanceof BigDecimal) {
				sourceValue = ((BigDecimal) sourceObject).stripTrailingZeros().toPlainString();
			}

			if (targetObject instanceof Date) {
				targetValue = Long.toString(((Date) targetObject).getTime());
				targetIsDate = true;
			} else if (targetObject instanceof DateTime) {
				targetValue = String.valueOf(((DateTime)targetObject).getMillis());
				targetIsDate = true;
			} else if (targetObject instanceof BigDecimal) {
				targetValue = ((BigDecimal) targetObject).stripTrailingZeros().toPlainString();
			}

			if (!sourceValue.equals(targetValue)) {
				ChangeLogFieldChange c = new ChangeLogFieldChange();
				c.setField(field);
				c.setFromValue((targetIsDate)?String.valueOf(targetObject):targetValue);
				c.setToValue((sourceIsDate)?String.valueOf(sourceObject):sourceValue);
				changes.add(c);
				if (!compareOnly && !ObjectUtils.isEmpty(targetProperty)) targetProperty.getWriteMethod().invoke(target, sourceObject);
			}
		}
		return changes;
	}

}
