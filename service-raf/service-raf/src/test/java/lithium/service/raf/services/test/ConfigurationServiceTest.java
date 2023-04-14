package lithium.service.raf.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import lithium.service.raf.data.entities.Configuration;
import lithium.service.raf.data.enums.AutoConvertPlayer;
import lithium.service.raf.data.objects.AutoConvertRequest;
import lithium.service.raf.data.objects.AutoConvertResult;
import lithium.service.raf.data.repositories.ConfigurationRepository;
import lithium.service.raf.services.ConfigurationService;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationServiceTest {

	@Mock
	private ConfigurationRepository configurationRepository;
	
	@InjectMocks
	private ConfigurationService configurationService;
	
	@Mock
	private Configuration configuration;
	
	private String domainName="default";
	
	@Test
	public void shouldFailIfDomainNameIsNull() {
		domainName=null;
		AutoConvertRequest request=null;
		AutoConvertResult result = configurationService.enableAutoConvertPlayer(domainName,request);
		assertEquals("Invalid domain name", result.getMessage());
	}
	
	@Test
	public void shouldFailIfAutoConvertRequestIsNull() {
		AutoConvertRequest request=null;
		AutoConvertResult result = configurationService.enableAutoConvertPlayer(domainName,request);
		assertEquals("Invalid Request", result.getMessage());
	}
	
	@Test
	public void shouldFailIfConfigurationNotFound() {
		AutoConvertRequest request=new AutoConvertRequest();
		request.setAutoConvertPlayer(AutoConvertPlayer.ENABLED);
		when(configurationRepository.findByDomainName(anyString())).thenReturn(null);
		AutoConvertResult result = configurationService.enableAutoConvertPlayer(domainName,request);
		assertEquals("Configuration for domain default not found", result.getMessage());
	}
	
	@Test
	public void shouldEnableAutoCovertPlayerIfPastConvertCriteria() {
		AutoConvertRequest request=new AutoConvertRequest();
		request.setAutoConvertPlayer(AutoConvertPlayer.ENABLED);
		when(configurationRepository.findByDomainName(domainName)).thenReturn(configuration);
		AutoConvertResult result = configurationService.enableAutoConvertPlayer(domainName,request);
		assertNotNull(result);
		assertEquals("ENABLED Auto Convert Player Successful", result.getMessage());
		verify(configurationRepository, times(1)).findByDomainName(anyString());
	}

}
