package lithium.service.cashier.services;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.client.AccountingSummaryTransactionTypeClient;
import lithium.service.accounting.client.AccountingTransactionLabelClient;
import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.accounting.client.stream.transactionlabel.TransactionLabelStream;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryAccountTransactionType;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.accounting.objects.TransactionLabelContainer;
import lithium.service.cashier.CashierTransactionLabels;
import lithium.service.cashier.ServiceCashierApplication;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.objects.User;
import lithium.service.cashier.client.objects.enums.AccountType;
import lithium.service.cashier.config.ServiceCashierConfigurationProperties;
import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.exceptions.AccountTransactionLableAlredyExistsException;
import lithium.service.cashier.exceptions.BalanceGetFailedException;
import lithium.service.cashier.exceptions.NoAccountTransactionException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.geo.client.objects.GeoLabelTransactionStreamData;
import lithium.service.geo.client.stream.GeoStream;
import lithium.service.user.client.UserApiClient;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.UserEventClient;
import lithium.service.user.client.objects.Address;
import lithium.service.user.client.objects.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lithium.service.user.client.objects.User.SYSTEM_GUID;

@Slf4j
@Service
public class CashierService {
	private static String FIRST_DEP_LABEL_YES = "yes";
	private static String FIRST_DEP_LABEL_NO = "no";

	private static final int FEE_STRATEGY_NET = 1;
	private static final int FEE_STRATEGY_GROSS = 2;

	@Autowired
	private ServiceCashierConfigurationProperties properties;
	@Autowired
	private LithiumServiceClientFactory services;
	@Autowired
	private TransactionLabelStream transactionLabelStream;
	@Autowired
	private AccountingClientService accountingClientService;
	@Autowired
	private GeoStream geoStream;

	private AccountingSummaryTransactionTypeClient getAccountingSummaryTransactionTypeService() {
		AccountingSummaryTransactionTypeClient cl = null;
		try {
			cl = services.target(AccountingSummaryTransactionTypeClient.class, "service-accounting-provider-internal", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting accounting summary service", e);
		}
		return cl;
	}

	private AccountingClient getAccountingService() throws Exception {
		AccountingClient cl = null;
		
		cl = services.target(AccountingClient.class,"service-accounting", true);
		
		return cl;
	}
	
	private UserApiInternalClient getUserApiInternalClient() throws Exception {
		UserApiInternalClient cl = null;
		
		cl = services.target(UserApiInternalClient.class, "service-user", true);
		
		return cl;
	}
	
	private AccountingTransactionLabelClient getAccountingTransactionLabelClient() {
		AccountingTransactionLabelClient cl = null;
		try {
			cl = services.target(AccountingTransactionLabelClient.class, "service-accounting-provider-internal", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting accounting transaction label service", e);
		}
		return cl;
	}
	
	private UserApiClient getUserApiService() throws Exception {
		UserApiClient cl = null;
		
		cl = services.target(UserApiClient.class,"service-user", true);
		
		return cl;
	}
	
	private UserEventClient getUserEventService() throws Exception {
		UserEventClient cl = null;
		
		cl = services.target(UserEventClient.class, "service-user", true);
		
		return cl;
	}
	
	public Long getCustomerBalance(String currency, String domainName, String userGuid) {
		try {
			Response<Long> bal = getAccountingService().get(currency, domainName, userGuid);
			if (bal != null && bal.getStatus() == Status.OK) {
				return bal.getData();
			} else {
				throw new Exception("Accounting service returned an unhealthy response.");
			}
		} catch (Exception e) {
			log.error("Could not get balance for user: " + userGuid + ". " + e.getMessage());
			throw new BalanceGetFailedException(e.getMessage());
		}
	}

	public Long getCustomerBalance(String currency, String domainName, String userGuid, String accountCode, String accountType) {
		try {
			Response<Long> bal = getAccountingService().getPath(domainName, accountCode, accountType, currency, userGuid.split("/")[0], userGuid.split("/")[1]);
			if (bal != null && bal.getStatus() == Status.OK) {
				return bal.getData();
			} else {
				throw new Exception("Accounting service returned an unhealthy response.");
			}
		} catch (Exception e) {
			log.error("Could not get balance for user: " + userGuid + ". " + e.getMessage());
			throw new BalanceGetFailedException(e.getMessage());
		}
	}
	
	public ProviderClient getProviderService() throws Exception {
		ProviderClient cl = null;

		cl = services.target(ProviderClient.class,"service-domain", true);

		return cl;
	}

	//This entire process needs to be changed to be transactional. It is not safe the way it is.
	public Response<Long> processPayout(Long amountCents, String domainName, String userGuid, String tranId, String providerGuid, String processingMethod, String currency, Fees fees, Long domainMethodProcessorId) throws Exception {
		return processPayout(amountCents, domainName, userGuid, tranId, providerGuid, processingMethod, currency, fees, domainMethodProcessorId, null, null, null, null, null, null, null);
	}
	public Response<Long> processPayout(Long amountCents, String domainName, String userGuid, String tranId,
		String providerGuid, String processingMethod, String currency, Fees fees, Long domainMethodProcessorId,
		String processorReference, String additionalReference, String processorDescription, Long accRefToWithdrawalPending,
		Long accRefFromWithdrawalPending, Long sessionId, Long playerPaymentMethodId
	) throws Exception {
		//Label meta data
		ArrayList<String> labelList = new ArrayList<>();
		labelList.add(ServiceCashierApplication.TRAN_ID_LABEL+"="+tranId);
		labelList.add(ServiceCashierApplication.PROVIDER_GUID_LABEL+"="+providerGuid);
		labelList.add(ServiceCashierApplication.PROCESSING_METHOD_LABEL+"="+processingMethod);
		if (processorReference!=null) labelList.add(ServiceCashierApplication.PROCESSOR_REFERENCE+"="+processorReference); // processor reference
		if (additionalReference!=null) labelList.add(ServiceCashierApplication.ADDITIONAL_REFERENCE+"="+additionalReference); // additional reference
		if (domainMethodProcessorId!=null) labelList.add(ServiceCashierApplication.DOMAIN_METHOD_PROCESSOR_ID+"="+domainMethodProcessorId);
		if (processorDescription!=null) labelList.add(lithium.cashier.CashierTransactionLabels.PROCESSOR_DESCRIPTION+"="+processorDescription);
		if (sessionId!=null) labelList.add(lithium.cashier.CashierTransactionLabels.SESSION_ID+"="+sessionId);
		if (playerPaymentMethodId!=null) labelList.add(lithium.cashier.CashierTransactionLabels.PLAYER_PAYMENT_METHOD_REFERENCE +"="+playerPaymentMethodId);

		Response<AdjustmentTransaction> accTran = null;

		// Fee calculations
		BigDecimal flatFee = (fees!=null&&fees.getFlatDec()!=null)?fees.getFlatDec():BigDecimal.ZERO;
		BigDecimal percentage = (fees!=null&&fees.getPercentage()!=null)?fees.getPercentage():BigDecimal.ZERO;
		BigDecimal minimumFee = (fees!=null&&fees.getMinimumDec()!=null)?fees.getMinimumDec():BigDecimal.ZERO;
		BigDecimal payoutAmount = new BigDecimal(Math.abs(amountCents)).movePointLeft(2);


		BigDecimal playerAmount = new BigDecimal(0);
		BigDecimal feeAmount = new BigDecimal(0);
		BigDecimal percentageFee = new BigDecimal(0);

		if ((percentage!=null)&&(percentage.compareTo(BigDecimal.ZERO)>0)) percentageFee = (payoutAmount.multiply(percentage.movePointLeft(2))).setScale(2, BigDecimal.ROUND_HALF_UP);;
		if ((percentageFee!=null)&&(percentageFee.compareTo(BigDecimal.ZERO)>0)) feeAmount = feeAmount.add(percentageFee);
		if ((flatFee!=null)&&(flatFee.compareTo(BigDecimal.ZERO)>0)) feeAmount = feeAmount.add(flatFee);
		if ((minimumFee!=null)&&(minimumFee.compareTo(BigDecimal.ZERO)>0)&&(minimumFee.compareTo(feeAmount)>0)) feeAmount = minimumFee;
		if ((feeAmount!=null)&&(feeAmount.compareTo(BigDecimal.ZERO)>0)) {
			playerAmount = payoutAmount.subtract(feeAmount).negate();
		} else {
			playerAmount = payoutAmount.negate();
		}

		Response<AdjustmentTransaction> accTranFee = null;
		if ((feeAmount!=null)&&(feeAmount.compareTo(BigDecimal.ZERO)>0)) {
			labelList.add(CashierTransactionLabels.FEES_FLAT+"="+flatFee);
			labelList.add(CashierTransactionLabels.FEES_MINIMUM+"="+minimumFee);
			labelList.add(CashierTransactionLabels.FEES_PERCENTAGE+"="+percentage);
			labelList.add(CashierTransactionLabels.FEES_PERCENTAGE_FEE+"="+percentageFee);
			labelList.add(CashierTransactionLabels.FEES_PLAYER_AMOUNT+"="+playerAmount.negate()); //Negation done so frontend display from label is positive
			if (accRefToWithdrawalPending != null && accRefFromWithdrawalPending == null) {
				accTranFee = getAccountingService().adjustMulti(
						feeAmount.movePointRight(2).negate().longValue(),
						DateTime.now().toString(),
						CashierTranType.PLAYER_BALANCE_PENDING_WITHDRAWAL.toString(), //accountCode
						AccountType.PLAYER_BALANCE.getCode(), //accountTypeCode
						CashierTranType.PAYOUT_FEE.toString(), //transactionTypeCode
						CashierTranType.PAYOUT_FEE.toString(), //contraAccountCode
						CashierTranType.PAYOUT_FEE.toString(), //contraAccountTypeCode
						labelList.toArray(new String[labelList.size()]),
						currency,
						domainName,
						userGuid,
						SYSTEM_GUID,
						false,
						new String[] {AccountType.PLAYER_BALANCE.getCode()}
				);
			} else {
				accTranFee = getAccountingService().adjust(
						feeAmount.movePointRight(2).negate().longValue(),
						new DateTime().toDateTimeISO().toString(),
						CashierTranType.PAYOUT_FEE.toString(), //transactionTypeCode
						CashierTranType.PAYOUT_FEE.toString(), //contraAccountCode
						CashierTranType.PAYOUT_FEE.toString(), //contraAccountTypeCode
						labelList.toArray(new String[labelList.size()]),
						currency,
						domainName,
						userGuid,
						SYSTEM_GUID,
						true
				);
			}
		}

		if (accRefToWithdrawalPending != null && accRefFromWithdrawalPending == null) {
			accTran = getAccountingService().adjustMulti(
				playerAmount.movePointRight(2).longValue(),
				DateTime.now().toString(),
				CashierTranType.PLAYER_BALANCE_PENDING_WITHDRAWAL.toString(), //accountCode
				AccountType.PLAYER_BALANCE.getCode(), //accountTypeCode
				CashierTranType.PAYOUT.toString(), //transactionTypeCode
				CashierTranType.PAYOUT.toString(), //contraAccountCode
				CashierTranType.PAYOUT.toString(), //contraAccountTypeCode
				labelList.toArray(new String[labelList.size()]),
				currency,
				domainName,
				userGuid,
				SYSTEM_GUID,
				false,
				new String[] {AccountType.PLAYER_BALANCE.getCode()}
			);
		} else {
			accTran = getAccountingService().adjust(
				playerAmount.movePointRight(2).longValue(),
				new DateTime().toDateTimeISO().toString(),
				CashierTranType.PAYOUT.toString(),
				CashierTranType.PAYOUT.toString(),
				CashierTranType.PAYOUT.toString(),
				labelList.toArray(new String[labelList.size()]),
				currency,
				domainName,
				userGuid,
				SYSTEM_GUID,
				false
			);
		}
		
		try {
			addTransactionGeoDeviceLabels(userGuid, accTran.getData().getTransactionId());
		} catch (Exception e) {
			log.error("Problem adding geo device labels to transaction (" + accTran.getData().getTransactionId() + ")" + e.getMessage(), e);
		}

		if (accTran != null && accTran.getStatus() == Status.OK && accTran.getData().getStatus() == AdjustmentTransaction.AdjustmentResponseStatus.NEW) {
			Response<UserEvent> userEvent = null;
			try {
				userEvent = getUserEventService().registerEvent(
					domainName,
					userGuid.substring(userGuid.indexOf("/") + 1, userGuid.length()),
					UserEvent.builder()
					.type(CashierTranType.PAYOUT.toString())
					.data(amountCents.toString())
					.message("Payout processed on " + new Date())
					.build()
				);
				log.debug(userEvent.toString());
			} catch (Exception e) {
				log.error("Failed to register user event for payout (" + accTran + ")");
			}

			Map<String,String> data2 = new HashMap<>();
			data2.put("processorCommunicationAmount", payoutAmountFeeManagementCalculation(payoutAmount, feeAmount, fees).toPlainString());
			if (userEvent != null) data2.put("userEventId", (userEvent.isSuccessful()) ? "" + userEvent.getData().getId() : null);
			if (accTranFee!=null) {
				data2.put("accountingFeeTranId", (accTranFee.isSuccessful())?""+accTranFee.getData().getTransactionId():null);
				data2.put("accountingFee", feeAmount.toPlainString());
				data2.put("playerAmount", playerAmount.negate().toPlainString());
			}

			return Response.<Long>builder()
					.status(Status.OK)
					.data(accTran.getData().getTransactionId())
					.data2(data2)
					.build();
		}

		return Response.<Long>builder().status(Status.CONFLICT).data(0L).build();
	}

	/**
	 * Produces the amount value that a processor expects to receive from Lithium during a payout request from a customer.
	 * The 2 possible outcomes are gross payout amount or gross payout amount less fees (net payout amount)
	 * The amount will always be positive
	 * @param payoutAmount Gross amount requested for processing
	 * @param feeAmount Fee amount for this transaction
	 * @param fees Container object of the applicable transaction fees for the processor (contains the strategy)
	 * @return payoutAmountCommunicationToProcessor
	 */
	private BigDecimal payoutAmountFeeManagementCalculation(final BigDecimal payoutAmount, final BigDecimal feeAmount, final Fees fees) {
		BigDecimal payoutAmountCommunicationToProcessor = new BigDecimal(0);

		switch (fees.getStrategy()) {
			case FEE_STRATEGY_GROSS:
				payoutAmountCommunicationToProcessor = payoutAmount.abs();
				break;
			case FEE_STRATEGY_NET:
				payoutAmountCommunicationToProcessor = payoutAmount.abs().subtract(feeAmount.abs());
				break;
		}
		return payoutAmountCommunicationToProcessor;
	}

	public Response<SummaryAccountTransactionType> accountingSummaryTransactionType(CashierTranType tranType, String domainName, String userGuid, int period, String currency) throws UnsupportedEncodingException, Exception {
		Response<SummaryAccountTransactionType> stt = getAccountingSummaryTransactionTypeService().find(
			tranType.value(),
			domainName,
			URLEncoder.encode(userGuid, "UTF-8"),
			period,
			currency);
		return stt;
	}

	public Response<Long> processDeposit(Long amountCents, String domainName, String userGuid, String tranId, String providerGuid, String processingMethod, String currency, Fees fees, Long domainMethodProcessorId) throws Exception {
		return processDeposit(amountCents, domainName, userGuid, tranId, providerGuid, processingMethod, currency, fees, domainMethodProcessorId, null, null, null, null, null, null);
	}
	public Response<Long> processDeposit(Long amountCents, String domainName, String userGuid, String tranId, String providerGuid, String processingMethod, String currency, Fees fees, Long domainMethodProcessorId, String processorReference, String additionalReference, String processorDescription, Date playerRegDate, Long sessionId, Long playerPaymentMethodId) throws Exception {
		ArrayList<String> labelList = new ArrayList<>();
		labelList.add(ServiceCashierApplication.TRAN_ID_LABEL+"="+tranId);
		labelList.add(ServiceCashierApplication.PROVIDER_GUID_LABEL+"="+providerGuid); //processor code
		labelList.add(ServiceCashierApplication.PROCESSING_METHOD_LABEL+"="+processingMethod); // method code
		if (processorReference!=null) labelList.add(ServiceCashierApplication.PROCESSOR_REFERENCE+"="+processorReference); // processor reference
		if (additionalReference!=null) labelList.add(ServiceCashierApplication.ADDITIONAL_REFERENCE+"="+additionalReference); // additional reference
		if (domainMethodProcessorId!=null) labelList.add(ServiceCashierApplication.DOMAIN_METHOD_PROCESSOR_ID+"="+domainMethodProcessorId);
		if (processorDescription!=null) labelList.add(lithium.cashier.CashierTransactionLabels.PROCESSOR_DESCRIPTION+"="+processorDescription);
		if (sessionId!=null) labelList.add(lithium.cashier.CashierTransactionLabels.SESSION_ID+"="+sessionId);
		if (playerPaymentMethodId!=null) labelList.add(lithium.cashier.CashierTransactionLabels.PLAYER_PAYMENT_METHOD_REFERENCE +"="+playerPaymentMethodId);

		BigDecimal flatFee = (fees!=null&&fees.getFlatDec()!=null)?fees.getFlatDec():BigDecimal.ZERO;
		BigDecimal percentage = (fees!=null&&fees.getPercentage()!=null)?fees.getPercentage():BigDecimal.ZERO;
		BigDecimal minimumFee = (fees!=null&&fees.getMinimumDec()!=null)?fees.getMinimumDec():BigDecimal.ZERO;
		BigDecimal depositAmount = new BigDecimal(amountCents).movePointLeft(2);

		BigDecimal playerAmount = new BigDecimal(0);
		BigDecimal feeAmount = new BigDecimal(0);
		BigDecimal percentageFee = new BigDecimal(0);

		if ((percentage!=null)&&(percentage.compareTo(BigDecimal.ZERO)>0)) percentageFee = (depositAmount.multiply(percentage.movePointLeft(2))).setScale(2, BigDecimal.ROUND_HALF_UP);;
		if ((percentageFee!=null)&&(percentageFee.compareTo(BigDecimal.ZERO)>0)) feeAmount = feeAmount.add(percentageFee);
		if ((flatFee!=null)&&(flatFee.compareTo(BigDecimal.ZERO)>0)) feeAmount = feeAmount.add(flatFee);
		if ((minimumFee!=null)&&(minimumFee.compareTo(BigDecimal.ZERO)>0)&&(minimumFee.compareTo(feeAmount)>0)) feeAmount = minimumFee;
		if ((feeAmount!=null)&&(feeAmount.compareTo(BigDecimal.ZERO)>0)) {
			playerAmount = depositAmount.subtract(feeAmount);
		} else {
			playerAmount = depositAmount;
		}
		Long depositCount = null;
		try {
			//could possibly just have done a check on the summary data for this user to determine if it was a first deposit
			Response<SummaryAccountTransactionType> stt = accountingSummaryTransactionType(
				CashierTranType.DEPOSIT,
				domainName,
				userGuid,
				Period.GRANULARITY_TOTAL,
				currency);

			if (stt.isSuccessful()) {
				if (stt.getData() == null || stt.getData().getTranCount() == 0L) {
					labelList.add(ServiceCashierApplication.FIRST_DEPOSIT_LABEL+"="+FIRST_DEP_LABEL_YES);
					addFirstTimeDepositRegSameDayLabel(playerRegDate, labelList);
					depositCount=1l;
				} else {
					labelList.add(ServiceCashierApplication.FIRST_DEPOSIT_LABEL+"="+FIRST_DEP_LABEL_NO);
					depositCount = stt.getData().getTranCount() + 1;
				}
			}
		} catch (Exception e) {
			log.error("Problem determining if this was first deposit for user (continue with tran process without validated label): " + userGuid + " amountcents: " + amountCents + " tranId: " + tranId, e);
		}
		
		Response<AdjustmentTransaction> accTran = getAccountingService().adjust(
			depositAmount.movePointRight(2).plus().longValue(),
			new DateTime().toDateTimeISO().toString(), 
			CashierTranType.DEPOSIT.toString(), //transactionTypeCode
			CashierTranType.DEPOSIT.toString(), //contraAccountCode
			CashierTranType.DEPOSIT.toString(), //contraAccountTypeCode
			labelList.toArray(new String[labelList.size()]),
			currency, 
			domainName, 
			userGuid, 
			SYSTEM_GUID,
			true
		);
		
		Response<AdjustmentTransaction> accTranFee = null;
		if ((feeAmount!=null)&&(feeAmount.compareTo(BigDecimal.ZERO)>0)) {
			labelList.add(CashierTransactionLabels.FEES_FLAT+"="+flatFee);
			labelList.add(CashierTransactionLabels.FEES_MINIMUM+"="+minimumFee);
			labelList.add(CashierTransactionLabels.FEES_PERCENTAGE+"="+percentage);
			labelList.add(CashierTransactionLabels.FEES_PERCENTAGE_FEE+"="+percentageFee);
			labelList.add(CashierTransactionLabels.FEES_PLAYER_AMOUNT+"="+playerAmount);
			accTranFee = getAccountingService().adjust(
				feeAmount.movePointRight(2).negate().longValue(), 
				new DateTime().toDateTimeISO().toString(), 
				CashierTranType.DEPOSIT_FEE.toString(), //transactionTypeCode
				CashierTranType.DEPOSIT_FEE.toString(), //contraAccountCode
				CashierTranType.DEPOSIT_FEE.toString(), //contraAccountTypeCode
				labelList.toArray(new String[labelList.size()]),
				currency, 
				domainName, 
				userGuid, 
				SYSTEM_GUID,
				true
			);
		}
		
		try {
			addTransactionGeoDeviceLabels(userGuid, accTran.getData().getTransactionId());
		} catch (Exception e) {
			log.error("Problem adding geo device labels to transaction (" + accTran.getData().getTransactionId() + ")" + e.getMessage(), e);
		}
		
		if (accTran != null && accTran.getStatus() == Status.OK && accTran.getData().getStatus() == AdjustmentTransaction.AdjustmentResponseStatus.NEW) {
			Response<UserEvent> userEvent = null;
			try {
				userEvent = getUserEventService().registerEvent(
					domainName,
					userGuid.substring(userGuid.indexOf("/") + 1, userGuid.length()),
					UserEvent.builder()
					.type(CashierTranType.DEPOSIT.toString())
					.data(amountCents.toString())
					.message("Deposit processed on " + new Date())
					.build()
				);
				log.debug(userEvent.toString());
			} catch (Exception e) {
				log.error("Failed to register user event for deposit (" + accTran + ")");
			}
			
			Map<String,String> data2 = new HashMap<>();
			data2.put("userEventId", (userEvent.isSuccessful())?""+userEvent.getData().getId():null);
			data2.put("depositCount", depositCount != null ? depositCount.toString() : null );
			if (accTranFee!=null) {
				data2.put("accountingFeeTranId", (accTranFee.isSuccessful())?""+accTranFee.getData().getTransactionId():null);
				data2.put("accountingFee", feeAmount.toPlainString());
				data2.put("playerAmount", playerAmount.toPlainString());
			}

			return Response.<Long>builder()
				.status(Status.OK)
				.data(accTran.getData().getTransactionId())
				.data2(data2)
				.build();
		}
		
		return Response.<Long>builder().status(Status.CONFLICT).data(0L).build();
	}

	private void addFirstTimeDepositRegSameDayLabel(Date playerRegDate, List<String> labelList) {
		if (playerRegDate != null) {
			DateTime dtPlayerReg = new DateTime(playerRegDate).withTimeAtStartOfDay();
			DateTime dtStartOfDayToday = DateTime.now().withTimeAtStartOfDay();
			if (dtPlayerReg.equals(dtStartOfDayToday)) {
				labelList.add(lithium.cashier.CashierTransactionLabels.FIRST_DEPOSIT_REG_SAME_DAY_LABEL+"="+FIRST_DEP_LABEL_YES);
			} else {
				labelList.add(lithium.cashier.CashierTransactionLabels.FIRST_DEPOSIT_REG_SAME_DAY_LABEL+"="+FIRST_DEP_LABEL_NO);
			}
		} else {
			labelList.add(lithium.cashier.CashierTransactionLabels.FIRST_DEPOSIT_REG_SAME_DAY_LABEL+"="+"NA");
		}
	}

	public AdjustmentTransaction reserveWithdrawalFunds(
			Long transactionId,
			String domainName,
			String playerGuid,
			Long amountCents,
			String currency,
			Long sessionId,
			boolean balanceLimitEscrow) throws Exception {
		ArrayList<String> labelList = new ArrayList<>();
		labelList.add(ServiceCashierApplication.TRAN_ID_LABEL+"="+transactionId);
		if (sessionId != null) labelList.add(lithium.cashier.CashierTransactionLabels.SESSION_ID +"="+sessionId);

		String transactionTypeCode = balanceLimitEscrow ? CashierTranType.TRANSFER_FROM_BALANCE_LIMIT_ESCROW.toString() : CashierTranType.TRANSFER_TO_PLAYER_BALANCE_PENDING_WITHDRAWAL.toString();
		String contraAccountCode = balanceLimitEscrow ? CashierTranType.PLAYER_BALANCE_LIMIT_ESCROW.toString() : AccountType.PLAYER_BALANCE.getCode();
		Response<AdjustmentTransaction> adjustment = getAccountingService().adjustMulti(
			amountCents,
			DateTime.now().toString(),
			CashierTranType.PLAYER_BALANCE_PENDING_WITHDRAWAL.toString(), //accountCode
			AccountType.PLAYER_BALANCE.getCode(), //accountTypeCode
			transactionTypeCode, //transactionTypeCode
			contraAccountCode, //contraAccountCode
			AccountType.PLAYER_BALANCE.getCode(), //contraAccountTypeCode
			labelList.toArray(new String[labelList.size()]),
			currency,
			domainName,
			playerGuid,
			SYSTEM_GUID,
			false,
			new String[] {AccountType.PLAYER_BALANCE.getCode()}
		);

		if (!adjustment.isSuccessful() || adjustment.getData().getStatus() != AdjustmentTransaction.AdjustmentResponseStatus.NEW) {
			throw new Exception("A technical error occurred during the reservation of the withdrawal funds.");
		}

		return adjustment.getData();
	}

	public AdjustmentTransaction reverseReserveWithdrawalFunds(
		Long transactionId,
		String domainName,
		String playerGuid,
		Long amountCents,
		String currency,
		Long sessionId
	) throws Exception {
		ArrayList<String> labelList = new ArrayList<>();
		labelList.add(ServiceCashierApplication.TRAN_ID_LABEL + "=" + getReverseTransactionName(transactionId));
		if (sessionId != null) labelList.add(lithium.cashier.CashierTransactionLabels.SESSION_ID +"="+sessionId);

		Response<AdjustmentTransaction> adjustment = getAccountingService().adjustMulti(
			amountCents,
			DateTime.now().toString(),
			AccountType.PLAYER_BALANCE.getCode(), //accountCode
			AccountType.PLAYER_BALANCE.getCode(), //accountTypeCode
			CashierTranType.TRANSFER_FROM_PLAYER_BALANCE_PENDING_WITHDRAWAL.toString(), //transactionTypeCode
			CashierTranType.PLAYER_BALANCE_PENDING_WITHDRAWAL.toString(), //contraAccountCode
			AccountType.PLAYER_BALANCE.getCode(), //contraAccountTypeCode
			labelList.toArray(new String[labelList.size()]),
			currency,
			domainName,
			playerGuid,
			SYSTEM_GUID,
			true,
			new String[] {AccountType.PLAYER_BALANCE.getCode()}
		);

		if (!adjustment.isSuccessful() || adjustment.getData().getStatus() != AdjustmentTransaction.AdjustmentResponseStatus.NEW) {
			throw new Exception("A technical error occurred during the reversal of reserved withdrawal funds.");
		}

		return adjustment.getData();
	}

	private String getReverseTransactionName(Long transactionId) {
		return transactionId + "_REVERSE";
	}

	private String addressLineCheck(String addressLine, boolean first) {
		if(addressLine == null || addressLine.trim().equalsIgnoreCase("")) {
			return "";
		} else {
			if (first) {
				return addressLine;
			} else {
				return "," + addressLine;
			}
		}
	}
	public User getUser(String guid, String apiToken, String currency) throws Exception {
		Response<lithium.service.user.client.objects.User> response = getUserApiService().getUser(guid, apiToken);
		
		if(response.getStatus() == Status.OK) {
			lithium.service.user.client.objects.User responseUser = response.getData();
			if(responseUser.getPostalAddress() == null) {
				responseUser.setPostalAddress(new Address());
			}
			String postalAddressString = "";
			postalAddressString += addressLineCheck(responseUser.getPostalAddress().getAddressLine1(), true);
			postalAddressString += addressLineCheck(responseUser.getPostalAddress().getAddressLine2(), false);
			postalAddressString += addressLineCheck(responseUser.getPostalAddress().getAddressLine3(), false);

			User result = User.builder()
					.firstName(responseUser.getFirstName())
					.lastName(responseUser.getLastName())
					.email(responseUser.getEmail())
					.domain(responseUser.getDomain().getName())
					.currency(currency)
					.country(responseUser.getPostalAddress().getCountryCode())
					.city(responseUser.getPostalAddress().getCity())
					.postalAddress(postalAddressString)
					.dateOfBirth(responseUser.getDateOfBirth())
					.phoneNumber(responseUser.getCellphoneNumber())
					.ssn(responseUser.getSocialSecurityNumber())
					.state(responseUser.getPostalAddress().getAdminLevel1Code())
					.username(guid)
					.zipCode(responseUser.getPostalAddress().getPostalCode())
					.balanceCents(getCustomerBalance(currency, guid.substring(0, guid.indexOf("/")), guid))
					.build();
			
			return result;
		}
		
		return null;
	}
	
	private void addTransactionGeoDeviceLabels(String userGuid, Long transactionId) throws Exception {
		if (properties.getTransactionGeoDeviceLabels().isEnabled()) {
			geoStream.register(GeoLabelTransactionStreamData.builder()
					.userGuid(userGuid)
					.transactionId(transactionId)
					.build()
			);
		}
	}

//	public TranProcessResponse processReversal(String domainName, String userGuid, String originalRemoteTranId, String originalAccountCode, String originalAccountTypeCode, String reversalTransactionTypeCode, String currencyCode) {
//
//		String logEntry = "processReversal domainName " + domainName + " userGuid " + userGuid + " originalRemoteTranId " + originalRemoteTranId
//				+ " originalAccountCode " + originalAccountCode + " originalAccountTypeCode " + originalAccountTypeCode
//				+ " reversalTransactionTypeCode " + reversalTransactionTypeCode + " currencyCode " + currencyCode;
//		Response<AdjustmentTransaction> accTran = null;
//		try {
//			accTran = getAccountingService().rollback(
//					new DateTime().toDateTimeISO().toString(),
//					reversalTransactionTypeCode,
//					ServiceCasinoApplication.TRAN_ID_REVERSE_LABEL,
//					domainName, userGuid, SYSTEM_USER, currencyCode,
//					ServiceCasinoApplication.TRAN_ID_LABEL,
//					originalRemoteTranId,
//					originalAccountCode, originalAccountTypeCode);
//
//		} catch (Exception e) {
//			log.error("Problem requesting rolling back transaction " + logEntry, e);
//		}
//
//		if (accTran != null && accTran.getStatus() == Status.OK && accTran.getData().getAdjustmentResponse() != AdjustmentResponse.ERROR) {
//			log.info("Rolled back transaction: " + accTran + " " + logEntry);
//			return TranProcessResponse.builder()
//					.tranId(accTran.getData().getTransactionId())
//					.duplicate(accTran.getData().getAdjustmentResponse() == AdjustmentResponse.DUPLICATE)
//					.build();
//		}
//
//		// It is not an error if it did not find the transaction at this point, its only an error if we can't find it anywhere.
//		return null;
//	}

	/**
	 * Process the reversal of a previously completed deposit
	 * @param processorOriginalTransactionId
	 * @param domainName
	 * @param userGuid
	 * @param tranId
	 * @param providerGuid
	 * @param processingMethod
	 * @param currency
	 * @param fees
	 * @param domainMethodProcessorId
	 * @return
	 * @throws Exception
	 */
	public Response<Long> processDepositReversal(String processorOriginalTransactionId, String domainName, String userGuid, String tranId, String providerGuid, String processingMethod, String currency, Fees fees, Long domainMethodProcessorId) throws Exception {
		ArrayList<String> labelList = new ArrayList<>();
		labelList.add(ServiceCashierApplication.TRAN_ID_LABEL+"="+tranId);
		labelList.add(ServiceCashierApplication.PROVIDER_GUID_LABEL+"="+providerGuid); //processor code
		labelList.add(ServiceCashierApplication.PROCESSING_METHOD_LABEL+"="+processingMethod); // method code
		if (domainMethodProcessorId!=null) labelList.add(ServiceCashierApplication.DOMAIN_METHOD_PROCESSOR_ID+"="+domainMethodProcessorId);

		BigDecimal flatFee = (fees!=null&&fees.getFlatDec()!=null)?fees.getFlatDec():BigDecimal.ZERO;
		BigDecimal minimumFee = (fees!=null&&fees.getMinimumDec()!=null)?fees.getMinimumDec():BigDecimal.ZERO;

		BigDecimal feeAmount = new BigDecimal(0);
		BigDecimal percentageFee = new BigDecimal(0);
		BigDecimal playerAmount = new BigDecimal(0);

		// FIXME: 2019/07/29 Figure out how we will handle reversal fees at some future time. Currently only need to use value passed in from processor
		if ((flatFee!=null)&&(flatFee.compareTo(BigDecimal.ZERO)>0)) feeAmount = feeAmount.add(flatFee);
		if ((minimumFee!=null)&&(minimumFee.compareTo(BigDecimal.ZERO)>0)&&(minimumFee.compareTo(feeAmount)>0)) feeAmount = minimumFee;


		// TODO: 2019/07/29 The seperate transactions can be done in a single transactional unit once the "bonus rework" is completed . It allows multiple trans to be passed to accounting as a unit of work

		Response<AdjustmentTransaction> accTran = getAccountingService().rollback(
				new DateTime().toDateTimeISO().toString(),
				CashierTranType.CASHIER_DEPOSIT_REVERSAL.value(),
				CashierTransactionLabels.TRAN_ID_REVERSE_LABEL,
				domainName,
				userGuid,
				SYSTEM_GUID,
				currency,
				CashierTransactionLabels.TRAN_ID_LABEL,
				processorOriginalTransactionId,
				CashierTranType.DEPOSIT.toString(), //accountCode
				CashierTranType.DEPOSIT.toString() //accountTypeCode
		);

		if (accTran != null && accTran.getStatus() == Status.OK && accTran.getData().getStatus() == AdjustmentTransaction.AdjustmentResponseStatus.NEW) {
			Map<String,String> data2 = new HashMap<>();

			if ((feeAmount!=null)&&(feeAmount.compareTo(BigDecimal.ZERO)>0)) {
				labelList.add(CashierTransactionLabels.FEES_FLAT + "=" + flatFee);
				labelList.add(CashierTransactionLabels.FEES_MINIMUM + "=" + minimumFee);
				labelList.add(CashierTransactionLabels.FEES_PERCENTAGE + "=" + percentageFee);
				labelList.add(CashierTransactionLabels.FEES_PERCENTAGE_FEE + "=" + percentageFee);
				labelList.add(CashierTransactionLabels.FEES_PLAYER_AMOUNT + "=" + playerAmount);
				Response<AdjustmentTransaction> accTranFee = getAccountingService().adjust(
						feeAmount.movePointRight(2).negate().longValue(),
						new DateTime().toDateTimeISO().toString(),
						CashierTranType.REVERSAL_FEE.toString(), //transactionTypeCode
						CashierTranType.REVERSAL_FEE.toString(), //contraAccountCode
						CashierTranType.REVERSAL_FEE.toString(), //contraAccountTypeCode
						labelList.toArray(new String[labelList.size()]),
						currency,
						domainName,
						userGuid,
						SYSTEM_GUID,
						true
				);
				if (accTranFee!=null) data2.put("accountingFeeTranId", (accTranFee.isSuccessful())?""+accTranFee.getData().getTransactionId():null);
			}

			return Response.<Long>builder()
					.status(Status.OK)
					.data(accTran.getData().getTransactionId())
					.data2(data2)
					.build();
		}

		return Response.<Long>builder().status(Status.CONFLICT).data(0L).build();
	}

	/**
	 * Process a reversal of a previously completed payout
	 * The reversal will only be processed if not done so before.
	 *
	 * @param processorOriginalTransactionId
	 * @param domainName
	 * @param userGuid
	 * @param tranId
	 * @param providerGuid
	 * @param processingMethod
	 * @param currency
	 * @param fees
	 * @param domainMethodProcessorId
	 * @return
	 * @throws Exception
	 */
	public Response<Long> processPayoutReversal(String processorOriginalTransactionId, String domainName, String userGuid, String tranId, String providerGuid, String processingMethod, String currency, Fees fees, Long domainMethodProcessorId) throws Exception {
		ArrayList<String> labelList = new ArrayList<>();
		labelList.add(ServiceCashierApplication.TRAN_ID_LABEL+"="+tranId);
		labelList.add(ServiceCashierApplication.PROVIDER_GUID_LABEL+"="+providerGuid); //processor code
		labelList.add(ServiceCashierApplication.PROCESSING_METHOD_LABEL+"="+processingMethod); // method code
		if (domainMethodProcessorId!=null) labelList.add(ServiceCashierApplication.DOMAIN_METHOD_PROCESSOR_ID+"="+domainMethodProcessorId);

		BigDecimal flatFee = (fees!=null&&fees.getFlatDec()!=null)?fees.getFlatDec():BigDecimal.ZERO;
		BigDecimal minimumFee = (fees!=null&&fees.getMinimumDec()!=null)?fees.getMinimumDec():BigDecimal.ZERO;

		BigDecimal feeAmount = new BigDecimal(0);
		BigDecimal percentageFee = new BigDecimal(0);

		// FIXME: 2019/07/29 Figure out how we will handle reversal fees at some future time. Currently only need to use value passed in from processor
		if ((flatFee!=null)&&(flatFee.compareTo(BigDecimal.ZERO)>0)) feeAmount = feeAmount.add(flatFee);
		if ((minimumFee!=null)&&(minimumFee.compareTo(BigDecimal.ZERO)>0)&&(minimumFee.compareTo(feeAmount)>0)) feeAmount = minimumFee;


		// TODO: 2019/07/29 The seperate transactions can be done in a single transactional unit once the "bonus rework" is completed . It allows multiple trans to be passed to accounting as a unit of work

		Response<AdjustmentTransaction> accTran = getAccountingService().rollback(
				new DateTime().toDateTimeISO().toString(),
				CashierTranType.CASHIER_PAYOUT_REVERSAL.value(),
				CashierTransactionLabels.TRAN_ID_REVERSE_LABEL,
				domainName,
				userGuid,
				SYSTEM_GUID,
				currency,
				CashierTransactionLabels.TRAN_ID_LABEL,
				processorOriginalTransactionId,
				CashierTranType.PAYOUT.toString(), //accountCode
				CashierTranType.PAYOUT.toString() //accountTypeCode
		);

		if (accTran != null && accTran.getStatus() == Status.OK && accTran.getData().getStatus() == AdjustmentTransaction.AdjustmentResponseStatus.NEW) {
			Response<AdjustmentTransaction> accTranFee = getAccountingService().adjust(
					feeAmount.movePointRight(2).negate().longValue(),
					new DateTime().toDateTimeISO().toString(),
					CashierTranType.REVERSAL_FEE.toString(), //transactionTypeCode
					CashierTranType.REVERSAL_FEE.toString(), //contraAccountCode
					CashierTranType.REVERSAL_FEE.toString(), //contraAccountTypeCode
					labelList.toArray(new String[labelList.size()]),
					currency,
					domainName,
					userGuid,
					SYSTEM_GUID,
					true
			);

			Response<UserEvent> userEvent = null;

			Map<String,String> data2 = new HashMap<>();
			data2.put("userEventId", (userEvent.isSuccessful())?""+userEvent.getData().getId():null);
			if (accTranFee!=null) data2.put("accountingFeeTranId", (accTranFee.isSuccessful())?""+accTranFee.getData().getTransactionId():null);

			return Response.<Long>builder()
					.status(Status.OK)
					.data(accTran.getData().getTransactionId())
					.data2(data2)
					.build();
		}

		return Response.<Long>builder().status(Status.CONFLICT).data(0L).build();
	}

	public void registerTransactionLabelContainer(TransactionLabelContainer entry) {
		transactionLabelStream.register(entry);
	}

	public void addAccountingTransactionLabel(Transaction transaction, List<TransactionLabelBasic> labelsList) throws Exception {
		String transactionTypeCode = null;
		if (TransactionType.DEPOSIT.equals(transaction.getTransactionType())) {
			transactionTypeCode = "CASHIER_DEPOSIT";
		} else if (TransactionType.WITHDRAWAL.equals(transaction.getTransactionType())) {
			transactionTypeCode = "CASHIER_PAYOUT";
		}

		TransactionLabelContainer storedLabels = getAccountingTransactionLabelClient().findLabelsByExternalTransaction(transaction.getId().toString(), transactionTypeCode);
		if (storedLabels == null) {
			log.info("No account transaction for cashier transactionId: " + transaction.getId() + "transactionType: " + transaction.getTransactionType());
			throw new NoAccountTransactionException();
		} else if (storedLabels.getLabelList() != null && !storedLabels.getLabelList().isEmpty()
				&& storedLabels.getLabelList().stream().anyMatch(sl -> labelsList.stream().anyMatch(l -> sl.getLabelName().equalsIgnoreCase(l.getLabelName())))) {
			log.info("Account transaction already contains one of the label: " + transaction.getId() + "transactionType: " + transaction.getTransactionType() + " labels to add:" + labelsList);
			throw new AccountTransactionLableAlredyExistsException();
		}

		TransactionLabelContainer transactionLabelContainer = TransactionLabelContainer.builder()
				.transactionId(storedLabels.getTransactionId())
				.labelList(labelsList)
				.build();

		log.info("Adding account transaction labels for the transactionId" + storedLabels.getTransactionId() + " cashier transactionId: " + transaction.getId() + "transactionType: " + transaction.getTransactionType() + " labels to add:" + labelsList);
		getAccountingTransactionLabelClient().addLabels(transactionLabelContainer);
	}

	public Long getRelatedAccountingTransactionId(Long transactionId, String transactionTypeCode) throws Status510AccountingProviderUnavailableException {
		return accountingClientService.findExternalTransactionId(String.valueOf(transactionId), transactionTypeCode);
	}

	public Long getRelatedAccountingReverseTransactionId(Long transactionId) throws Status510AccountingProviderUnavailableException {
		return accountingClientService.findExternalReverseTransactionId(getReverseTransactionName(transactionId));
	}
}
