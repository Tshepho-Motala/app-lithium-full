package lithium.service.promo.services;


//public class UserPromotionServiceTest {
//
//    @Mock
//    UserPromotionRepository userMissionRepository;
//
//    @Mock
//    PeriodService periodService;
//
//
//    Logger log = LoggerFactory.getLogger(UserPromotionService.class);
//
//    @Mock
//    UserPromotionService self;
//
//    @InjectMocks
//    UserPromotionService userPromotionService;
//
//
//    PromoContext context;
//
//    Promotion firstPromotion;
//    Promotion secondPromotion;
//
//    PromotionRevision firstRevision;
//    PromotionRevision secondRevision;
//
//    ZoneId zoneId;
//
//    @Before
//    public void setup() throws ReflectiveOperationException {
//        setFinalStaticField(UserPromotionService.class, "log", log);
//        zoneId = ZoneId.of("Africa/Johannesburg");
//
//        context = PromoContext.builder()
//                .user(User.builder().guid("livescore_sa/rivalani03052021").build())
//                .promotions(new ArrayList<>())
//                .userZoneId(zoneId)
//                .userZonedDateTime(ZonedDateTime.now(zoneId))
//                .build();
//
//        firstPromotion = Promotion.builder()
//                .id(100L)
//                .build();
//
//        secondPromotion = Promotion.builder()
//                .id(200L)
//                .build();
//
//        firstRevision = PromotionRevision.builder()
//                .id(1000L)
//                // // .maxRedeemable(2)
//                // .granularityStartOffset(1)
//                // .duration(1)
//                // .sequenceNumber(1)
//               // .challenges(new ArrayList<>())
//                .promotion(firstPromotion)
//                .xpLevel(0)
//                .build();
//
//        secondRevision = PromotionRevision.builder()
//                .id(2000L)
//                // // .maxRedeemable(1)
//                // .sequenceNumber(2)
//               // .challenges(new ArrayList<>())
//                // .duration(1)
//                // .granularityBreak(true)
//                .promotion(secondPromotion)
//                .xpLevel(0)
//                .build();
//    }
//
//    private static void setFinalStaticField(Class<?> clazz, String fieldName, Object value)
//            throws ReflectiveOperationException {
//
//        Field field = clazz.getDeclaredField(fieldName);
//        field.setAccessible(true);
//
//        Field modifiers = Field.class.getDeclaredField("modifiers");
//        modifiers.setAccessible(true);
//        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
//
//        field.set(null, value);
//    }
//
//    @Test
//    public void shouldParticipateInMissionWhenPreviousSequenceHasBeenCompleted()
//    {
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotion(Mockito.any(), Mockito.any(Promotion.class)))
//                        .thenReturn(Arrays.asList(
//                                UserPromotion.builder()
//                                        .promotionComplete(true)
//                                        .build()
//                        ));
//
//        firstPromotion.setCurrent(PromotionRevision.builder()
//                        .id(1000L)
//                        // .maxRedeemable(1)
//                        // .duration(1)
//                        // .maxRedeemableGranularity(Granularity.GRANULARITY_DAY.granularity())
//                        // .sequenceNumber(1)
//                .build());
//
//        secondPromotion.setCurrent(PromotionRevision.builder()
//                        .id(2000L)
//                        // .maxRedeemable(1)
//                        // .maxRedeemableGranularity(Granularity.GRANULARITY_DAY.granularity())
//                        // .sequenceNumber(2)
//                                .build());
//
//        context.setPromotions(Arrays.asList(firstPromotion, secondPromotion));
//
////        Assert.assertTrue(userPromotionService.completedPreviousSequence(context, secondPromotion));
//    }
//
//    @Test
//    public void shouldNotParticipateInMissionWhenPreviousSequenceHasNotBeenCompleted()
//    {
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotion(Mockito.any(), Mockito.any(Promotion.class)))
//                .thenReturn(Arrays.asList(
//                        UserPromotion.builder()
//                                .promotionComplete(false)
//                                .build()
//                ));
//
//        firstPromotion.setCurrent(PromotionRevision.builder()
//                .id(1000L)
//                // .maxRedeemable(1)
//                // .maxRedeemableGranularity(Granularity.GRANULARITY_DAY.granularity())
//                // .sequenceNumber(1)
//                .build());
//
//        secondPromotion.setCurrent(PromotionRevision.builder()
//                .id(2000L)
//                // .maxRedeemable(1)
//                // .maxRedeemableGranularity(Granularity.GRANULARITY_DAY.granularity())
//                // .sequenceNumber(2)
//                .build());
//
//        context.setPromotions(Arrays.asList(firstPromotion, secondPromotion));
//
////        Assert.assertFalse(userPromotionService.completedPreviousSequence(context, secondPromotion));
//    }
//
//    @Test
//    public void shouldParticipateInMissionWhenAllUserMissionsForThePreviousSequenceHaveBeenCompletedForTheGranularityDay()
//    {
//        int granurality = Granularity.GRANULARITY_DAY.granularity();
//
//        Period period = Period.builder()
//                .granularity(granurality)
//                .build();
//
//        PromotionRevision firstRevision = PromotionRevision.builder()
//                .id(1000L)
//                // .maxRedeemable(1)
//                // .maxRedeemableGranularity(granurality)
//                // .duration(1)
//                // .sequenceNumber(1)
//                .build();
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotionAndPeriod(Mockito.any(), Mockito.any(Promotion.class), Mockito.any(Period.class)))
//                .thenReturn(Arrays.asList(
//                        UserPromotion.builder()
//                                .promotionComplete(true)
//                                .period(period)
//                                .started(LocalDateTime.now(zoneId).minusDays(1))
//                                .promotionRevision(firstRevision)
//                                .build()
//                ));
//
//        firstPromotion.setCurrent(firstRevision);
//
//        secondPromotion.setCurrent(PromotionRevision.builder()
//                .id(2000L)
//                // .maxRedeemableGranularity(granurality)
//                // .maxRedeemable(2)
//                // .sequenceNumber(2)
//                .build());
//
//        context.setPromotions(Arrays.asList(firstPromotion, secondPromotion));
//
////        Assert.assertTrue(userPromotionService.completedPreviousGranularitySequence(context, secondPromotion, period));
//    }
//
//    @Test
//    public void shouldNotParticipateInMissionWhenAllUserMissionsForThePreviousSequenceHaveNotBeenCompletedForTheGranularityDay()
//    {
//
//        int granularity = Granularity.GRANULARITY_YEAR.granularity();
//
//        Period period = Period.builder()
//                .granularity(granularity)
//                .build();
//
//        PromotionRevision firstRevision = PromotionRevision.builder()
//                .id(1000L)
//                // .maxRedeemable(1)
//                // .maxRedeemableGranularity(granularity)
//                // .duration(1)
//                // .sequenceNumber(1)
//                // .granularityStartOffset(3)
//                .build();
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotionAndPeriod(Mockito.any(), Mockito.any(Promotion.class), Mockito.any(Period.class)))
//                .thenReturn(Arrays.asList(
//                        UserPromotion.builder()
//                                .promotionComplete(true)
//                                .period(period)
//                                .started(LocalDateTime.now(zoneId))
//                                .promotionRevision(firstRevision)
//                                .build()
//                ));
//
//        firstPromotion.setCurrent(firstRevision);
//
//        secondPromotion.setCurrent(PromotionRevision.builder()
//                .id(2000L)
//                // .maxRedeemable(2)
//                // .maxRedeemableGranularity(granularity)
//                // .sequenceNumber(2)
//                .build());
//
//        context.setPromotions(Arrays.asList(firstPromotion, secondPromotion));
//
////        Assert.assertFalse(userPromotionService.completedPreviousGranularitySequence(context, secondPromotion, period));
//    }
//
//    @Test
//    public void shouldParticipateInMissionWhenAllUserMissionsForThePreviousSequenceHaveBeenCompletedForTheGranularityWeek()
//    {
//        int granurality = Granularity.GRANULARITY_WEEK.granularity();
//
//        Period period = Period.builder()
//                .granularity(granurality)
//                .build();
//
//        context.setUserZonedDateTime(context.getUserZonedDateTime().plusDays(7));
//
//        PromotionRevision firstRevision = PromotionRevision.builder()
//                .id(1000L)
//                // .maxRedeemable(1)
//                // .maxRedeemableGranularity(granurality)
//                // .granularityStartOffset(1)
//                // .duration(1)
//                // .sequenceNumber(1)
//                .build();
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotionAndPeriod(Mockito.any(), Mockito.any(Promotion.class), Mockito.any(Period.class)))
//                .thenReturn(Arrays.asList(
//                        UserPromotion.builder()
//                                .promotionComplete(true)
//                                .period(period)
//                                .started(LocalDateTime.now(zoneId).minusDays(1))
//                                .promotionRevision(firstRevision)
//                                .build()
//                ));
//
//        firstPromotion.setCurrent(firstRevision);
//
//        secondPromotion.setCurrent(PromotionRevision.builder()
//                .id(2000L)
//                // .maxRedeemableGranularity(granurality)
//                // .maxRedeemable(2)
//                // .sequenceNumber(2)
//                .build());
//
//        context.setPromotions(Arrays.asList(firstPromotion, secondPromotion));
//
////        Assert.assertTrue(userPromotionService.completedPreviousGranularitySequence(context, secondPromotion, period));
//    }
//
//    @Test
//    public void shouldNotParticipateInMissionWhenAllUserMissionsForThePreviousSequenceHaveNotBeenCompletedForTheGranularityWeek()
//    {
//
//        int granularity = Granularity.GRANULARITY_WEEK.granularity();
//
//        Period period = Period.builder()
//                .granularity(granularity)
//                .build();
//
//        PromotionRevision firstRevision = PromotionRevision.builder()
//                .id(1000L)
//                // .maxRedeemable(1)
//                // .maxRedeemableGranularity(granularity)
//                // .duration(1)
//                // .sequenceNumber(1)
//                // .granularityStartOffset(1)
//                .build();
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotionAndPeriod(Mockito.any(), Mockito.any(Promotion.class), Mockito.any(Period.class)))
//                .thenReturn(Arrays.asList(
//                        UserPromotion.builder()
//                                .promotionComplete(true)
//                                .period(period)
//                                .started(LocalDateTime.now(zoneId))
//                                .promotionRevision(firstRevision)
//                                .build()
//                ));
//
//        firstPromotion.setCurrent(firstRevision);
//
//        secondPromotion.setCurrent(PromotionRevision.builder()
//                .id(2000L)
//                // .maxRedeemable(2)
//                // .maxRedeemableGranularity(granularity)
//                // .sequenceNumber(2)
//                .build());
//
//        context.setPromotions(Arrays.asList(firstPromotion, secondPromotion));
//
////        Assert.assertFalse(userPromotionService.completedPreviousGranularitySequence(context, secondPromotion, period));
//    }
//
//    @Test
//    public void shouldParticipateInMissionWhenAllUserMissionsForThePreviousSequenceHaveBeenCompletedForTheGranularityMonth()
//    {
//        int granurality = Granularity.GRANULARITY_MONTH.granularity();
//
//        Period period = Period.builder()
//                .granularity(granurality)
//                .build();
//
//        context.setUserZonedDateTime(context.getUserZonedDateTime().plusMonths(1));
//
//        PromotionRevision firstRevision = PromotionRevision.builder()
//                .id(1000L)
//                // .maxRedeemable(1)
//                // .maxRedeemableGranularity(granurality)
//                // .granularityStartOffset(1)
//                // .duration(1)
//                // .sequenceNumber(1)
//                .build();
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotionAndPeriod(Mockito.any(), Mockito.any(Promotion.class), Mockito.any(Period.class)))
//                .thenReturn(Arrays.asList(
//                        UserPromotion.builder()
//                                .promotionComplete(true)
//                                .period(period)
//                                .started(LocalDateTime.now(zoneId).minusDays(1))
//                                .promotionRevision(firstRevision)
//                                .build()
//                ));
//
//        firstPromotion.setCurrent(firstRevision);
//
//        secondPromotion.setCurrent(PromotionRevision.builder()
//                .id(2000L)
//                // .maxRedeemableGranularity(granurality)
//                // .maxRedeemable(2)
//                // .sequenceNumber(2)
//                .build());
//
//        context.setPromotions(Arrays.asList(firstPromotion, secondPromotion));
//
////        Assert.assertTrue(userPromotionService.completedPreviousGranularitySequence(context, secondPromotion, period));
//    }
//
//    @Test
//    public void shouldNotParticipateInMissionWhenAllUserMissionsForThePreviousSequenceHaveNotBeenCompletedForTheGranularityMonth()
//    {
//
//        int granularity = Granularity.GRANULARITY_MONTH.granularity();
//
//        Period period = Period.builder()
//                .granularity(granularity)
//                .build();
//
//        PromotionRevision firstRevision = PromotionRevision.builder()
//                .id(1000L)
//                // .maxRedeemable(1)
//                // .maxRedeemableGranularity(granularity)
//                // .duration(1)
//                // .sequenceNumber(1)
//                // .granularityStartOffset(3)
//                .build();
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotionAndPeriod(Mockito.any(), Mockito.any(Promotion.class), Mockito.any(Period.class)))
//                .thenReturn(Arrays.asList(
//                        UserPromotion.builder()
//                                .promotionComplete(true)
//                                .period(period)
//                                .started(LocalDateTime.now(zoneId))
//                                .promotionRevision(firstRevision)
//                                .build()
//                ));
//
//        firstPromotion.setCurrent(firstRevision);
//
//        secondPromotion.setCurrent(PromotionRevision.builder()
//                .id(2000L)
//                // .maxRedeemable(2)
//                // .maxRedeemableGranularity(granularity)
//                // .sequenceNumber(2)
//                .build());
//
//        context.setPromotions(Arrays.asList(firstPromotion, secondPromotion));
//
////        Assert.assertFalse(userPromotionService.completedPreviousGranularitySequence(context, secondPromotion, period));
//    }
//
//    @Test
//    public void shouldParticipateInMissionWhenAllUserMissionsForThePreviousSequenceHaveBeenCompletedForTheGranularityYear()
//    {
//        context.setUserZonedDateTime(context.getUserZonedDateTime().plusYears(1));
//        int granurality = Granularity.GRANULARITY_YEAR.granularity();
//
//        Period period = Period.builder()
//                .granularity(granurality)
//                .build();
//
//        PromotionRevision firstRevision = PromotionRevision.builder()
//                .id(1000L)
//                // .maxRedeemable(1)
//                // .maxRedeemableGranularity(granurality)
//                // .granularityStartOffset(1)
//                // .duration(1)
//                // .sequenceNumber(1)
//                .build();
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotionAndPeriod(Mockito.any(), Mockito.any(Promotion.class), Mockito.any(Period.class)))
//                .thenReturn(Arrays.asList(
//                        UserPromotion.builder()
//                                .promotionComplete(true)
//                                .period(period)
//                                .started(LocalDateTime.now(zoneId).minusDays(1))
//                                .promotionRevision(firstRevision)
//                                .build()
//                ));
//
//        firstPromotion.setCurrent(firstRevision);
//
//        secondPromotion.setCurrent(PromotionRevision.builder()
//                .id(2000L)
//                // .maxRedeemableGranularity(granurality)
//                // .maxRedeemable(2)
//                // .sequenceNumber(2)
//                .build());
//
//        context.setPromotions(Arrays.asList(firstPromotion, secondPromotion));
//
////        Assert.assertTrue(userPromotionService.completedPreviousGranularitySequence(context, secondPromotion, period));
//    }
//
//    @Test
//    public void shouldNotParticipateInMissionWhenAllUserMissionsForThePreviousSequenceHaveNotBeenCompletedForTheGranularityYear()
//    {
//
//        int granularity = Granularity.GRANULARITY_YEAR.granularity();
//
//        Period period = Period.builder()
//                .granularity(granularity)
//                .build();
//
//        PromotionRevision firstRevision = PromotionRevision.builder()
//                .id(1000L)
//                // .maxRedeemable(1)
//                // .maxRedeemableGranularity(granularity)
//                // .duration(1)
//                // .sequenceNumber(1)
//                // .granularityStartOffset(3)
//                .build();
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotionAndPeriod(Mockito.any(), Mockito.any(Promotion.class), Mockito.any(Period.class)))
//                .thenReturn(Arrays.asList(
//                        UserPromotion.builder()
//                                .promotionComplete(true)
//                                .period(period)
//                                .started(LocalDateTime.now(zoneId))
//                                .promotionRevision(firstRevision)
//                                .build()
//                ));
//
//        firstPromotion.setCurrent(firstRevision);
//
//        secondPromotion.setCurrent(PromotionRevision.builder()
//                .id(2000L)
//                // .maxRedeemable(2)
//                // .maxRedeemableGranularity(granularity)
//                // .sequenceNumber(2)
//                .build());
//
//        context.setPromotions(Arrays.asList(firstPromotion, secondPromotion));
//
////        Assert.assertFalse(userPromotionService.completedPreviousGranularitySequence(context, secondPromotion, period));
//    }
//
//
//    @Test
//    public void shouldCreateNewUserMissionWhenMaxRedeemableIsNotMet() {
//
//        context.setUserZonedDateTime(context.getUserZonedDateTime().plusYears(1));
//        int granurality = Granularity.GRANULARITY_YEAR.granularity();
//        // firstRevision.setMaxRedeemableGranularity(granurality);
//
//        Period period = Period.builder()
//                .granularity(granurality)
//                .build();
//
//
//
//        Mockito.when(periodService.findOrCreatePeriod(Mockito.any(DateTime.class), Mockito.any(Domain.class), Mockito.any(), Mockito.anyInt()))
//                .thenReturn(period);
//
//        Mockito.when(self.lockingUpdate(Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong()))
//                .thenReturn(Arrays.asList(
//                        UserPromotion.builder()
//                                .promotionComplete(true)
//                                .period(period)
//                                .started(LocalDateTime.now(zoneId).minusDays(1))
//                                .promotionRevision(firstRevision)
//                                .build()
//                ));
//        Mockito.when(userMissionRepository.save(Mockito.any(UserPromotion.class))).thenReturn(
//                UserPromotion.builder()
//                        .promotionComplete(false)
//                        .period(period)
//                        .started(LocalDateTime.now(zoneId).minusDays(1))
//                        .promotionRevision(firstRevision)
//                        .build()
//        );
//
//        firstPromotion.setCurrent(firstRevision);
//
//        secondPromotion.setCurrent(secondRevision);
//
//        context.setPromotions(Arrays.asList(firstPromotion));
//
//        userPromotionService.findUserPromotions(context);
//
//        Assert.assertEquals(1, context.getUserPromotions().size());
//    }
//
//    @Test
//    public void shouldNotCreateNewUserMissionWhenMaxRedeemableIsMet() {
//
//        context.setUserZonedDateTime(context.getUserZonedDateTime().plusYears(1));
//        int granurality = Granularity.GRANULARITY_YEAR.granularity();
//        // firstRevision.setMaxRedeemableGranularity(granurality);
//        // firstRevision.setMaxRedeemable(1);
//
//        Period period = Period.builder()
//                .granularity(granurality)
//                .build();
//
//        Mockito.when(periodService.findOrCreatePeriod(Mockito.any(DateTime.class), Mockito.any(Domain.class), Mockito.any(), Mockito.anyInt()))
//                .thenReturn(period);
//
//        Mockito.when(self.lockingUpdate(Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong()))
//                .thenReturn(Arrays.asList(
//                        UserPromotion.builder()
//                                .promotionComplete(true)
//                                .period(period)
//                                .started(LocalDateTime.now(zoneId).minusDays(1))
//                                .promotionRevision(firstRevision)
//                                .build()
//                ));
//        Mockito.when(userMissionRepository.save(Mockito.any(UserPromotion.class))).thenReturn(
//                UserPromotion.builder()
//                        .promotionComplete(false)
//                        .period(period)
//                        .started(LocalDateTime.now(zoneId).minusDays(1))
//                        .promotionRevision(firstRevision)
//                        .build()
//        );
//
//        firstPromotion.setCurrent(firstRevision);
//
//        secondPromotion.setCurrent(secondRevision);
//
//        context.setPromotions(Arrays.asList(firstPromotion));
//
//        userPromotionService.findUserPromotions(context);
//
//        Assert.assertNull(context.getUserPromotions());
//    }
//
//    @Test
//    public void shouldNotCreateNewUserMissionWhenThePreviousSequenceMissionsAreNotCompleted() {
//        int granurality = Granularity.GRANULARITY_DAY.granularity();
//        // firstRevision.setMaxRedeemableGranularity(granurality);
//        // firstRevision.setMaxRedeemable(1);
//        // secondRevision.setGranularityStartOffset(1);
//        // secondRevision.setMaxRedeemableGranularity(granurality);
//
//
//        Period period = Period.builder()
//                .granularity(granurality)
//                .build();
//
//        Mockito.when(periodService.findOrCreatePeriod(Mockito.any(DateTime.class), Mockito.any(Domain.class), Mockito.any(), Mockito.anyInt()))
//                .thenReturn(period);
//
//        UserPromotion incomplete = UserPromotion.builder()
//                .promotionComplete(false)
//                .period(period)
//                .started(LocalDateTime.now(zoneId))
//                .promotionRevision(firstRevision)
//                .build();
//
//        Mockito.when(self.lockingUpdate(Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong()))
//                .thenReturn(Arrays.asList(incomplete), new ArrayList<UserPromotion>(), Arrays.asList(incomplete));
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotion(Mockito.anyString(), Mockito.any(Promotion.class)))
//                        .thenReturn(Arrays.asList(incomplete));
//
//        Mockito.when(userMissionRepository.save(Mockito.any(UserPromotion.class))).thenReturn(
//                UserPromotion.builder()
//                        .promotionComplete(false)
//                        .period(period)
//                        .started(LocalDateTime.now(zoneId).minusDays(1))
//                        .promotionRevision(firstRevision)
//                        .build()
//        );
//
//        firstPromotion.setCurrent(firstRevision);
//
//        secondPromotion.setCurrent(secondRevision);
//
//        context.setPromotions(Arrays.asList(firstPromotion, secondPromotion));
//
//        userPromotionService.findUserPromotions(context);
//
//        Assert.assertEquals(1, context.getUserPromotions().size());
//    }
//
//    @Test
//    public void shouldCreateNewUserMissionWhenThePreviousSequenceMissionsAreCompleted() {
//        int granularity = Granularity.GRANULARITY_DAY.granularity();
//        // firstRevision.setMaxRedeemableGranularity(granularity);
//        // firstRevision.setMaxRedeemable(1);
//        // secondRevision.setGranularityStartOffset(1);
//        // secondRevision.setMaxRedeemableGranularity(granularity);
//        // secondRevision.setGranularityBreak(false);
//
//
//        Period period = Period.builder()
//                .granularity(granularity)
//                .build();
//
//        Mockito.when(periodService.findOrCreatePeriod(Mockito.any(DateTime.class), Mockito.any(Domain.class), Mockito.any(), Mockito.anyInt()))
//                .thenReturn(period);
//
//        UserPromotion complete = UserPromotion.builder()
//                .promotionComplete(true)
//                .period(period)
//                .started(LocalDateTime.now(zoneId))
//                .promotionRevision(firstRevision)
//                .build();
//
//        Mockito.when(self.lockingUpdate(Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong()))
//                .thenReturn(Arrays.asList(complete), new ArrayList<UserPromotion>(), Arrays.asList(complete));
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotion(Mockito.anyString(), Mockito.any(Promotion.class)))
//                .thenReturn(Arrays.asList(complete));
//
//        Mockito.when(userMissionRepository.save(Mockito.any(UserPromotion.class))).thenReturn(
//                UserPromotion.builder()
//                        .promotionComplete(false)
//                        .period(period)
//                        .started(LocalDateTime.now(zoneId).minusDays(1))
//                        .promotionRevision(firstRevision)
//                        .build()
//        );
//
//        firstPromotion.setCurrent(firstRevision);
//
//        secondPromotion.setCurrent(secondRevision);
//
//        context.setPromotions(Arrays.asList(firstPromotion, secondPromotion));
//
//        userPromotionService.findUserPromotions(context);
//
//        Assert.assertEquals(1, context.getUserPromotions().size());
//    }
//
//    @Test
//    public void shouldNotCreateNewUserMissionWhenGranularityBreakIsNotOver() {
//        int granurality = Granularity.GRANULARITY_DAY.granularity();
//        // firstRevision.setMaxRedeemableGranularity(granurality);
//        // firstRevision.setMaxRedeemable(1);
//        // secondRevision.setGranularityStartOffset(1);
//        // secondRevision.setMaxRedeemableGranularity(granurality);
//        // secondRevision.setGranularityBreak(true);
//
//
//        Period period = Period.builder()
//                .granularity(granurality)
//                .build();
//
//        Mockito.when(periodService.findOrCreatePeriod(Mockito.any(DateTime.class), Mockito.any(Domain.class), Mockito.any(), Mockito.anyInt()))
//                .thenReturn(period);
//
//        UserPromotion complete = UserPromotion.builder()
//                .promotionComplete(true)
//                .period(period)
//                .started(LocalDateTime.now(zoneId))
//                .promotionRevision(firstRevision)
//                .build();
//
//        Mockito.when(self.lockingUpdate(Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong()))
//                .thenReturn(Arrays.asList(complete), new ArrayList<UserPromotion>(), Arrays.asList(complete));
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotion(Mockito.anyString(), Mockito.any(Promotion.class)))
//                .thenReturn(Arrays.asList(complete));
//
//        Mockito.when(userMissionRepository.save(Mockito.any(UserPromotion.class))).thenReturn(
//                UserPromotion.builder()
//                        .promotionComplete(false)
//                        .period(period)
//                        .started(LocalDateTime.now(zoneId).minusDays(1))
//                        .promotionRevision(firstRevision)
//                        .build()
//        );
//
//        firstPromotion.setCurrent(firstRevision);
//
//        secondPromotion.setCurrent(secondRevision);
//
//        context.setPromotions(Arrays.asList(firstPromotion, secondPromotion));
//
//        userPromotionService.findUserPromotions(context);
//
//        Assert.assertNull(context.getUserPromotions());
//    }
//
//    @Test
//    public void shouldCreateNewUserMissionWhenGranularityBreakIsOver() {
//        int granurality = Granularity.GRANULARITY_DAY.granularity();
//        // firstRevision.setMaxRedeemableGranularity(granurality);
//        // firstRevision.setMaxRedeemable(1);
//        // secondRevision.setGranularityStartOffset(1);
//        // secondRevision.setMaxRedeemableGranularity(granurality);
//        // secondRevision.setGranularityBreak(true);
//
//
//        Period period = Period.builder()
//                .granularity(granurality)
//                .build();
//
//        Mockito.when(periodService.findOrCreatePeriod(Mockito.any(DateTime.class), Mockito.any(Domain.class), Mockito.any(), Mockito.anyInt()))
//                .thenReturn(period);
//
//        UserPromotion complete = UserPromotion.builder()
//                .promotionComplete(true)
//                .period(period)
//                .started(LocalDateTime.now(zoneId).minusDays(2))
//                .promotionRevision(firstRevision)
//                .build();
//
//        Mockito.when(self.lockingUpdate(Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong()))
//                .thenReturn(Arrays.asList(complete), new ArrayList<UserPromotion>(), Arrays.asList(complete));
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotionAndPeriod(Mockito.anyString(), Mockito.any(Promotion.class), Mockito.any(Period.class)))
//                .thenReturn(Arrays.asList(complete), new ArrayList<UserPromotion>(), Arrays.asList(complete));
//
//        Mockito.when(userMissionRepository.findByUserGuidAndPromotionRevisionPromotion(Mockito.anyString(), Mockito.any(Promotion.class)))
//                .thenReturn(Arrays.asList(complete));
//
//        Mockito.when(userMissionRepository.save(Mockito.any(UserPromotion.class))).thenReturn(
//                UserPromotion.builder()
//                        .promotionComplete(false)
//                        .period(period)
//                        .started(LocalDateTime.now(zoneId).minusDays(1))
//                        .promotionRevision(firstRevision)
//                        .build()
//        );
//
//        firstPromotion.setCurrent(firstRevision);
//
//        secondPromotion.setCurrent(secondRevision);
//
//        context.setPromotions(Arrays.asList(firstPromotion, secondPromotion));
//
//        userPromotionService.findUserPromotions(context);
//
//        Assert.assertEquals(1, context.getUserPromotions().size());
//    }
//}
//
//

import lithium.service.promo.data.entities.Challenge;
import lithium.service.promo.data.entities.ChallengeGroup;
import lithium.service.promo.data.entities.UserPromotionChallenge;
import lithium.service.promo.data.entities.UserPromotionChallengeGroup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
public class UserPromotionServiceTest {
    private final UserPromotionService userPromotionService = new UserPromotionService();

    @Mock
    private Logger logger;

    @Test
    public void shouldNotParticipateInChallengeWhenInSequencedGroupWithoutCompletingPreviousChallenge() {

        ChallengeGroup challengeGroup = ChallengeGroup.builder()
                .challenges(new ArrayList<>())
                .sequenced(true)
                .build();

        Challenge firstChallenge = Challenge.builder()
                .description("Win R1000 in Roxor Games")
                .sequenceNumber(2)
                .challengeGroup(challengeGroup)
                .build();

        Challenge secondChallenge = Challenge.builder()
                .description("Login 10 times on Friday")
                .sequenceNumber(1)
                .challengeGroup(challengeGroup)
                .build();

        challengeGroup.getChallenges().add(firstChallenge);
        challengeGroup.getChallenges().add(secondChallenge);

        UserPromotionChallengeGroup userPromotionChallengeGroup = UserPromotionChallengeGroup
                .builder()
                .challengeGroup(challengeGroup)
                .userPromotionChallenges(Arrays.asList(
                        UserPromotionChallenge.builder()
                                .challenge(firstChallenge)
                                .build(),
                        UserPromotionChallenge.builder()
                                .challenge(secondChallenge)
                                .build()))
                .challengeGroup(challengeGroup)
                .build();

        UserPromotionChallenge userPromotionChallenge = userPromotionChallengeGroup.getUserPromotionChallenges().get(0);

        boolean result = userPromotionService.doesChallengeHavePrerequisiteChallenges(userPromotionChallengeGroup, userPromotionChallenge);

        Assert.assertTrue(result);
    }

    @Test
    public void shouldParticipateInChallengeWhenInSequencedGroupHavingCompletedPreviousChallenge() {

        ChallengeGroup challengeGroup = ChallengeGroup.builder()
                .challenges(new ArrayList<>())
                .sequenced(true)
                .build();

        Challenge firstChallenge = Challenge.builder()
                .id(1L)
                .description("Win R1000 in Roxor Games")
                .sequenceNumber(2)
                .challengeGroup(challengeGroup)
                .build();

        Challenge secondChallenge = Challenge.builder()
                .id(2L)
                .description("Login 10 times on Friday")
                .sequenceNumber(1)
                .challengeGroup(challengeGroup)
                .build();

        challengeGroup.getChallenges().add(firstChallenge);
        challengeGroup.getChallenges().add(secondChallenge);

        UserPromotionChallengeGroup userPromotionChallengeGroup = UserPromotionChallengeGroup
                .builder()
                .challengeGroup(challengeGroup)
                .userPromotionChallenges(Arrays.asList(
                        UserPromotionChallenge.builder()
                                .challenge(firstChallenge)
                                .build(),
                        UserPromotionChallenge.builder()
                                .challenge(secondChallenge)
                                .challengeComplete(true)
                                .build()))
                .build();

        UserPromotionChallenge userPromotionChallenge = userPromotionChallengeGroup.getUserPromotionChallenges().get(0);

        boolean result = userPromotionService.doesChallengeHavePrerequisiteChallenges(userPromotionChallengeGroup, userPromotionChallenge);

        Assert.assertFalse(result);
    }


    @Test
    public void shouldParticipateInChallengeNotWhenInSequencedGroup() {

        ChallengeGroup challengeGroup = ChallengeGroup.builder()
                .challenges(new ArrayList<>())
                .sequenced(false)
                .build();

        Challenge firstChallenge = Challenge.builder()
                .description("Win R1000 in Roxor Games")
                .sequenceNumber(2)
                .challengeGroup(challengeGroup)
                .build();

        Challenge secondChallenge = Challenge.builder()
                .description("Login 10 times on Friday")
                .sequenceNumber(1)
                .challengeGroup(challengeGroup)
                .build();

        challengeGroup.getChallenges().add(firstChallenge);
        challengeGroup.getChallenges().add(secondChallenge);

        UserPromotionChallengeGroup userPromotionChallengeGroup = UserPromotionChallengeGroup
                .builder()
                .challengeGroup(challengeGroup)
                .userPromotionChallenges(Arrays.asList(
                        UserPromotionChallenge.builder()
                                .challenge(firstChallenge)
                                .build(),
                        UserPromotionChallenge.builder()
                                .challenge(secondChallenge)
                                .build()))
                .build();

        UserPromotionChallenge userPromotionChallenge = userPromotionChallengeGroup.getUserPromotionChallenges().get(0);

        boolean result = userPromotionService.doesChallengeHavePrerequisiteChallenges(userPromotionChallengeGroup, userPromotionChallenge);

        Assert.assertFalse(result);
    }

    @BeforeAll
    public void setup() throws ReflectiveOperationException {
        UserPromotionServiceTest.setFinalStaticField(UserPromotionService.class, "log", logger);

    }

    private static void setFinalStaticField(Class<?> clazz, String fieldName, Object value)
            throws ReflectiveOperationException {

        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);

        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, value);
    }
}

