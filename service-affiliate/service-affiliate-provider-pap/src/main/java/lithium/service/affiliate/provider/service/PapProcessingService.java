package lithium.service.affiliate.provider.service;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.service.affiliate.provider.data.objects.PapJsonTransaction;
import lithium.service.affiliate.provider.data.objects.PapJsonTransactionWrapper;
import lithium.service.affiliate.provider.stream.objects.PapTransactionStreamData;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PapProcessingService {
	@Autowired RestTemplate restTemplate;
	
	private static final String PAP_AUTH_CLASS_NAME = "Gpf_Api_AuthService";
	private static final String PAP_AUTH_METHOD_NAME = "authenticate";
	
	private static final String PAP_RPC_SERVER_CLASS_NAME = "Gpf_Rpc_Server";
	private static final String PAP_RPC_SERVER_METHOD_NAME = "run";
	
	private static final String PAP_TRANSACTION_CLASS_NAME = "Pap_Merchants_Transaction_TransactionsForm";
	private static final String PAP_TRANSACTION_METHOD_NAME = "add";
	
	public void sendDataToPap(PapTransactionStreamData papData) {
		try {
			boolean tranResult = registerTransaction(authenticate(papData.getAuthTokenUser(), papData.getAuthTokenPassword(), papData.getBaseUrl()), papData);
			
			if (!tranResult) {
				log.error("Failed to register transaction in PaP, got failed response " + papData);
			}
		} catch (Exception e) {
			log.error("Unble to register transaction on PaP system" + papData, e);
		}
	}
	
	public String authenticate(String merchantUsername, String merchantPassword, String url) throws Exception {
		
		PapJsonTransaction authRequest = new PapJsonTransaction(PAP_AUTH_CLASS_NAME, PAP_AUTH_METHOD_NAME);
		authRequest.addField("username", merchantUsername);
		authRequest.addField("password", merchantPassword);
		authRequest.addField("roleType", "M"); //M = merchant ; A = affiliate
		authRequest.addField("isFromApi", "Y");
		authRequest.addField("apiVersion", null);
		
		ObjectMapper om = new ObjectMapper();
		String papShit = "D=";

		papShit += om.writeValueAsString(authRequest);
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		final HttpEntity<String> requestEntity = new HttpEntity<>(
				papShit, headers);

		//log.info(restTemplate.exchange("http://isp55.com/scripts/server.php", HttpMethod.POST, requestEntity, String.class).getBody());
		

		JsonNode rootNode = om.readTree(restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody());
		
		JsonNode successNode = rootNode.findValue("success");
		
		if (successNode != null && successNode.asText().equals("Y")) {
			
			JsonNode fields = rootNode.findValue("fields");
			for (JsonNode jn : fields) {
				Iterator<JsonNode> jit = jn.iterator();
				if (jit.next().asText().equals("S"))
					return jit.next().asText();
			}
			
		} else {
			JsonNode messageNode = rootNode.findValue("message");
			log.error("Failure authenticating Pap merchant: " + messageNode.asText());
			throw new Exception("Failure authenticating Pap merchant: " + messageNode.asText());
		}

		
		return "";
	}
	
	public boolean registerTransaction(String papSessionId, PapTransactionStreamData tranData) throws Exception {
		PapJsonTransaction transaction = new PapJsonTransaction(PAP_TRANSACTION_CLASS_NAME, PAP_TRANSACTION_METHOD_NAME);
		String tranDate = DateFormatUtils.format(tranData.getTransactionDate(), "yyyy-MM-dd");
		transaction.addField("Id", "");
		transaction.addField("rstatus", "A");
		transaction.addField("dateinserted", tranDate);
		transaction.addField("dateapproved", tranDate);
		transaction.addField("totalcost", tranData.getAmount());
		transaction.addField("channel", tranData.getOwnerGuid());
		transaction.addField("fixedcost", "");
		transaction.addField("multiTier", "Y");
		transaction.addField("commtypeid", tranData.getCommissionTypeId()); 
		transaction.addField("bannerid", tranData.getBannerGuid());
		transaction.addField("payoutstatus", "U");
		transaction.addField("countrycode", "");
		transaction.addField("userid", tranData.getAffiliateGuid());
		transaction.addField("campaignid", tranData.getCampaignGuid());
		transaction.addField("parenttransid", "");
		transaction.addField("commission", "");
		transaction.addField("tier", "1");
		transaction.addField("commissionTag", "Commissions are computed automatically");
		transaction.addField("orderid", tranData.getOwnerGuid() + "-" + tranData.getTransactionType() + "-" + tranDate);
		transaction.addField("productid", "");
		transaction.addField("data1", tranData.getOwnerGuid());
		transaction.addField("data2", "");
		transaction.addField("data3", "");
		transaction.addField("data4", "");
		transaction.addField("data5", "");
		transaction.addField("trackmethod", "1");
		transaction.addField("refererurl", tranData.getReferrerUrl());
		transaction.addField("ip", "");
		transaction.addField("firstclicktime", "");
		transaction.addField("firstclickreferer", "");
		transaction.addField("firstclickip", "");
		transaction.addField("firstclickdata1", "");
		transaction.addField("firstclickdata2", "");
		transaction.addField("lastclicktime", "");
		transaction.addField("lastclickreferer", "");
		transaction.addField("lastclickip", "");
		transaction.addField("lastclickdata1", "");
		transaction.addField("lastclickdata2", "");
		transaction.addField("systemnote", "");
		transaction.addField("merchantnote", "");
		
		ArrayList<PapJsonTransaction> tranList = new ArrayList<>();
		tranList.add(transaction);
		
		PapJsonTransactionWrapper transactionRequestWrapper = new PapJsonTransactionWrapper(PAP_RPC_SERVER_CLASS_NAME, PAP_RPC_SERVER_METHOD_NAME, papSessionId, tranList);
		
		ObjectMapper om = new ObjectMapper();
		String papShit = "D=";

		papShit += om.writeValueAsString(transactionRequestWrapper);
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		final HttpEntity<String> requestEntity = new HttpEntity<>(
				papShit, headers);
		log.info("pap tran exec request: " + requestEntity.getBody());
		
		String response = restTemplate.exchange(tranData.getBaseUrl(), HttpMethod.POST, requestEntity, String.class).getBody();
		
		log.info("pap tran exec response: " + response);
		
		// See if it helps with pap channel duplications
		try {
			Thread.sleep(100L);
		} catch (Exception ex) {
			log.error("Unable to sleep pap procesing thread.", ex);
		}
		
		JsonNode rootNode = om.readTree(response);
		JsonNode successNode = rootNode.findValue("success");
		if (successNode != null && successNode.asText().equals("Y")) {
			return true;
		} else {
			JsonNode messageNode = rootNode.findValue("message");
			log.error("Failure to add transaction in pap: " + messageNode.asText());
			throw new Exception("Failure to add transaction in pap: " + messageNode.asText());
		}

	}
	
	//"{\"C\":\"Gpf_Rpc_Server\", \"M\":\"run\", \"requests\":[{\"C\":\"Pap_Merchants_Transaction_TransactionsForm\", \"M\":\"add\", \"fields\":[[\"name\",\"value\"],[\"Id\",\"\"],[\"rstatus\",\"A\"],[\"dateinserted\",\""+context.affiliate_tran_date+"\"],[\"dateapproved\",\""+context.affiliate_tran_date+"\"],[\"totalcost\",\""+transactions.total_rake+"\"],[\"channel\",\""+transactions.user_name+"\"],[\"fixedcost\",\"\"],[\"multiTier\",\"Y\"],[\"commtypeid\",\""+context.affiliate_tran_action_code+"\"],[\"bannerid\",\""+transactions.external_banner_id+"\"],[\"payoutstatus\",\"U\"],[\"countrycode\",\"\"],[\"userid\",\""+transactions.external_affiliate_id+"\"],[\"campaignid\",\""+context.affiliate_campaign+"\"],[\"parenttransid\",\"\"],
	//[\"commission\",\"\"],[\"tier\",\"1\"],[\"commissionTag\",\"Commissions are computed automatically\"],[\"orderid\",\""+transactions.customer_id+"-"+transactions.tran_date_string+"\"],[\"productid\",\"\"],[\"data1\",\""+transactions.user_name+"\"],[\"data2\",\"\"],[\"data3\",\"\"],[\"data4\",\"\"],[\"data5\",\"\"],
	//[\"trackmethod\",\""+context.affiliate_tracking+"\"],[\"refererurl\",\""+context.affiliate_referrer+"\"],[\"ip\",\"\"],[\"firstclicktime\",\"\"],[\"firstclickreferer\",\"\"],[\"firstclickip\",\"\"],[\"firstclickdata1\",\"\"],[\"firstclickdata2\",\"\"],[\"lastclicktime\",\"\"],[\"lastclickreferer\",\"\"],[\"lastclickip\",\"\"],[\"lastclickdata1\",\"\"],[\"lastclickdata2\",\"\"],[\"systemnote\",\"\"],[\"merchantnote\",\"\"]]}], \"S\":\""+context.affiliate_session_id+"\"}"     
}