package lithium.service.kyc.provider.paystack.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.kyc.provider.exceptions.Status406InvalidVerificationNumberException;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.exceptions.Status520KycProviderEndpointException;
import lithium.service.kyc.provider.paystack.config.BrandsConfigurationBrand;
import lithium.service.kyc.provider.paystack.data.objects.BvnResolveResponse;
import lithium.service.kyc.provider.paystack.services.ApiService;
import lithium.service.kyc.provider.paystack.services.PaystackService;
import lithium.service.kyc.provider.paystack.services.RestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class PaystackServiceTest {

    @InjectMocks
    private PaystackService paystackService;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ApiService apiService;

    @Mock
    private RestService restService;

    @Mock
    private BrandsConfigurationBrand brandsConfigurationBrand;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldFailIfBvnNumberIsNull() throws Status520KycProviderEndpointException, Status512ProviderNotConfiguredException, Status406InvalidVerificationNumberException {
        expectedException.expect(Status406InvalidVerificationNumberException.class);
        expectedException.expectMessage("Invalid Bvn Number");
        String bvnNumber = null;
        String userGuid = null;
        paystackService.bvnResolveBvn(bvnNumber, userGuid);
    }

    @Test
    public void shouldFailIfBvnNumberIsEmpty() throws Status520KycProviderEndpointException, Status512ProviderNotConfiguredException, Status406InvalidVerificationNumberException {
        expectedException.expect(Status406InvalidVerificationNumberException.class);
        expectedException.expectMessage("Invalid Bvn Number");
        String bvnNumber = "";
        String userGuid = null;
        paystackService.bvnResolveBvn(bvnNumber, userGuid);
    }

    @Test
    public void shouldFailIfBvnIsNonNumeric() throws Status520KycProviderEndpointException, Status512ProviderNotConfiguredException, Status406InvalidVerificationNumberException {
        expectedException.expect(Status406InvalidVerificationNumberException.class);
        expectedException.expectMessage("Invalid Bvn Number");
        String bvnNumber = "1234567890T";
        String userGuid = null;
        paystackService.bvnResolveBvn(bvnNumber, userGuid);
    }

    @Test
    public void shouldFailIfBvnLengthIsNotEqualTo11() throws Status520KycProviderEndpointException, Status512ProviderNotConfiguredException, Status406InvalidVerificationNumberException {
        expectedException.expect(Status406InvalidVerificationNumberException.class);
        expectedException.expectMessage("Invalid Bvn Number");
        when(brandsConfigurationBrand.getBvnLength()).thenReturn(11);
        when(apiService.getBrandsConfigurationBrand(anyString())).thenReturn(brandsConfigurationBrand);
        String bvnNumber = "1234567890";
        String userGuid = "domainName/12345";
        paystackService.bvnResolveBvn(bvnNumber, userGuid);
    }

    @Test
    public void shouldThrowExceptionIfResponseIsNull() throws Status520KycProviderEndpointException, Status512ProviderNotConfiguredException, Status406InvalidVerificationNumberException {
        expectedException.expect(Status520KycProviderEndpointException.class);
        when(restService.restTemplate(anyInt(), anyInt(), anyInt())).thenReturn(restTemplate);
        trainBrandsConfigurationBrandMock();
        String userGuid = "domainName/12345";

        paystackService.bvnResolveBvn("12345678901", userGuid);
    }

    @Test
    public void shouldThrowExceptionIfResponseBodyIsNull() throws Status520KycProviderEndpointException, Status512ProviderNotConfiguredException, Status406InvalidVerificationNumberException {
        expectedException.expect(Status520KycProviderEndpointException.class);
        when(restService.restTemplate(anyInt(), anyInt(), anyInt())).thenReturn(restTemplate);
        trainBrandsConfigurationBrandMock();
        ResponseEntity<BvnResolveResponse> expectedResponse = new ResponseEntity<>(null, HttpStatus.OK);
        String userGuid = "domainName/123455";
        paystackService.bvnResolveBvn("12345678901", userGuid);
    }

    private void trainBrandsConfigurationBrandMock() throws Status512ProviderNotConfiguredException {
        when(apiService.getBrandsConfigurationBrand(anyString())).thenReturn(brandsConfigurationBrand);
        when(brandsConfigurationBrand.getApiKey()).thenReturn("apiKey");
        when(brandsConfigurationBrand.getPlatformUrl()).thenReturn("url");
        when(brandsConfigurationBrand.getBvnLength()).thenReturn(11);
        when(brandsConfigurationBrand.getConnectTimeout()).thenReturn(1000);
        when(brandsConfigurationBrand.getConnectionRequestTimeout()).thenReturn(1000);
        when(brandsConfigurationBrand.getSocketTimeout()).thenReturn(1000);
    }

    private BvnResolveResponse mock() {
		ObjectMapper mapper = new ObjectMapper();
		String responseString = "{\"status\":\"true\",\"message\":\"BVN resolved\",\"data\":{\"dob\":\"26-Sep-97\",\"mobile\":\"08149120887\",\"first_name\":\"VICTOR\",\"last_name\":\"AGBO\",\"formatted_dob\":\"1997-09-26\"},\"meta\":{\"calls_this_month\":\"7\",\"free_calls_left\":\"3\"}}";
		try {
			return mapper.readValue(responseString, BvnResolveResponse.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
