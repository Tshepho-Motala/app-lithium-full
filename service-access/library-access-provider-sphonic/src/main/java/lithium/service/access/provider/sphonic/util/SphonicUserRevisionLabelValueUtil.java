package lithium.service.access.provider.sphonic.util;

import lithium.service.user.client.objects.UserRevisionLabelValue;

import java.util.List;

public class SphonicUserRevisionLabelValueUtil {
	public static String getValueFromUserRevisionLabelValues(List<UserRevisionLabelValue> labelValues, String label) {
		return labelValues.stream()
				.filter(userRevisionLabelValue -> {
					return userRevisionLabelValue.getLabelValue().getLabel().getName().toLowerCase().contentEquals(label.toLowerCase());
				})
				.map(userRevisionLabelValue -> {
					return userRevisionLabelValue.getLabelValue().getValue();
				})
				.findFirst()
				.orElse(null);
	}
}
