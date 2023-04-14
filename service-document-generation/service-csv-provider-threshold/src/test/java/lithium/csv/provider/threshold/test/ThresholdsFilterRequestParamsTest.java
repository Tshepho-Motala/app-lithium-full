package lithium.csv.provider.threshold.test;

public class ThresholdsFilterRequestParamsTest {
//    /**
//     * The specified field of ThresholdsFilterRequest not processed by the service-csv-provider-threshold.
//     * To fix:
//     * 1. Add field processing to ThresholdsFilterRequestParams::buildThresholdFilter
//     * 2. Set field into ThresholdsFilterRequestParamsTest::getCompleteThresholdsParams
//     */
//    //@Test
//    @Ignore
//    public void shouldPopulateAllFields() throws Status500InternalServerErrorException {
//
//        ThresholdsFilterRequest actualRequest = ThresholdsFilterRequestParams.buildThresholdFilter(getCompleteThresholdsParams());
//        assertThat(actualRequest).hasNoNullFieldsOrProperties();
//    }
//
//    @Ignore @Test
//    public void shouldOverrideAllDefaults() throws IllegalAccessException, Status500InternalServerErrorException {
//
//        ThresholdsFilterRequest actualRequest = ThresholdsFilterRequestParams.buildThresholdFilter(getCompleteThresholdsParams());
//
//        ThresholdsFilterRequest defaultFilterRequest = new ThresholdsFilterRequest();
//
//        for (Field field : defaultFilterRequest.getClass().getDeclaredFields()) {
//            field.setAccessible(true);
//            Object value = field.get(defaultFilterRequest);
//            if (value != null) {
//                assertThat(value)
//                        .withFailMessage("Field: \\\"%s\\\" of ThresholdsFilterRequest not processed by the service-csv-provider-threshold.\n" +
//                                "To fix:\n" +
//                                "1. Add field processing to ThresholdsFilterRequestParams::buildThresholdFilter\n" +
//                                "2. Set field into ThresholdsFilterRequestParamsTest::getCompleteThresholdsParams", field.getName())
//                        .isNotEqualTo(field.get(actualRequest));
//            }
//        }
//    }
//
//    public static ThresholdsFilterRequestParams getCompleteThresholdsParams() {
//
//        String startDate = "2022-08-28 00:00";
//        String endDate = "2022-12-24 00:00";
//        String[] domains = new String[]{"default", "livescore_nigeria"};
//
//        Map<String, String> parameters = new HashMap<>();
//        parameters.put(START_DATE_TIME, startDate);
//        parameters.put(END_DATE_TIME, endDate);
//        parameters.put(SELECTED_DOMAINS, String.join(",", domains));
//
//        return new ThresholdsFilterRequestParams(parameters);
//    }
}
