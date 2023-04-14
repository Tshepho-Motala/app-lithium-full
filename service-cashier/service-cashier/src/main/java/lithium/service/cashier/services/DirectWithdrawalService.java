package lithium.service.cashier.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.math.CurrencyAmount;
import lithium.service.cashier.client.frontend.DoRequest;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.frontend.DoStateField;
import lithium.service.cashier.client.frontend.DoStateFieldGroup;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.objects.DirectWithdrawalTransaction;
import lithium.service.cashier.exceptions.MoreThanOneMethodWithCodeException;
import lithium.service.cashier.exceptions.NoMethodWithCodeException;
import lithium.service.cashier.machine.DoMachine;
import lithium.service.domain.client.objects.Domain;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class DirectWithdrawalService {

	@Autowired
	private WebApplicationContext beanContext;
	@Autowired
	private UserService userService;
	@Autowired
	private DomainMethodService dmService;
	@Autowired
	private ChangeLogService changeLogService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private CashierService cashierService;

    public DoResponse getDirectWithdrawalResponse(String domainName, String methodCode, String amount, Map<String, String> fields, Long sessionId, String userGuid, String initiationAuthorGuid, boolean balanceLimitEscrow, String ip, Map<String, String> headers, Long linkedTransactionId) throws NoMethodWithCodeException, MoreThanOneMethodWithCodeException {

    	DoRequest withdrawRequest = DoRequest.builder().stage(1).state("VALIDATEINPUT").build();
		DoStateFieldGroup groupOne = new DoStateFieldGroup();
		DoStateField amountField = DoStateField.builder().value(amount).build();
		groupOne.getFields().put("amount", amountField);
		withdrawRequest.getInputFieldGroups().put("1", groupOne);

		DoStateFieldGroup groupTwo = new DoStateFieldGroup();
		for (Map.Entry<String, String> entry : fields.entrySet()) {
			DoStateField field = DoStateField.builder().value(entry.getValue()).build();
			groupTwo.getFields().put(entry.getKey(), field);
		}
		withdrawRequest.getInputFieldGroups().put("2", groupTwo);

		DoMachine machine = beanContext.getBean(DoMachine.class);
		DomainMethod dm = dmService.findOneEnabledByCode(domainName, methodCode, false);

		return machine.runDirectWithdraw(dm, withdrawRequest, sessionId, userGuid, initiationAuthorGuid, balanceLimitEscrow,  ip, headers, linkedTransactionId);
	}

	public void changeHistory(String userGuid, String amount, String domainMethodName, String initiatorGuid, String withdrawComment, LithiumTokenUtil token) {
		User user = userService.find(userGuid);
		List<ChangeLogFieldChange> clfc = new ArrayList<>();

		String comment = messageSource.getMessage(
				"UI_NETWORK_ADMIN.USER.DIRECT_WITHDRAWAL",
				new Object[]{
						"Domain Method: " + domainMethodName,
						"Amount of withdrawal adjustment: " + amount,
						"InitiatedBy: " + initiatorGuid,
						"Comment: " + withdrawComment
				},
				Locale.US
		);
		changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), initiatorGuid, token, comment, null, clfc,
				Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, user.domainName());
	}

	public boolean enoughFunds(String domainName, CurrencyAmount amount, boolean isBalanceEscrow, String guid) throws Exception {
		Domain domain = userService.retrieveDomainFromDomainService(domainName);
		long customerBalanceForWithdraw = isBalanceEscrow
				? cashierService.getCustomerBalance(domain.getCurrency(), domain.getName(), guid, "PLAYER_BALANCE_LIMIT_ESCROW", "PLAYER_BALANCE")
				: cashierService.getCustomerBalance(domain.getCurrency(), domain.getName(), guid);
		boolean passed = customerBalanceForWithdraw >= amount.toCents();
		if (!passed) {
			log.error("Player (" + guid + ") does not have sufficient funds(" + customerBalanceForWithdraw + ")");
		}
		return passed;
	}
}
