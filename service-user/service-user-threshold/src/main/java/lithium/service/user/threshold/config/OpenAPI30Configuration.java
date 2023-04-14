package lithium.service.user.threshold.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme( name = "LithiumTokenUtil", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer" )
@SecurityScheme( name = "LithiumSystemToken", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer" )
@OpenAPIDefinition( info = @Info( title = "Player Threshold Service", version = "0.0.1", description = "This service will track events to notify and report on thresholds that were triggered." ), tags = {
    @Tag( name = "Loss Limits Setup", description = "These endpoints are meant to configure specific loss limit thresholds." ),
    @Tag( name = "Player Thresholds", description = "Endpoints meant to retrieve recorded threshold breaches and player settings." )}, servers = {
    @Server( url = "http://localhost:9000", description = "Local Gateway." ),
    @Server( url = "https://gateway.lithium-develop.ls-g.net", description = "Livescore Lithium Develop" ),
    @Server( url = "https://gateway.lithium-qa.ls-g.net", description = "Livescore Lithium QA" ),
    @Server( url = "https://gateway.lithium-staging.ls-g.net/", description = "Livescore Lithium Staging" ),
    @Server( url = "https://gateway.lithium-prod.ls-g.net", description = "Livescore Lithium Production" )} )
public class OpenAPI30Configuration {

}
