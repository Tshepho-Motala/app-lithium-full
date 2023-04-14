package lithium.service.raf.services.test;

import lithium.service.notifications.client.stream.NotificationStream;
import lithium.service.raf.data.entities.Configuration;
import lithium.service.raf.data.entities.Domain;
import lithium.service.raf.data.entities.Referral;
import lithium.service.raf.data.entities.Referrer;
import lithium.service.raf.data.enums.AutoConvertPlayer;
import lithium.service.raf.data.repositories.ReferralRepository;
import lithium.service.raf.enums.RAFConversionType;
import lithium.service.raf.enums.ReferralConversionStatus;
import lithium.service.user.client.objects.User;
import lithium.service.xp.client.objects.Level;
import lithium.service.raf.services.ExternalUserService;
import lithium.service.raf.services.ReferralService;
import lithium.service.raf.services.DomainService;
import lithium.service.raf.services.ReferrerService;
import lithium.service.raf.services.ConfigurationService;
import lithium.service.raf.services.ExternalXPService;
import lithium.service.raf.services.BonusService;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class ReferralServiceTest {
    @InjectMocks
    private ReferralService referralService;

    @Mock
    private ReferralRepository referralRepository;

    @Mock
    private ExternalUserService externalUserService;

    @Mock
    private User user;

    @Mock
    private DomainService domainService;

    @Mock
    private ReferrerService referrerService;

    @Mock
    private Domain domain;

    @Mock
    private ExternalXPService externalXPService;

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private BonusService bonusService;

    @Mock
    private NotificationStream notificationStream;

    @Test
    public void shouldReturnAlreadyReferredIfAlreadyExist() throws Exception {
        String referrerGuid = null;
        String playerGuid = "ggmvrlcad/legame";
        Referral referral=new Referral();
        when(referralRepository.findByPlayerGuid(playerGuid)).thenReturn(referral);
        ReferralConversionStatus result =referralService.addReferralAfterSignUp(referrerGuid,playerGuid);
        assertEquals(ReferralConversionStatus.ALREADY_REFERRED,result);
    }

    @Test
    public void shouldRejectIfDisableLevel() throws Exception {

        String referrerGuid = "ggmvrlcad/Arsenal2019";
        String playerGuid = "ggmvrlcad/legame";
        Configuration configuration=new Configuration();
        configuration.setConversionType(RAFConversionType.XP_LEVEL);
        configuration.setAutoConvertPlayer(AutoConvertPlayer.DISABLED);
        configuration.setConversionXpLevel(2);
        Level level=new Level();
        level.setNumber(2);
        Referrer referrer=new Referrer();
        referrer.setPlayerGuid(playerGuid);

        Referral referral=new Referral();
        referral.setReferrer(referrer);
        referral.setDomain(domain);
        when(domain.getName()).thenReturn("ggmvrlcad");
        when(referralRepository.save(any(Referral.class))).thenReturn(referral);
        when(referrerService.findOrCreate(referrerGuid)).thenReturn(referrer);
        when(externalXPService.getUserLevel(playerGuid,domain.getName())).thenReturn(level);
        when(externalUserService.getExternalUser(referrerGuid)).thenReturn(user);
        when(configurationService.findOrCreate(any())).thenReturn(configuration);
        when(domainService.findOrCreate(domain.getName())).thenReturn(domain);
        ReferralConversionStatus result=referralService.addReferralAfterSignUp(referrerGuid,playerGuid);
        assertEquals(ReferralConversionStatus.PASSED_CONVERSION_CRITERIA,result);

    }

    @Test
    public void shouldAddReferral() throws Exception {
        String referrerGuid = "ggmvrlcad/Arsenal2019";
        String playerGuid = "ggmvrlcad/legame";
        Configuration configuration=new Configuration();
        configuration.setConversionType(RAFConversionType.XP_LEVEL);
        configuration.setAutoConvertPlayer(AutoConvertPlayer.ENABLED);
        configuration.setConversionXpLevel(2);
        Level level=new Level();
        level.setNumber(2);
        Referrer referrer=new Referrer();
        referrer.setPlayerGuid(playerGuid);

        Referral referral=new Referral();
        referral.setReferrer(referrer);
        referral.setDomain(domain);
        when(domain.getName()).thenReturn("ggmvrlcad");
        when(referralRepository.save(any(Referral.class))).thenReturn(referral);
        when(referrerService.findOrCreate(referrerGuid)).thenReturn(referrer);
        when(externalXPService.getUserLevel(playerGuid,domain.getName())).thenReturn(level);
        when(externalUserService.getExternalUser(referrerGuid)).thenReturn(user);
        when(configurationService.findOrCreate(any())).thenReturn(configuration);
        when(domainService.findOrCreate(domain.getName())).thenReturn(domain);
        ReferralConversionStatus result=referralService.addReferralAfterSignUp(referrerGuid,playerGuid);
        assertEquals(ReferralConversionStatus.SUCCESS_REFERRAL_AND_CONVERTED,result);
    }

}
