package lithium.service.kyc.provider.smileindentity.api.schema;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class ResolveDobResponse {
	private DateTime dob;
	private boolean dobYearOnly = false;

	public ResolveDobResponse(DateTime dob, boolean dobYearOnly) {
		this.dob = dob;
		this.dobYearOnly = dobYearOnly;
	}

	public ResolveDobResponse(DateTime dob) {
		this.dob = dob;
	}
}
