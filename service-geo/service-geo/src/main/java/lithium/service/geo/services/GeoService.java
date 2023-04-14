package lithium.service.geo.services;

import java.util.ArrayList;
import java.util.List;

import lithium.service.accounting.client.stream.transactionlabel.TransactionLabelStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.accounting.objects.TransactionLabelContainer;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.user.client.UserApiInternalClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GeoService {

	@Autowired
	private LithiumServiceClientFactory services;
	
	@Autowired private TransactionLabelStream transactionLabelStream;
	
	private UserApiInternalClient getUserApiInternalClient() throws Exception {
		UserApiInternalClient cl = null;
		
		cl = services.target(UserApiInternalClient.class, "service-user", true);
		
		return cl;
	}
	

	public void addTransactionGeoDeviceLabels(String userGuid, Long transactionId) throws Exception {
		lithium.service.user.client.objects.User user = getUserApiInternalClient().getUser(userGuid).getData();
		if (user.getLastLogin() != null) {
			List<TransactionLabelBasic> labelList = new ArrayList<>();
			if (user.getLastLogin().getCountry() != null) {
				labelList.add(TransactionLabelBasic.builder()
						.labelName("geo_country")
						.labelValue(user.getLastLogin().getCountry())
						.summarize(true)
						.build());
			}
			if (user.getLastLogin().getState() != null) {
				labelList.add(TransactionLabelBasic.builder()
						.labelName("geo_state")
						.labelValue(user.getLastLogin().getState())
						.summarize(true)
						.build());
			}
			if (user.getLastLogin().getCity() != null) {
				labelList.add(TransactionLabelBasic.builder()
						.labelName("geo_city")
						.labelValue(user.getLastLogin().getCity())
						.summarize(true)
						.build());
			}
			if (user.getLastLogin().getOs() != null) {
				labelList.add(TransactionLabelBasic.builder()
						.labelName("device_os")
						.labelValue(user.getLastLogin().getOs())
						.summarize(true)
						.build());
			}
			if (user.getLastLogin().getBrowser() != null) {
				labelList.add(TransactionLabelBasic.builder()
						.labelName("device_browser")
						.labelValue(user.getLastLogin().getBrowser())
						.summarize(true)
						.build());
			}

			TransactionLabelContainer container = TransactionLabelContainer.builder()
					.transactionId(transactionId)
					.labelList(labelList)
					.build();
			log.debug("Register TransactionLabelContainer: " + container);
			transactionLabelStream.register(container);
		}
	}
}
