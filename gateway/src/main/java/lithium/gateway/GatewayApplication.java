package lithium.gateway;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication
@EnableHypermediaSupport(type = HypermediaType.HAL)
//@EnableConfigurationProperties( UriConfiguration.class )
@OpenAPIDefinition( info = @Info( title = "Gateway API", version = "3.0", description = "Documentation Gateway API v3.0" ) )
public class GatewayApplication {

  public static void main (String[] args) {
    SpringApplication.run(GatewayApplication.class, args);
  }

}