package lithium.csv.provider.threshold.test;




//@ExtendWith(MockitoExtension.class)
public class ThresholdsRecordsServiceTest {
//    @Mock
//    private LithiumServiceClientFactory mockedLithiumServiceClientFactory;
//    @Mock
//    private UserProviderThresholdReportClient mockedClient;
//
//    private ThresholdsRecordsService service;
//
//    @BeforeEach
//    public void init() {
//        try {
//            when(mockedLithiumServiceClientFactory.target(UserProviderThresholdReportClient.class, "service-user-provider-threshold", true)).thenReturn(mockedClient);
//        } catch (LithiumServiceClientFactoryException e) {
//            throw new RuntimeException(e);
//        }
//        this.service = new ThresholdsRecordsService(mockedLithiumServiceClientFactory, new CsvThresholdProviderConfigurationProperties());
//    }
//
//   @Ignore
//   @Test
//    public void shouldBuildCorrectFilterForThresholdClient() throws Status500InternalServerErrorException {
//
//        DataTableResponse dummyResponse = new DataTableResponse<>();
//        dummyResponse.setRecordsTotalPages(1);
//        dummyResponse.setData(new ArrayList<>(List.of(getDummyThresholdTransaction())));
//
//        when(mockedClient.getThresholdHistoryDTOPage(any(), any(), any()))
//                .thenReturn(dummyResponse);
//
//        ThresholdsFilterRequestParams commandParams = getCompleteThresholdsParams();
//
//        CsvDataResponse actualResponse = service.getCsvData(commandParams, 0);
//
//        CsvDataResponse expectedResponse = getExpectedCsvResponse();
//
//        assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
//    }
//
//    private static ThresholdHistoryDTO getDummyThresholdTransaction() {
//        ThresholdHistoryBOUser user = new ThresholdHistoryBOUser();
//        user.setGuid("test/guid");
//        user.setName("testUserName");
//
//        ThresholdRevision revision = new ThresholdRevision();
//        //revision.setGranularity(3);
//        revision.setPercentage(BigDecimal.valueOf(50.00));
//
//        ThresholdHistoryDTO historyDTO = ThresholdHistoryDTO.builder()
//                .user(user)
//                .thresholdHitDate("1668515180000")
//                .dailyLimit("50.00")
//                .dailyLimitUsed("52.53")
//                .amount(BigDecimal.valueOf(20.00))
//                .thresholdRevision(revision)
//                .build();
//
//        return historyDTO;
//    }
//
//    private static CsvDataResponse getExpectedCsvResponse() {
//        ThresholdHistoryCsv csv = ThresholdHistoryCsv.builder()
//                .thresholdHitDate(getDate("1668515180000"))
//                .accountId("testUserName")
//                .guid("test/guid")
//                .thresholdHit("Daily")
//                .dailyLimit("50.00")
//                .dailyThreshold("50%")
//                .dailyLimitUsed("52.53")
//                .weeklyLimit("")
//                .weeklyThreshold("50%")
//                .weeklyLimitUsed("")
//                .monthlyLimit("")
//                .monthlyThreshold("50%")
//                .monthlyLimitUsed("")
//                .build();
//
//        return CsvDataResponse.builder().data(new ArrayList<>(List.of(csv))).pages(1).build();
//    }
//
//    private static String getDate(String milliseconds) {
//        DateFormat simple = new SimpleDateFormat(
//                "dd MMM yyyy HH:mm:ss");
//        Date result = new Date(Long.parseLong(milliseconds));
//        return simple.format(result);
//    }
}

