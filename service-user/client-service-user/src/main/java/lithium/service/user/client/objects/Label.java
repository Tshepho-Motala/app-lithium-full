package lithium.service.user.client.objects;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Label implements Serializable {
	public static final String AFFILIATED_LABEL = "affiliated";
	public static final String AFFILIATE_GUID_LABEL = "affiliateGuid";
	public static final String AFFILIATE_SECONDARY_GUID_1_LABEL = "affiliateSecondaryGuid1";
	public static final String AFFILIATE_SECONDARY_GUID_2_LABEL = "affiliateSecondaryGuid2";
	public static final String AFFILIATE_SECONDARY_GUID_3_LABEL = "affiliateSecondaryGuid3";
	public static final String CRUKS_ID = "cruksId";
	public static final String IBAN = "iban";
	public static final String GUID = "guid";
	public static final String CORRELATION_ID = "correlationId";
	public static final String PENDING_EMAIL = "pendingEmail";

	public static final List<String> COMMON_AFFILIATE_GUID_LABEL_NAMES = ImmutableList.of(
			AFFILIATE_GUID_LABEL,
			AFFILIATE_SECONDARY_GUID_1_LABEL,
			AFFILIATE_SECONDARY_GUID_2_LABEL,
			AFFILIATE_SECONDARY_GUID_3_LABEL
	);

	private static final long serialVersionUID = 1L;
	private Long id;
	int version;
	private String name;
}
