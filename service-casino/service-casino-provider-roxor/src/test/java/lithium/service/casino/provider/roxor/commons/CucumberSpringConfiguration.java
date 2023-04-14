package lithium.service.casino.provider.roxor.commons;

import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import lithium.service.casino.provider.roxor.ServiceCasinoProviderRoxorApplication;
import lithium.service.casino.provider.roxor.api.controllers.GamePlayController;
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.casino.provider.roxor.services.GamePlayService;
import lithium.service.domain.client.CachingDomainClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@Slf4j
//@SpringBootTest
@ActiveProfiles( "test" )
//@CucumberContextConfiguration
//@ContextConfiguration( classes = CucumberSpringConfiguration.class )
//@ContextConfiguration( classes = ServiceCasinoProviderRoxorApplication.class, loader = SpringBootContextLoader.class )
public class CucumberSpringConfiguration {

  @Autowired
  private GamePlayService gamePlayService;
  @MockBean
  private ProviderConfigService providerConfigService;
  @MockBean
  private CachingDomainClientService cachingDomainClientService;
  @Autowired
  private GamePlayController gamePlayController;

  @Before
  public void setup() {
    gamePlayController.setGamePlayService(gamePlayService);
    gamePlayController.setProviderConfigService(providerConfigService);
    gamePlayController.setCachingDomainClientService(cachingDomainClientService);
    log.info("-------------- Spring Context Initialized For Executing Cucumber Tests --------------");
  }
}