package lithium.service.kyc.provider.onfido;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.kyc.provider.onfido.config.ProviderConfigProperties;
import lithium.service.role.client.objects.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class ServiceKycOnfidoModuleInfo extends ModuleInfoAdapter {
    public ServiceKycOnfidoModuleInfo() {
        super();
        List<ProviderConfigProperty> properties= new ArrayList<>();

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.BASE_API_URL.getValue())
                .required(true)
                .tooltip("Base API url")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.API_TOKEN.getValue())
                .required(true)
                .tooltip("A unique number assigned to your account")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.REPORT_NAMES.getValue())
                .required(true)
                .tooltip("Array of strings describing reports requested for the check. https://documentation.onfido.com/#report-names-in-api")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.WEBHOOK_IDS.getValue())
                .required(false)
                .tooltip("Array of comma separated webhook_id used to send only environment related webhooks")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.MATCH_DOCUMENT_ADDRESS.getValue())
                .required(false)
                .tooltip("Match document address using internal mechanism")
                .dataType(Boolean.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.MATCH_FIRST_NAME.getValue())
                .required(false)
                .tooltip("Compare first name using internal mechanism. So that if the lithium 1st name is present in the string " +
                        "extracted from the document, and last name and DOB already matched, we will accept the document and verify the player")
                .dataType(Boolean.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.SUPPORTED_ISSUING_COUNTRIES.getValue())
                .required(false)
                .tooltip("Array of comma separated supported issuing countries used to avoid try match document address if document's " +
                        "issuing country not in supported list. If this parameter is empty it try to match any document. " +
                        "Country should be in ISO 3166-1 alpha-3 format, example: GBR,IRL,NLD" )
                .dataType(String.class)
                .version(1)
                .build());

        //Add the provider to moduleinfo
        addProvider(ProviderConfig.builder()
                .name(getModuleName())
                .type(ProviderConfig.ProviderType.KYC)
                .properties(properties)
                .build());

        Role.Category onfido = Role.Category.builder().name("KYC - Onfido").description("Onfido service ").build();
        addRole(Role.builder().category(onfido).name("Submit player document verification via Onfido").role("KYC_ONFIDO_VERIFY_DOCUMENTS").description("Upload player's documents and submit verification check via Onfido").build());

    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests()
                .antMatchers("/frontend/api/**").authenticated()
                .antMatchers("/backoffice/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'KYC_ONFIDO_VERIFY_DOCUMENTS')")
                .antMatchers("/webhook/**").permitAll();

    }
}
