package lithium.service.mock.gamstop.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerDocumentationConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {

        return new OpenAPI()
                .info(new Info().title("gamstop-single")
                        .description("Search for person")
                        .version("v2.0.0")
                        .license(new License().url(("http://unlicense.org")))
                        .termsOfService("")
                        .contact(new Contact().url("support@gamstop.co.uk")));
    }
}
