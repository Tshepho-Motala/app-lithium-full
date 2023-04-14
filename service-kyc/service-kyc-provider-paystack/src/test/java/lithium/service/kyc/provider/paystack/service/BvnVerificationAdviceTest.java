package lithium.service.kyc.provider.paystack.service;

import lithium.client.changelog.ChangeLogService;
import lithium.service.kyc.provider.exceptions.Status424KycVerificationUnsuccessfulException;
import lithium.service.kyc.provider.exceptions.Status425IllegalUserStateException;
import lithium.service.kyc.provider.exceptions.Status426PlayerUnderAgeException;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.paystack.data.objects.BvnResolveData;
import lithium.service.kyc.provider.paystack.data.objects.BvnResolveResponse;
import lithium.service.kyc.provider.paystack.services.ApiService;
import lithium.service.kyc.provider.paystack.services.BvnVerificationAdvice;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class BvnVerificationAdviceTest {

    @InjectMocks
    private BvnVerificationAdvice bvnVerificationAdvice;

    @Mock
    private ApiService apiService;

    @Mock
    private BvnResolveResponse bvnResolveResponse;

    @Mock
    private BvnResolveData bvnResolveData;

    @Mock
    private User user;

    @Mock
    private ChangeLogService changeLogService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void trainMocks() {
        when(bvnResolveResponse.getData()).thenReturn(bvnResolveData);
    }

    @Test
    public void shouldThrow_if_UNVERIFIED() throws ParseException, Status424KycVerificationUnsuccessfulException {
        when(apiService.getUser(anyString())).thenReturn(user);
        expectedException.expect(Status424KycVerificationUnsuccessfulException.class);
        VerificationStatus advice = bvnVerificationAdvice.advice("domainName", bvnResolveResponse);
    }

    @Test
    public void shouldReturn_EXTERNALLY_VERIFIED_IfValid() throws ParseException, Status425IllegalUserStateException, Status424KycVerificationUnsuccessfulException {
        when(apiService.getUser(anyString())).thenReturn(user);
        trainValidUserMock();
        trainValidReponseMock();
        VerificationStatus advice = bvnVerificationAdvice.advice("domainName", bvnResolveResponse);
        log.debug("advice {}",advice);
        assertEquals(VerificationStatus.EXTERNALLY_VERIFIED, advice);
    }

    @Test
    public void shouldThrow_if_UNDER_AGE() throws ParseException, Status425IllegalUserStateException, Status424KycVerificationUnsuccessfulException, Status426PlayerUnderAgeException, Status512ProviderNotConfiguredException, UserClientServiceFactoryException {
        expectedException.expect(Status426PlayerUnderAgeException.class);
        when(apiService.getUser(anyString())).thenReturn(user);
        DateTime yearLessThan18 = DateTime.now().minusYears(16);
        when(user.getDateOfBirth()).thenReturn(yearLessThan18);
        VerificationStatus advice = bvnVerificationAdvice.currentStatus("userGuid");
        //log.debug("advice {}",advice);
        //assertEquals(VerificationStatus.UNDER_AGE, advice);
    }

    @Test
    public void shouldThrow_if_UNVERIFIED_when_DobDoesntMatch() throws ParseException, Status424KycVerificationUnsuccessfulException {
        expectedException.expect(Status424KycVerificationUnsuccessfulException.class);
        when(apiService.getUser(anyString())).thenReturn(user);
        trainValidUserMock();
        trainValidReponseMock();
        when(user.getDobYear()).thenReturn(LocalDate.now().minusYears(19).getYear());
        VerificationStatus advice = bvnVerificationAdvice.advice("domainName", bvnResolveResponse);
        //log.debug("advice {}",advice);
        //assertEquals(VerificationStatus.UNVERIFIED, advice);
    }

    @Test
    public void shouldThrow_UNVERIFIED_ifFirstNameDoesntMatch() throws ParseException, Status424KycVerificationUnsuccessfulException {
        expectedException.expect(Status424KycVerificationUnsuccessfulException.class);
        when(apiService.getUser(anyString())).thenReturn(user);
        trainValidUserMock();
        trainValidReponseMock();
        when(user.getLastName()).thenReturn("Paul");
        VerificationStatus advice = bvnVerificationAdvice.advice("domainName", bvnResolveResponse);
        //log.debug("advice {}",advice);
        //assertEquals(VerificationStatus.UNVERIFIED, advice);
    }

    private void trainValidUserMock(){
        when(user.getFirstName()).thenReturn("John");
        when(user.getLastName()).thenReturn("Doe");
        when(user.getDobDay()).thenReturn(01);
        when(user.getDobMonth()).thenReturn(01);
        when(user.getDobYear()).thenReturn(LocalDate.now().minusYears(20).getYear());
    }

    private void trainValidReponseMock(){
        LocalDate dob = LocalDate.now().minusYears(20);
        when(bvnResolveResponse.getStatus()).thenReturn("true");
        when(bvnResolveResponse.getData()).thenReturn(bvnResolveData);
        when(bvnResolveData.getFormattedDob()).thenReturn(dob.getYear()+ "-01-01");
        when(bvnResolveData.getFirstName()).thenReturn("John");
        when(bvnResolveData.getLastName()).thenReturn("Doe");
    }
}
