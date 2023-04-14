package lithium.service.limit.services;

import lithium.service.limit.client.exceptions.Status422PlayerRestrictionExclusionException;
import lithium.service.limit.client.exceptions.Status403PlayerRestrictionDeniedException;
import lithium.service.limit.client.exceptions.Status409PlayerRestrictionConflictException;
import lithium.service.limit.client.objects.Access;
import lithium.service.limit.data.entities.Domain;
import lithium.service.limit.data.entities.DomainRestriction;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.Restriction;
import lithium.service.limit.data.entities.User;
import lithium.service.limit.data.entities.UserRestrictionSet;
import lithium.service.limit.data.repositories.UserRestrictionSetRepository;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.UserCategory;
import lithium.service.user.client.service.UserApiInternalClientService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UserRestrictionServiceTest {

    @Mock
    UserRestrictionSetRepository userRestrictionSetRepository;

    @Mock
    RestrictionService restrictionService;

    @Mock
    UserApiInternalClientService userApiInternalClientService;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    UserRestrictionService userRestrictionService;


    DomainRestrictionSet interventionBlock;
    DomainRestrictionSet playerBlock;

    @Test
    public void isCompsAllowedShouldBeFalseWhenPlayerCompsOptOutIsActive() {
        UserRestrictionSet set = UserRestrictionSet.builder()
                .set(playerBlock)
                .activeFrom(DateTime.now().toDate())
                .user(User.builder().guid("").build())
                .build();
        Access access = Access.builder().build();

        Mockito.when(userRestrictionSetRepository.findByUserGuid(Mockito.anyString())).thenReturn(Arrays.asList(set));
        userRestrictionService.checkCompsRestriction(set, access);

        Assert.assertTrue(!access.isCompsAllowed());
    }

    @Test
    public void isCompsAllowedShouldBeFalseWhenInterventionCompsBlockIsActive() {
        UserRestrictionSet set = UserRestrictionSet.builder()
                .set(interventionBlock)
                .user(User.builder().guid("").build())
                .activeFrom(DateTime.now().toDate())
                .build();
        Access access = Access.builder().build();
        userRestrictionService.checkCompsRestriction(set, access);

        Assert.assertTrue(!access.isCompsAllowed());
    }

    @Test
    public void isCompsSystemPlacedShouldBeFalseWhenPlayerCompsOptOutIsActive() {
        UserRestrictionSet set = UserRestrictionSet.builder()
                .set(playerBlock)
                .activeFrom(DateTime.now().toDate())
                .user(User.builder().guid("").build())
                .build();
        Access access = Access.builder().build();
        userRestrictionService.checkCompsRestriction(set, access);

        Assert.assertTrue(!access.isCompsSystemPlaced());
        Assert.assertTrue(!access.isCompsAllowed());
    }


    @Test
    public void isCompsSystemPlacedShouldBeTrueWhenInterventionCompsBlockIsActive() {
        UserRestrictionSet set = UserRestrictionSet.builder()
                .set(interventionBlock)
                .activeFrom(DateTime.now().toDate())
                .user(User.builder().guid("").build())
                .build();
        Access access = Access.builder().build();

        Mockito.when(userRestrictionSetRepository.findByUserGuid(Mockito.anyString())).thenReturn(Arrays.asList(set));
        userRestrictionService.checkCompsRestriction(set, access);

        Assert.assertTrue(access.isCompsSystemPlaced());
        Assert.assertTrue(!access.isCompsAllowed());
    }

    @Test
    public void shouldPlacePlayerRestrictionWhenInterventionCompsBlockIsNotPresent() throws Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException, Status422PlayerRestrictionExclusionException {
        Mockito.when(userRestrictionSetRepository.findByUserGuid(Mockito.anyString())).thenReturn(Arrays.asList());
        userRestrictionService.validateSystemRestrictionPlace(playerBlock, "livescore_wa/rivalani06");
    }

    @Test
    public void shouldPlaceRestrictionWhenPlayerIsNotExcluded() throws UserClientServiceFactoryException, UserNotFoundException {
        lithium.service.user.client.objects.User user = lithium.service.user.client.objects.User.builder()
                .guid("livescore_sa/rivalani")
                .userCategories(Arrays.asList(
                        UserCategory.builder()
                                .id(1L)
                                .build()
                ))
                .build();

        Mockito.when(userApiInternalClientService.getUserByGuid(Mockito.anyString())).thenReturn(user);

        Assert.assertFalse(userRestrictionService.isPlayerExcludedFromRestriction(playerBlock, "livescore_sa/rivalani"));
    }

    @Test
    public void shouldNotPlaceRestrictionWhenPlayerIsNotExcluded() throws UserClientServiceFactoryException, UserNotFoundException {
        lithium.service.user.client.objects.User user = lithium.service.user.client.objects.User.builder()
                .guid("livescore_sa/rivalani")
                .userCategories(Arrays.asList(
                        UserCategory.builder()
                                .id(1L)
                                .build()
                ))
                .build();
        playerBlock.setExcludeTagId(1L);

        Mockito.when(userApiInternalClientService.getUserByGuid(Mockito.anyString())).thenReturn(user);

        Assert.assertTrue(userRestrictionService.isPlayerExcludedFromRestriction(playerBlock, "livescore_sa/rivalani"));
    }

    @Test(expected = Status409PlayerRestrictionConflictException.class)
    public void shouldNotPlacePlayerRestrictionWhenActiveCompsRestrictionIsInterventionCompsBlock() throws Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException,Status422PlayerRestrictionExclusionException {
        UserRestrictionSet set = UserRestrictionSet.builder()
                .set(interventionBlock)
                .activeFrom(DateTime.now().toDate())
                .user(User.builder().guid("").build())
                .build();

        Mockito.when(userRestrictionSetRepository.findByUserGuid(Mockito.anyString())).thenReturn(Arrays.asList(set));

        userRestrictionService.validateSystemRestrictionPlace(playerBlock, "livescore_wa/rivalani06");
    }

    @Test(expected = Status409PlayerRestrictionConflictException.class)
    public void shouldNotPlacePlayerRestrictionWhenActiveCompsRestrictionIsPlayerCompsOptOut() throws Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException,Status422PlayerRestrictionExclusionException {
        UserRestrictionSet set = UserRestrictionSet.builder()
                .set(playerBlock)
                .activeFrom(DateTime.now().toDate())
                .user(User.builder().guid("").build())
                .build();

        Mockito.when(userRestrictionSetRepository.findByUserGuid(Mockito.anyString())).thenReturn(Arrays.asList(set));
        userRestrictionService.validateSystemRestrictionPlace(playerBlock, "livescore_wa/rivalani06");
    }

    @Test()
    public void shouldLiftPlayerRestrictionWhenActiveCompsRestrictionIsNotInterventionCompsBlock() throws Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException {
        UserRestrictionSet set = UserRestrictionSet.builder()
                .set(playerBlock)
                .activeFrom(DateTime.now().toDate())
                .user(User.builder().guid("").build())
                .build();

        Mockito.when(userRestrictionSetRepository.findByUserGuid(Mockito.anyString())).thenReturn(Arrays.asList(set));
        userRestrictionService.validateRestrictionLift(playerBlock, "livescore_wa/rivalani06");
    }

    @Test(expected = Status403PlayerRestrictionDeniedException.class)
    public void shouldNotLiftPlayerRestrictionWhenActiveCompsRestrictionIsInterventionCompsBlock() throws Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException {
        UserRestrictionSet set = UserRestrictionSet.builder()
                .set(interventionBlock)
                .activeFrom(DateTime.now().toDate())
                .user(User.builder().guid("").build())
                .build();

        Mockito.when(userRestrictionSetRepository.findByUserGuid(Mockito.anyString())).thenReturn(Arrays.asList(set));
        userRestrictionService.validateRestrictionLift(playerBlock, "livescore_wa/rivalani06");
    }

    @Test(expected = Status409PlayerRestrictionConflictException.class)
    public void shouldNotLiftPlayerRestrictionWhenNoActiveCompsRestrictionIsPresent() throws Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException {
        Mockito.when(userRestrictionSetRepository.findByUserGuid(Mockito.anyString())).thenReturn(Arrays.asList());
        userRestrictionService.validateRestrictionLift(playerBlock, "livescore_wa/rivalani06");

    }

    @Test
    public void isDomainRestrictionSystemCompsBlock() {
        Assert.assertTrue(userRestrictionService.isDomainRestrictionInterventionCompsBlock(interventionBlock));
        Assert.assertTrue(!userRestrictionService.isDomainRestrictionInterventionCompsBlock(playerBlock));
    }

    @Test
    public void isCompsRestriction() {
        Assert.assertTrue(userRestrictionService.isCompsRestriction(interventionBlock));
        Assert.assertTrue(userRestrictionService.isCompsRestriction(playerBlock));
    }

    @Test
    public void shouldFilterWithExistingUserRestrictionSets() throws UserClientServiceFactoryException, UserNotFoundException {
        List<UserRestrictionSet> userRestrictionSets = new ArrayList<>();
        List<DomainRestrictionSet> domainRestrictionSets = new ArrayList<>();

        userRestrictionSets.add(UserRestrictionSet.builder()
                .activeFrom(new Date())
                .set(playerBlock)
                .build());

        domainRestrictionSets.add(playerBlock);
        domainRestrictionSets.add(interventionBlock);

        Mockito.when(userApiInternalClientService.getUserByGuid(Mockito.anyString())).thenReturn(lithium.service.user.client.objects.User.builder()
                .build());
        Mockito.when(restrictionService.findByDomainNameAndEnabledTrue(Mockito.anyString())).thenReturn(domainRestrictionSets);
        Mockito.when(userRestrictionSetRepository.findByUserGuid(Mockito.anyString())).thenReturn(userRestrictionSets);

        List<DomainRestrictionSet> sets =  userRestrictionService.getEligibleRestrictionSetsForUser("livesdcore_sa/rivalani");

        Assert.assertEquals(1, sets.size());
    }

    @Test
    public void shouldFilterWithExistingUserRestrictionSetsAndTags() throws UserClientServiceFactoryException, UserNotFoundException {
        List<UserRestrictionSet> userRestrictionSets = new ArrayList<>();
        List<DomainRestrictionSet> domainRestrictionSets = new ArrayList<>();

        userRestrictionSets.add(UserRestrictionSet.builder()
                .activeFrom(new Date())
                .set(playerBlock)
                .build());

        interventionBlock.setExcludeTagId(1L);
        domainRestrictionSets.add(playerBlock);
        domainRestrictionSets.add(interventionBlock);

        lithium.service.user.client.objects.User user = lithium.service.user.client.objects.User.builder()
                .userCategories(Arrays.asList(UserCategory.builder().id(1L).build()))
                .build();

        Mockito.when(userApiInternalClientService.getUserByGuid(Mockito.anyString())).thenReturn(user);
        Mockito.when(restrictionService.findByDomainNameAndEnabledTrue(Mockito.anyString())).thenReturn(domainRestrictionSets);
        Mockito.when(userRestrictionSetRepository.findByUserGuid(Mockito.anyString())).thenReturn(userRestrictionSets);

        List<DomainRestrictionSet> sets =  userRestrictionService.getEligibleRestrictionSetsForUser("livesdcore_sa/rivalani");

        Assert.assertEquals(0, sets.size());
    }

    @Test
    public void shouldStillFilterWithUserRestrictionAfterFetchingUserFailed() throws UserClientServiceFactoryException, UserNotFoundException {
        List<UserRestrictionSet> userRestrictionSets = new ArrayList<>();
        List<DomainRestrictionSet> domainRestrictionSets = new ArrayList<>();

        userRestrictionSets.add(UserRestrictionSet.builder()
                .activeFrom(new Date())
                .set(playerBlock)
                .build());

        domainRestrictionSets.add(playerBlock);
        domainRestrictionSets.add(interventionBlock);

        Mockito.when(userApiInternalClientService.getUserByGuid(Mockito.anyString())).thenThrow(UserNotFoundException.class);
        Mockito.when(restrictionService.findByDomainNameAndEnabledTrue(Mockito.anyString())).thenReturn(domainRestrictionSets);
        Mockito.when(userRestrictionSetRepository.findByUserGuid(Mockito.anyString())).thenReturn(userRestrictionSets);

        List<DomainRestrictionSet> sets =  userRestrictionService.getEligibleRestrictionSetsForUser("livesdcore_sa/rivalani");

        Assert.assertEquals(1, sets.size());
    }

    @Test
    public void shouldNotFilterWhenDomainRestrictionSetHasNoExcludeTagId() throws UserClientServiceFactoryException, UserNotFoundException {
        List<UserRestrictionSet> userRestrictionSets = new ArrayList<>();
        List<DomainRestrictionSet> domainRestrictionSets = new ArrayList<>();

        domainRestrictionSets.add(playerBlock);
        domainRestrictionSets.add(interventionBlock);

        lithium.service.user.client.objects.User user = lithium.service.user.client.objects.User.builder()
                .userCategories(Arrays.asList(UserCategory.builder().id(1L).build()))
                .build();

        Mockito.when(userApiInternalClientService.getUserByGuid(Mockito.anyString())).thenReturn(user);
        Mockito.when(restrictionService.findByDomainNameAndEnabledTrue(Mockito.anyString())).thenReturn(domainRestrictionSets);
        Mockito.when(userRestrictionSetRepository.findByUserGuid(Mockito.anyString())).thenReturn(userRestrictionSets);

        List<DomainRestrictionSet> sets =  userRestrictionService.getEligibleRestrictionSetsForUser("livesdcore_sa/rivalani");

        Assert.assertEquals(2, sets.size());
    }

    @Test
    public void shouldNotFilterWithTagsWhenUserHasNoMatchingTags() throws UserClientServiceFactoryException, UserNotFoundException {
        List<UserRestrictionSet> userRestrictionSets = new ArrayList<>();
        List<DomainRestrictionSet> domainRestrictionSets = new ArrayList<>();

        interventionBlock.setExcludeTagId(1L);
        domainRestrictionSets.add(playerBlock);
        domainRestrictionSets.add(interventionBlock);

        lithium.service.user.client.objects.User user = lithium.service.user.client.objects.User.builder()
                .build();

        Mockito.when(userApiInternalClientService.getUserByGuid(Mockito.anyString())).thenReturn(user);
        Mockito.when(restrictionService.findByDomainNameAndEnabledTrue(Mockito.anyString())).thenReturn(domainRestrictionSets);
        Mockito.when(userRestrictionSetRepository.findByUserGuid(Mockito.anyString())).thenReturn(userRestrictionSets);

        List<DomainRestrictionSet> sets =  userRestrictionService.getEligibleRestrictionSetsForUser("livesdcore_sa/rivalani");

        Assert.assertEquals(2, sets.size());
    }

    @Before
    public void setup() {
        Domain domain = Domain.builder().name("livescore_sa").build();

        Restriction restriction =Restriction.builder()
                .code("COMPS")
                .build();

        DomainRestriction domainRestriction = DomainRestriction.builder()
                .restriction(restriction)
                .enabled(true)
                .build();

        interventionBlock = DomainRestrictionSet.builder()
                .restrictions(Arrays.asList(domainRestriction))
                .name("Intervention Comps Block")
                .systemRestriction(true)
                .domain(domain)
                .build();

        playerBlock = DomainRestrictionSet.builder()
                .restrictions(Arrays.asList(domainRestriction))
                .name("Player Comps Opt-Out")
                .systemRestriction(true)
                .domain(domain)
                .build();
    }
}
