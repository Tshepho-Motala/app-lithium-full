package lithium.service.cashier;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.client.objects.transaction.dto.Processor;
import lithium.service.cashier.client.objects.ProcessorProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ProcessorFileReader {
	public Map<String, Processor> read() throws Exception {
		HashMap<String, Processor> processors = new HashMap<>();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		
		try {
			for (Resource resource: resolver.getResources("classpath*:processors/*.json")) {
				Pattern p = Pattern.compile("([a-z\\-]+)\\.json");
				Matcher m = p.matcher(resource.getFilename());
				if (m.matches()) {
					String name = m.group(1);
					log.info("Found processor " + resource.getFilename());
					ObjectMapper mapper = new ObjectMapper();
					Processor processor = mapper.readValue(resource.getInputStream(), Processor.class);
					processor.getProperties().add(ttlProperty());
					processor.getProperties().add(amountDifferenceProperty());
					processor.getProperties().add(oobProperty());
					processor.getProperties().add(alertCommServiceProperty());
					processor.getProperties().add(retryBeforeExpireProperty());
					processor.getProperties().add(directWithdrawSupportProperty());
					processor.getProperties().add(disableOnBlockAccount());
					processor.getProperties().add(setVerifiedAccount());
					processor.getProperties().add(setContraAccount());
					processor.getProperties().add(clientProcessingTimeout());
					processor.getProperties().add(processorAccountVerifications());
					processor.getProperties().add(withdrawMethodCode());
					processor.getProperties().add(allowMultiTransactions());
					if (processor.getDeposit() != null && processor.getDeposit()) {
						processor.getProperties().add(maxProcessorAccountsCount());
						processor.getProperties().add(depositTileLeft());
						processor.getProperties().add(depositTileMiddle());
						processor.getProperties().add(depositTileRight());
						processor.getProperties().add(defaultDepositTile());
						processor.getProperties().add(reversalOnInvalidAccount());
						processor.getProperties().add(firstDeposit());
					}
					processors.put(name, processor);
				} else {
					throw new Exception("The method file " + resource.getFilename() + " does not comply to the naming convention pattern.");
				}
			}
		} catch (FileNotFoundException fne) {
		}
		return processors;
	}

	private ProcessorProperty oobProperty() {
		return ProcessorProperty.builder()
				.name("oob")
				.type("boolean")
				.description("Allow 'Out of Band Transfers' - If set to true, processors can send additional calls to callback url which will produce a new transaction.")
				.defaultValue("false")
				.build();
	}

	private ProcessorProperty ttlProperty() {
		return ProcessorProperty.builder()
			.name("ttl")
			.type("ms")
			.description("Time specified in ms after start of transaction before it will be marked EXPIRED. -1 means never expires. Default is one week.")
			.defaultValue(null)
			.build();
	}
	
	private ProcessorProperty amountDifferenceProperty() {
		return ProcessorProperty.builder()
				.name("amountdiff")
				.type("cents")
				.description("This is the difference between amount entered by player, and amount returned from processor. a value of 100 = $1, eg. Player enters $25, but processor returns deposit done for $24.50, so we accept deposit, it falls within the $1 difference entered.")
				.defaultValue("0")
				.build();
	}

	private ProcessorProperty alertCommServiceProperty() {
		return ProcessorProperty.builder()
				.name("alertCommService")
				.type("int")
				.description("Workflow status change alert service to use. A value of 0 means use the mail communication service, 1 means use notification communication service, and 2 means use both. Invalid value will use the mail system. The default is 0.")
				.defaultValue("0")
				.build();
	}

	private ProcessorProperty retryBeforeExpireProperty() {
		return ProcessorProperty.builder()
				.name("retryBeforeExpire")
				.type("boolean")
				.description("Retry Processing before its expiration.")
				.defaultValue("false")
				.build();
	}

	private ProcessorProperty directWithdrawSupportProperty() {
		return ProcessorProperty.builder()
				.name("direct_withdrawal_supported")
				.type("boolean")
				.description("Direct withdrawal supported.")
				.defaultValue("false")
				.build();
	}

	private ProcessorProperty disableOnBlockAccount() {
		return ProcessorProperty.builder()
				.name("disable_on_block_account")
				.type("boolean")
				.description("Disable domain processor method for current user in case all processor accounts are blocked.")
				.defaultValue("false")
				.build();
	}

	private ProcessorProperty setVerifiedAccount() {
		return ProcessorProperty.builder()
				.name("set_verified_account")
				.type("boolean")
				.description("Enables account verification.")
				.defaultValue("false")
				.build();
	}

	private ProcessorProperty setContraAccount() {
		return ProcessorProperty.builder()
				.name("set_contra_account")
				.type("boolean")
				.description("Enables contra account marking.")
				.defaultValue("false")
				.build();
	}

	private ProcessorProperty clientProcessingTimeout() {
		return ProcessorProperty.builder()
				.name("client_processing_timeout")
				.type("string")
				.description("Client timeout for deposit processing.")
				.defaultValue(null)
				.avalableForClient(true)
				.build();
	}

	private ProcessorProperty depositTileLeft() {
		return ProcessorProperty.builder()
				.name("Deposit Tile 1 (left)")
				.type("int")
				.description("Specify the quick deposit amount for this tile (eg. 10)")
				.defaultValue("10")
				.avalableForClient(true)
				.build();
	}

	private ProcessorProperty depositTileMiddle() {
		return ProcessorProperty.builder()
				.name("Deposit Tile 2 (middle)")
				.type("int")
				.description("Specify the quick deposit amount for this tile (eg. 30)")
				.defaultValue("30")
				.avalableForClient(true)
				.build();
	}

	private ProcessorProperty depositTileRight() {
		return ProcessorProperty.builder()
				.name("Deposit Tile 3 (right)")
				.type("int")
				.description("Specify the quick deposit amount for this tile (eg. 50)")
				.defaultValue("50")
				.avalableForClient(true)
				.build();
	}

	private ProcessorProperty defaultDepositTile() {
		return ProcessorProperty.builder()
				.name("Default deposit tile")
				.type("int")
				.description("Specify default deposit tile selection. 0 means no deposit tile.")
				.defaultValue("2")
				.avalableForClient(true)
				.build();
	}

	private ProcessorProperty processorAccountVerifications() {
		return ProcessorProperty.builder()
			.name("account_verifications")
			.type("string")
			.description("Comma separated sorted list of processor account verifications.")
			.defaultValue(null)
			.avalableForClient(true)
			.build();
	}

	private ProcessorProperty maxProcessorAccountsCount() {
		return ProcessorProperty.builder()
			.name("max_active_accounts")
			.type("int")
			.description("Maximum allowed active processor accounts count.")
			.defaultValue(null)
			.avalableForClient(true)
			.build();
	}
	private ProcessorProperty withdrawMethodCode() {
		return ProcessorProperty.builder()
			.name("withdraw_method_code")
			.type("string")
			.description("Method code that will be used for withdrawal. By default the same method.")
			.defaultValue(null)
			.build();
	}

	private ProcessorProperty reversalOnInvalidAccount() {
		return ProcessorProperty.builder()
			.name("reversal_on_invalid_account")
			.type("boolean")
			.description("Initiate withdraw after success deposit if user's payment method verification failed.")
			.defaultValue("false")
			.avalableForClient(false)
			.build();
	}

	private ProcessorProperty allowMultiTransactions() {
		return ProcessorProperty.builder()
				.name("allow_multi_transactions")
				.type("boolean")
				.description("Allow user to create new transaction while previous one is pending.")
				.defaultValue("true")
				.build();
	}

	private ProcessorProperty firstDeposit() {
		return ProcessorProperty.builder()
			.name("first_deposit")
			.type("boolean")
			.description("Is method available to player on first deposit.")
			.defaultValue("true")
			.build();
	}
}
