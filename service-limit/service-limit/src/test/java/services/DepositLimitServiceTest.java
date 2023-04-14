package services;

import lithium.client.changelog.ChangeLogService;
import lithium.service.client.objects.Granularity;
import lithium.service.limit.client.LimitType;
import lithium.service.limit.client.exceptions.Status499EmptySupposedDepositLimitException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.PlayerLimitFE;
import lithium.service.limit.data.entities.PlayerLimit;
import lithium.service.limit.data.repositories.PlayerLimitHistoryRepository;
import lithium.service.limit.data.repositories.PlayerLimitRepository;
import lithium.service.limit.services.DepositLimitService;
import lithium.service.limit.services.ExternalUserService;
import lithium.service.limit.services.PubSubUserAccountChangeProxy;
import lithium.service.user.client.objects.User;
import lithium.tokens.JWTUser;
import lithium.tokens.LithiumTokenUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DepositLimitServiceTest {

    private Logger logger = LoggerFactory.getLogger(DepositLimitServiceTest.class);
    @Mock
    MessageSource messageSource;

    @Mock
    PubSubUserAccountChangeProxy pubSubUserAccountChangeProxy;

    @Mock
    ExternalUserService externalUserService;

    @Spy
    ChangeLogService changeLogService;

    @Mock
    PlayerLimitRepository playerLimitRepository;

    @Mock
    PlayerLimitHistoryRepository playerLimitHistoryRepository;

    @Mock
    Logger log;

    @InjectMocks
    DepositLimitService depositLimitService;

    LithiumTokenUtil util;
    JWTUser jwtUser;
    User user;

    PlayerLimit plCurrent;
    PlayerLimit plSupposed;

    @Test(expected = Status499EmptySupposedDepositLimitException.class)
    public void ShouldFailWhenSupposedLimitIsNull() throws Status499EmptySupposedDepositLimitException, Status500LimitInternalSystemClientException {
        depositLimitService.proceedSupposedLimit("livescore_sa/simon", Granularity.GRANULARITY_DAY, true, Locale.US, util);
    }

    @Test
    public void ShouldDeleteSupposedLimitWhenActionIsFalse() throws Status499EmptySupposedDepositLimitException, Status500LimitInternalSystemClientException {

        Mockito.when(playerLimitRepository.findByPlayerGuidAndGranularityAndType(jwtUser.getGuid(), Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_DEPOSIT_LIMIT_SUPPOSED.type()))
                .thenReturn(plSupposed);

        PlayerLimitFE limit = depositLimitService.proceedSupposedLimit(jwtUser.getGuid(), Granularity.GRANULARITY_DAY, false, Locale.US, util);

        Mockito.verify(playerLimitRepository, Mockito.times(1)).deleteByPlayerGuidAndGranularityAndType(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt());
        assertEquals(limit.getType(), "TYPE_DEPOSIT_LIMIT");

    }

    @Test
    public void ShouldDeletePLayerCurrentLimitIfSupposedLimitIsZero() throws Status499EmptySupposedDepositLimitException, Status500LimitInternalSystemClientException {
        /*plSupposed.setAmount(0);

        Mockito.when(playerLimitRepository.findByPlayerGuidAndGranularityAndType(jwtUser.getGuid(), Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_DEPOSIT_LIMIT_SUPPOSED.type()))
                .thenReturn(plSupposed);

        PlayerLimitFE limit = depositLimitService.proceedSupposedLimit(jwtUser.getGuid(), Granularity.GRANULARITY_DAY, true, Locale.US, util);

        Mockito.verify(playerLimitRepository, Mockito.times(2)).deleteByPlayerGuidAndGranularityAndType(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt());
        assertEquals(limit.getType(), "TYPE_DEPOSIT_LIMIT_SUPPOSED");*/
    }

    @Test
    public void ShouldUpdateCurrentPlayerLimitWhenSupposedLimitIsGreaterThanZero() throws Status499EmptySupposedDepositLimitException, Status500LimitInternalSystemClientException {

        /*plSupposed.setAmount(200);

        Mockito.when(playerLimitRepository.findByPlayerGuidAndGranularityAndType(jwtUser.getGuid(), Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_DEPOSIT_LIMIT_SUPPOSED.type()))
                .thenReturn(plSupposed);

        PlayerLimitFE limit = depositLimitService.proceedSupposedLimit(jwtUser.getGuid(), Granularity.GRANULARITY_DAY, true, Locale.US, util);

        Mockito.verify(playerLimitRepository, Mockito.times(1)).deleteByPlayerGuidAndGranularityAndType(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt());
        Mockito.verify(playerLimitRepository, Mockito.times(1)).save(Mockito.any(PlayerLimit.class));

        assertEquals(limit.getType(), "TYPE_DEPOSIT_LIMIT_SUPPOSED");*/
    }


    @Before
    public void setup() throws Exception {

        jwtUser = JWTUser.builder()
                .username("rivalani01")
                .domainName("livescore_sa")
                .firstName("Rivalani")
                .lastName("Hlengani")
                .email("rivalani.hlengani@wonderlabz.co.za")
                .guid("livescore_sa/rivalani01")
                .id(20210501L)
                .build();

        user = User.builder()
                .guid(jwtUser.getGuid())
                .firstName(jwtUser.getFirstName())
                .lastName(jwtUser.getLastName())
                .username(jwtUser.getUsername())
                .id(jwtUser.getId())
                .build();

        util = Mockito.mock(LithiumTokenUtil.class);
        util.setJwtUser(jwtUser);

        plCurrent = PlayerLimit.builder()
                .playerGuid(jwtUser.getGuid())
                .granularity(Granularity.GRANULARITY_DAY.granularity())
                .type(LimitType.TYPE_DEPOSIT_LIMIT.type())
                .build();

        plSupposed = PlayerLimit.builder()
                .playerGuid(jwtUser.getGuid())
                .granularity(Granularity.GRANULARITY_DAY.granularity())
                .type(LimitType.TYPE_DEPOSIT_LIMIT_SUPPOSED.type())
                .build();


        Mockito.when(playerLimitRepository.findByPlayerGuidAndGranularityAndType(jwtUser.getGuid(), Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_DEPOSIT_LIMIT.type()))
                .thenReturn(plCurrent);


//        Mockito.when(externalUserService.getUsersByGuids(Mockito.anyList())).thenReturn(Arrays.asList(user));
        Mockito.when(messageSource.getMessage(Mockito.anyString(), Mockito.any(), Mockito.any(Locale.class))).thenReturn("Hello World");
        Mockito.when(externalUserService.findByGuid(Mockito.anyString())).thenReturn(user);
//        Mockito.when(playerLimitRepository.save(Mockito.any(PlayerLimit.class))).thenReturn(plCurrent);

/*        Mockito.doNothing().when(changeLogService).registerChangesWithDomainAndFullName(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyString(),
                Mockito.anyString(),Mockito.anyString(), Mockito.anyList(),
                Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString());*/
    }

}
