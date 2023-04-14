package lithium.service.cashier.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.cashier.data.entities.ProcessorAccountStatus;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProcessorAccountChangeLogService {
	@Autowired
	private ChangeLogService changeLogService;
	@Autowired
	private UserApiInternalClientService userApiInternalClientService;
	@Autowired
	DomainMethodProcessorService dmpService;
	@Autowired
	LithiumServiceClientFactory serviceFactory;
	@Autowired
	ProcessorAccountTransactionService paTransactionService;
	@Autowired
	CashierService cashierService;
	@Autowired
	MessageSource messageSource;
	@Autowired
	Environment environment;

	public void addChangeLogField(Object newValue, Object oldValue, String fieldName, List<ChangeLogFieldChange> clfc) {
		if (clfc == null) {
			return;
		}
		clfc.add(ChangeLogFieldChange.builder()
				.field(fieldName)
				.fromValue(oldValue != null ? oldValue.toString() : "NOT SETUP")
				.toValue(newValue != null ? newValue.toString() : "NOT SETUP")
				.build());
	}

	public void logProcessorAccount(List<ChangeLogFieldChange> clfc, String userGuid, String domain, LithiumTokenUtil tokenUtil, String comment){
		try {
			Long userEntityId = userApiInternalClientService.getUserByGuid(userGuid).getId();
			changeLogService.registerChangesForNotesWithFullNameAndDomain(
					"user",
					"edit",
					userEntityId,
					userGuid,
					tokenUtil,
					comment,
					null,
					clfc,
					Category.FINANCE,
					SubCategory.STATUS_CHANGE,
					1,
					domain
			);
		} catch (UserClientServiceFactoryException e) {
			log.error("Can't store changelog for update processor account for user (" + userGuid + ") status due to internal server error: " + e.getMessage(), e);
		} catch (UserNotFoundException e) {
			log.error("Can't store changelog for update processor account for user(" + userGuid + ") status due to wrong user guid : " + e.getMessage(), e);
		}
	}
}
