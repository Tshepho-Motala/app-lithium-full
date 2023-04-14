package lithium.service.cashier.provider.mercadonet.service;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.client.CashierClient;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.cashier.client.objects.TransferRequest;
import lithium.service.cashier.client.objects.User;
import lithium.service.cashier.provider.mercadonet.MercadonetModuleInfo;
import lithium.service.cashier.provider.mercadonet.MercadonetModuleInfo.ConfigProperties;
import lithium.service.cashier.provider.mercadonet.data.MnetRequest;
import lithium.service.cashier.provider.mercadonet.data.MnetRequestData;
import lithium.service.cashier.provider.mercadonet.data.MnetResponse;
import lithium.service.cashier.provider.mercadonet.data.MnetResponseData;
import lithium.service.cashier.provider.mercadonet.data.TransferResult;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MercadonetService {
	@Autowired
	private LithiumServiceClientFactory services;
	@Autowired
	private TokenStore tokenStore;
	@Autowired
	private MercadonetModuleInfo info;
	
	//TODO: Do user auth and info grab
	public User authenticateUser(MnetRequest request, MnetResponse response) throws Exception {
		MnetRequestData data = request.getMnetRequestData();
		String password = ((data.getCustomerPassword()!=null)?data.getCustomerPassword():data.getPassword());
		String[] passwordSplit = password.split(":");
		password = passwordSplit[0];
		if(passwordSplit.length != 2) {
			log.error("Unable to authenticate user from mnet: " + request);
			return null;
		}
		
		String domainName = passwordSplit[1];
		String guid = data.getCustomerLogin();
		if(guid == null || guid.isEmpty()) {
			guid = data.getCustomerPin();
		}
		
		guid = domainName +"/"+guid;
		
		log.info("mnet user info guid:"+guid + " domainName: "+ domainName +" password: " + password + " providerUrl: " + info.getModuleName());
		
		ProviderClient pc = services.target(ProviderClient.class,"service-domain", true);
		if(pc == null) {
			log.error("Unable to get provider client for service domain");
		}
		Response<Iterable<ProviderProperty>> props = pc.propertiesByProviderUrlAndDomainName(info.getModuleName(), domainName);
		if(props == null || props.getStatus() != Status.OK || props.getData() == null) {
			log.error("ProviderProperties object error" + props);
		}
		
		String currency = "";

		for(ProviderProperty p : props.getData()) {
			if(p.getName().equalsIgnoreCase(ConfigProperties.CURRENCY.getValue())) {
				currency = p.getValue();
			}
		}
		
		CashierClient cashierClient = getCashierClient();
		Response<lithium.service.cashier.client.objects.User> responseData = cashierClient.getUserInfo(guid, password, currency);
		
		if(responseData.getStatus() == Status.OK) {
			lithium.service.cashier.client.objects.User user = 
					responseData.getData(); //Password will contain user token
	
			return user;
		}
		
		return null;
	}
	
	//Using tokens now, will run tests to make sure it still functions
//	private User authenticateUser(MnetRequest request, MnetResponse response) throws NumberFormatException {
//		MnetRequestData data = request.getMnetRequestData();
//		String username = ((data.getCustomerLogin()!=null)?data.getCustomerLogin():data.getCustomerPin());
//		log.debug("username : "+username);
//		String customerIdPassword = ((data.getCustomerPassword()!=null)?data.getCustomerPassword():data.getPassword());
//		String customerPassword = "";
//		
//		StringTokenizer st = new StringTokenizer(customerIdPassword, ":");
//		int customerId = 0;
//		if (st.countTokens() == 1) {
//			customerPassword = st.nextToken();
//		} else if (st.countTokens() == 2) {
//			customerId = new Integer(st.nextToken());
//			customerPassword = st.nextToken();
//		} else {
//			throw new Exception("Customer Password Supplied is wrong!");
//		}
//		if (customerId == 0) {
//			throw new Exception("CustomerId not supplied!");
//		}
//		CashierClient cashierClient = getCashierClient();
//		lithium.service.cashier.client.objects.User user = cashierClient.getUserInfo(data.getCustomerLogin()).getData();
//		Customer customer = customerDao.get(customerId);
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMw");
//		String password1;
//		String password2;
//		try {
//			password1 = MD5Crypto.getHash(customer.getPassword()+((hash == null)?sdf.format(new Date()):hash));
//			password2 = MD5Crypto.getHash(customer.getPassword()+sdf.format(new Date()));
//		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
//			password1 = "";
//			password2 = "";
//		}
//		log.debug("Comparing Passwords :");
//		log.debug("password1  (from DB)  :: "+password1);
//		log.debug("password2  (from DB)  :: "+password2);
//		log.debug("password  (from MNET) :: "+customerPassword);
//		if (!password1.equals(customerPassword)) {
//			if (!password2.equals(customerPassword)) {
//				throw new Exception("Wrong Password");
//			} else {
//				log.warn("Customer auth with 'old' password.");
//				log.warn("password1  (from DB)  :: "+password1);
//				log.warn("password2  (from DB)  :: "+password2);
//				log.warn("password  (from MNET) :: "+customerPassword);
//				customer = fillupCustomer(customerId, customer);
//			}
//		} else {
//			customer = fillupCustomer(customerId, customer);
//		}
//		return customer;
//	}
	private String nullCheck(String in) {
		if (in == null || in.startsWith("null")) return "";
		
		return in;
	}
	public void getCustomerInfo(MnetRequest request, MnetResponse response) throws Exception {
		User user = authenticateUser(request, response);
		if(user == null) throw new Exception("Could not auth user from api");
		MnetResponseData mnetResponseData = new MnetResponseData();
		mnetResponseData.setFirstname(nullCheck(user.getFirstName()));
		mnetResponseData.setLastname(nullCheck(user.getLastName()));
	
		mnetResponseData.setSsn((nullCheck(user.getSsn()).isEmpty())?"99999":user.getSsn());
		mnetResponseData.setPhoneNumber(nullCheck(user.getPhoneNumber()));
		mnetResponseData.setEmailAddress(nullCheck(user.getEmail()));
		mnetResponseData.setCity(nullCheck(user.getCity()));
		mnetResponseData.setAddress(nullCheck(user.getPostalAddress()));
		mnetResponseData.setZipCode(nullCheck(user.getZipCode()));
		
		if (user.getDateOfBirth() != null) {
			mnetResponseData.setBirthdate(user.getDateOfBirth());
		} else {
			DateTime dateOfBirth = new DateTime(1800, 1, 1, 0, 0);
			mnetResponseData.setBirthdate(dateOfBirth);
		}
		mnetResponseData.setCountry(nullCheck(user.getCountry()));
		String state = nullCheck(user.getState());
		if(!state.isEmpty()) {
			state = state.replaceAll(mnetResponseData.getCountry()+"\\.", "");
			if(state.matches("[0-9]*")) {
				mnetResponseData.setState(nullCheck(user.getCountry()));
			} else {
				mnetResponseData.setState(state);
			}
		} else {
			mnetResponseData.setState(nullCheck(user.getCountry()));
		}
		mnetResponseData.setBalance(BigDecimal.valueOf(user.getBalanceCents()).movePointLeft(2).doubleValue());
		mnetResponseData.setCurrencyCode(user.getCurrency());
		response.setStatus(MnetResponse.STATUS_OK);
		response.setMnetResponseData(mnetResponseData);
	}
	
	//TODO: sort out notifications to backoffice
//	private EmailRequest buildEmailRequest(Customer customer) {
//		EmailRequest emailRequest = new EmailRequest();
//		InternetAddress from = new InternetAddress();
//		InternetAddress[] to = new InternetAddress[1];
//		to[0] = new InternetAddress();
//		
//		if (customer == null) {
//			from.setAddress(Cashier.getProperty("external.cashier.notify.from.address.0"));
//			from.setPersonal(Cashier.getProperty("external.cashier.notify.from.personal.0"));
//			to[0].setAddress(Cashier.getProperty("external.cashier.notify.to.address.0"));
//			to[0].setPersonal(Cashier.getProperty("external.cashier.notify.to.personal.0"));
//		} else {
//			from.setAddress(Cashier.getProperty("external.cashier.notify.from.address."+customer.getSiteId(), Cashier.getProperty("external.cashier.notify.from.address.0")));
//			from.setPersonal(Cashier.getProperty("external.cashier.notify.from.personal."+customer.getSiteId(), Cashier.getProperty("external.cashier.notify.from.personal.0")));
//			to[0].setAddress(Cashier.getProperty("external.cashier.notify.to.address."+customer.getSiteId(), Cashier.getProperty("external.cashier.notify.to.address.0")));
//			to[0].setPersonal(Cashier.getProperty("external.cashier.notify.to.personal."+customer.getSiteId(), Cashier.getProperty("external.cashier.notify.to.personal.0")));
//		}
//		
//		emailRequest.setTo(to);
//		emailRequest.setFrom(from);
//		return emailRequest;
//	}
	
	
	//TODO: sort out failure notification
//	private void sendTransferExceptionNotification(MnetRequest request, MnetResponse response) {
//		log.warn("Preparing sendTransferExceptionNotification :: MnetRequest:"+request+" || MnetResponse:"+response);
//		EmailRequest emailRequest = buildEmailRequest(null);
//		emailRequest.setTemplateId("mnet-transfer-exception");
//		Map<String, Object> context = new HashMap<String, Object>();
//		context.put("response", response);
//		int cents = new Double(request.getMnetRequestData().getAmount()*100).intValue();
//		context.put("amount", CashierUtil.formatMoney(Math.abs(cents)));
//		context.put("customerId", request.getMnetRequestData().getCustomerPin());
//		context.put("transactionType", request.getMnetRequestData().getTransactionType());
//		context.put("transferRequest", request.getMnetRequestData());
//		if (request.getMnetRequestData().getTransactionDate() == null) {
//			context.put("transactionDate", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.sss").format(new Date()));
//		} else {
//			context.put("transactionDate", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.sss").format(request.getMnetRequestData().getTransactionDate().toDate()));
//		}
//		emailRequest.setContext(context);
//		log.warn("Preparing sendTransferExceptionNotification :: emailRequest:"+emailRequest+" emailServiceUrl:"+emailServiceUrl+" restTemplate:"+restTemplate);
//		EmailResponse emailResponse = restTemplate.postForObject(emailServiceUrl, emailRequest, EmailResponse.class);
//		log.warn("Finished sendTransferExceptionNotification :: emailRequest:"+emailRequest+" emailResponse:"+emailResponse);
//	}
	
	//TODO: sort out transfer notification
//	private void sendTransferNotification(
//		TransferRequest transferRequest,
//		TransferResult transferResult,
//		String transactionType
//	) {
//		log.debug("Preparing sendTransferNotification :: transferRequest:"+transferRequest+"  ||  transferResult:"+transferResult+"  ||  transactionType:"+transactionType);
//		Customer customer = transferRequest.getCustomer();
//		EmailRequest emailRequest = buildEmailRequest(customer);
//		log.info("Sending TransferNotification :: emailServiceUrl:"+emailServiceUrl+" emailRequest:"+emailRequest);
//		
//		emailRequest.setTemplateId("mnet-transfer");
//		
//		Map<String, Object> context = new HashMap<String, Object>();
//		context.put("transferRequest", transferRequest);
//		context.put("transferResult", transferResult);
//		context.put("transactionType", transactionType);
//		context.put("amount", CashierUtil.formatMoney(Math.abs(transferRequest.getCents())));
//		
//		emailRequest.setContext(context);
//		
//		restTemplate.postForObject(emailServiceUrl, emailRequest, EmailResponse.class);
//		log.info("Sent TransferNotification :: emailServiceUrl:"+emailServiceUrl+" emailRequest:"+emailRequest);
//	}
	
	//TODO: Add bonus management system in
//	private DepositBonus depositBonus(int customerId, String bonusCode, int amount) {
//		log.info("Retrieving bonus for :: customerId : "+customerId+" bonusCode : "+bonusCode+" amount : "+amount);
//		DepositBonus depositBonus = null;
//		DepositBonus defaultDepositBonus = null;
//		
//		// load eligible bonuses for user, including hidden bonuses
//		PendingBonusBL pendingBonusBL = (PendingBonusBL) SpringBL.getBean("PendingBonusBL");
//		ArrayList<DepositBonus> bonuses = pendingBonusBL.getEligibleDepositBonuses(customerId, CashierUtil.getPaymentMethodId("BO"), true);
//		log.info("Found The following bonusses : "+bonuses);
//		for (int i = 0; i < bonuses.size(); i++) {
//			DepositBonus b = (DepositBonus) bonuses.get(i);
//			// get the default bonus code
//			if (b.getDepositRule() == 4 || b.getDepositRule() == 5) {
//				defaultDepositBonus = b;
//			}
//			// find matching bonus
//			if (b.getCode().equalsIgnoreCase(bonusCode)) {
//				depositBonus = b;
//				break;
//			}
//		}
//		
//		// apply default if no bonus code entered, it is possible to have no default bonus
//		if (bonusCode.length() <= 0 && defaultDepositBonus != null) {
//			depositBonus = defaultDepositBonus;
//			//req.setAttribute("BONUS_CODE", defaultDepositBonus.getCode());
//		}
//		
//		// validate code
//		if (bonusCode.equalsIgnoreCase("none")) {
//			// if "none" is entered, do not use default deposit bonus
//			depositBonus = null;
//		} else if (depositBonus == null) {
//			// throw exception if bonus code entered and invalid
//			// if (bonusCode.length() > 0) throw new ValidationException(user.getSingleContent("error.deposit.bonuscode.invalid", "bonuscode", bonusCode));
//			// Not stopping transfers, with error messages, just ignoring bonus entered is invalid.
//		} else {
//			int min = depositBonus.getDepositMin();
//			int max = depositBonus.getDepositMax();
//			log.info("CustomerId : "+customerId+" | Amount : "+amount+" | DepositBonus : "+depositBonus);
//			if (amount < min) {
//				// Not stopping transfers, with error messages, just ignoring bonus if amount is invalid.
//				depositBonus = null;
//			}
//			if (amount > max) {
//				// Not stopping transfers, with error messages, just ignoring bonus if amount is invalid.
//				depositBonus = null;
////				throw new ValidationException(user.getSingleContent("error.deposit.bonus.max", "~max~", CashierUtil.formatMoney(max)));
//			}
//		}
//		return depositBonus;
//	}
	
	public void transfer(MnetRequest request, MnetResponse response) throws Exception {
		TransferResult transferResult = null;
		long customerBalance = 0;
		long transferCents = new Double(request.getMnetRequestData().getAmount()*100).longValue();
		long bonusCents = 0L;
		if(request.getMnetRequestData().getBonusAccepted()) {
			bonusCents = new Double(request.getMnetRequestData().getAmount()*100).longValue();
		}
		User customer;
			customer = authenticateUser(request, response);
			if(customer == null) throw new Exception("Could not auth user from api");
			TransferRequest transferRequest = TransferRequest.builder()
					.transferCents(transferCents)
					.transactionDate((request.getMnetRequestData().getTransactionDate()==null)?new DateTime():request.getMnetRequestData().getTransactionDate())
					.transactionId(request.getMnetRequestData().getTransactionId())
					.transactionNote(request.getMnetRequestData().getTransactionNote())
					.accountNumber(request.getMnetRequestData().getCardNumber())
					.transactionMethod(request.getMnetRequestData().getTransactionMethod())
					.bonusAmount(bonusCents)
					.cardType(request.getMnetRequestData().getCardType())
					.currency(request.getMnetRequestData().getCurrency())
					.ipAddress(request.getMnetRequestData().getIpAddress())
					.transactionType((request.getMnetRequestData().getTransactionType().equals(MnetRequest.TYPE_PAYOUT))?
							CashierTranType.PAYOUT.toString():CashierTranType.DEPOSIT.toString())
					.userName(customer.getUsername())
					.domainName(customer.getDomain())
					.providerGuid(info.getModuleName())
					.build();
			
			Response<Long> transferResponse = getCashierClient().transfer(transferRequest);
			//TODO: decide if we want to catch on provider level or in cashier-service and pass proper error to provider
			if(transferResponse.getStatus() != Status.OK || transferResponse.getData() == null || transferResponse.getData() <= 0) {
				response.setStatus(MnetResponse.STATUS_FAIL);
				response.setTransactionId(-1);
				response.setError("Unable to process transaction");
				//response.setError("Insufficient Funds.");
				//TODO: notification ==== sendTransferExceptionNotification(request, response);
			} else {
				response.setStatus(MnetResponse.STATUS_OK);
				response.setTransactionId(transferResponse.getData().intValue());
				response.setError("");
				//sendTransferNotification(transferRequest, transferResult, request.getMnetRequestData().getTransactionType());
			}
// TODO: check why password is being used for bonus notification stuff (wonder if it is the mnet hack to get bonus codes into the system)
//				try {
//					String bonusCode = bonusNotificationController.getBonusCode(request.getMnetRequestData().getCustomerPassword());
//					log.info("bonusCode from bonusNotificationController :: "+bonusCode);
//					DepositBonus depositBonus = depositBonus(customer.getId(), bonusCode, transferRequest.getCents());
//					transferRequest.setBonusId((depositBonus==null)?null:depositBonus.getId());
//				} catch (Exception e) {
//					transferRequest.setBonusId(null);
//					log.error("Error trying to retrieve bonus", e);
//				}
//TODO: affiliate tracking to be handled by casino-service
//					String actionCode = Cashier.getProperty("affiliate.actioncode.deposit."+customer.getSiteId(), Cashier.getProperty("affiliate.actioncode.deposit.0",""));
//					//Determine if it is first time deposit (if first deposit does not exist, use normal deposit)
//					int totalDeposits = Cashier.getCustomerDeposits(customer.getId()).getTotal();
//					if (totalDeposits == 1) { // ==1 because deposit has just been recorded
//						String tActionCode = Cashier.getProperty("affiliate.actioncode.firstdeposit."+customer.getSiteId(), Cashier.getProperty("affiliate.actioncode.firstdeposit.0",""));
//						if (!(tActionCode == null || tActionCode.isEmpty())) {
//							actionCode = tActionCode;
//						}
//					}
//					String campaign = Cashier.getProperty("affiliate.campaign."+customer.getSiteId(), Cashier.getProperty("affiliate.campaign.0"));
//					String url = Cashier.getProperty("affiliate.sale.url."+customer.getSiteId(), Cashier.getProperty("affiliate.sale.url.0"));
//					String ignoreZeroValTran = Cashier.getProperty("affiliate.ignore.zero.tran."+customer.getSiteId(), Cashier.getProperty("affiliate.ignore.zero.tran.0"));
//					if (campaign == null || url == null || ignoreZeroValTran == null || actionCode == null) {
//						//log.info("c: " + campaign+" u: "+url+" i: "+ignoreZeroValTran + " ac: " + actionCode);
//					} else {
//						//Break if zero tran should be ignored
//						if (ignoreZeroValTran.equalsIgnoreCase("true") && cents == 0) {
//						} else {
//						//Change transaction sign and c to $ value to reflect win/loss in terms of house perspective
//							CustomerBasicInfoBL customerBasicInfoBL = (CustomerBasicInfoBL) SpringBL.getBean("CustomerBasicInfoBL");
//							BasicCustomerInfo info = customerBasicInfoBL.getBasicCustomerInfo(customer.getId());
//							doAffiliateTracking(actionCode, url, cents/(100.00d)+"", transferRequest.getTransactionId()+"-"+response.getTransactionId(),"", customer.getSigninname(), info.getAffiliateId(), campaign, customer.getSigninname(), info.getBannerId());
//						}
	}
	//TODO: Discuss affiliate tracking (should go and live in cashier-service)
//	private void doAffiliateTracking(String actionCode, String url, String totalCost, String orderId, String productId, String extraData1, Integer affiliateId, String campaignId, String channelId, Integer bannerId) {
//		AffiliateWorker affiliateWorker = (AffiliateWorker) SpringBL.getBean("com.playsafe.services.api.async.AffiliateWorker");
//		affiliateWorker.registerApprovedSale(actionCode, url, totalCost, orderId, productId, extraData1, affiliateId, campaignId, channelId, bannerId.toString());
//	}
	
	public void getBalance(MnetRequest request, MnetResponse response) throws Exception {
		User user = authenticateUser(request, response);
		if(user == null) throw new Exception("Could not auth user from api");
		response.setBalance(((Double)BigDecimal.valueOf(user.getBalanceCents()).movePointLeft(2).doubleValue()).toString());
		response.setStatus(MnetResponse.STATUS_OK);
	}
	
	//TODO: Part of bonus setup, should come from service-casino
//	protected void getBonusSettings(MnetRequest request, MnetResponse response) {
//		try {
//			Customer customer = authenticateUser(request, response);
//			log.debug("Customer : "+customer);
//			response.setStatus(MnetResponse.STATUS_OK);
//			response.setBonus("");
//			response.setTerms("");
//		} catch (NumberFormatException | DatabaseException e) {
//			response.setStatus(MnetResponse.STATUS_FAIL);
//			response.setBonus("");
//			response.setTerms("");
//			log.error(e.getMessage(), e);
//		} catch (CustomerNotFoundException e) {
//			response.setStatus(MnetResponse.STATUS_FAIL);
//			response.setBonus("");
//			response.setTerms("");
//		}
//	}
	
	//TODO: Need to post back to some temp place for review before setting customer info in our system.
	public void updatePersonalInfo(MnetRequest request, MnetResponse response) {
		response.setStatus(MnetResponse.STATUS_OK);
	}
	
	public CashierClient getCashierClient() throws Exception {
		return services.target(CashierClient.class, "service-cashier", true);
	}
	
	public ProviderClient getProviderService() throws Exception {
		ProviderClient cl = null;
			cl = services.target(ProviderClient.class,"service-domain", true);
		return cl;
	}
}
