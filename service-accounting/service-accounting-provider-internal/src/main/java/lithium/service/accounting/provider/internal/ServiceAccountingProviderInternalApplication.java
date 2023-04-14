package lithium.service.accounting.provider.internal;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.metrics.EnableLithiumMetrics;
import lithium.service.accounting.client.stream.auxlabel.EnableAuxLabelStream;
import lithium.service.accounting.client.stream.transactionlabel.EnableTransactionLabelStream;
import lithium.service.accounting.domain.summary.stream.EnableAdjustmentStream;
import lithium.service.accounting.domain.summary.stream.EnableAsyncLabelValueStream;
import lithium.service.accounting.domain.summary.v2.stream.EnableAsyncLabelValueStreamV2;
import lithium.service.accounting.provider.internal.config.Properties;
import lithium.service.accounting.provider.internal.services.DomainCurrencyService;
import lithium.service.accounting.provider.internal.services.HistoricTransactionIngestionService;
import lithium.service.accounting.provider.internal.services.TransactionServiceWrapper;
import lithium.service.affiliate.client.stream.EnableTransactionStream;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.leaderboard.client.stream.EnableLeaderboardStream;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.rabbit.exchange.EnableLithiumRabbitExchangeCreation;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableAdjustmentStream
@EnableDomainClient
@EnableLeaderCandidate
@EnableScheduling
@EnableLeaderboardStream
@EnableTransactionStream
@EnableAuxLabelStream
@EnableTransactionLabelStream
@EnableAsyncLabelValueStream
@EnableAsyncLabelValueStreamV2
@EnableLithiumRabbitExchangeCreation
@EnableLimitInternalSystemClient
@EnableCustomHttpErrorCodeExceptions
@EnableUserApiInternalClientService
@EnableLithiumMetrics
@EnableConfigurationProperties(Properties.class)
public class ServiceAccountingProviderInternalApplication extends LithiumServiceApplication {
	@Autowired DomainCurrencyService domainCurrencyService;
	@Autowired TransactionServiceWrapper transactionServiceWrapper;
	@Autowired HistoricTransactionIngestionService historicTransactionIngestionService;

	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceAccountingProviderInternalApplication.class, args);
	}
	
	@EventListener
	public void startup(ApplicationStartedEvent event) throws Exception {
		super.startup(event);
		
		domainCurrencyService.updateDomainDefaultCurrency();
		/**
		 * Pass in a proxied version, so that we are able to call the internal methods and still maintain the @Transactional.
		 */
		transactionServiceWrapper.setServiceWrapper(transactionServiceWrapper);
		historicTransactionIngestionService.startupCodes();
	}

	//TODO: Check if still need. Is related to tomcat jdbc connection pool(replaced with Hikari CP).
	/* Hack to get the connection pool to register mbeans in jmx. The property spring.datasource.register-mbeans=true has to be set too. */
/*	@Bean
	ConnectionPool getConnectionPool(DataSource ds) throws SQLException {
		return ds.createPool().getJmxPool();
	}*/

	@Bean("customPeriodCacheKeyGenerator")
	public KeyGenerator keyGenerator() {
		return new CustomPeriodCacheKeyGenerator();
	}
	
	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
