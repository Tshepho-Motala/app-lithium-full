package lithium.service.document.provider.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportResponse {
    private Bespoke bespoke;
    private IdCheck idcheck;
    private Object scores;

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Bespoke {
        private String addressDecision;
        private String documentDecision;
    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IdCheck {
        private String result;
        private Biographic biographic;
        private Classification classification;
        private Document document;
        private Metrics metrics;
        private Metrics backMetrics;
        private Object alerts;
        private Object dataFields;


        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Biographic {
            private int age;
            private String birthDate;
            private String fullName;
            private String gender;
            private String surname;
            private String address;
            private String birthPlace;
            private String givenName;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Classification {
            private String issue;
            private String issueType;
            private String issuerCode;
            private String issuerName;
            private String issueDate;
            private String issuingAuthority;
            private String issuingStateCode;
            private String issuingStateName;
            private String keesingCode;
            private String className;
            @JsonProperty("class")
            private int classId;
            private String isGeneric;
            private String name;

        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Document {
            private String applicationDate;
            private String documentNumber;
            private String expirationDate;
            private String startDate;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Metrics {
            private Integer glareMetric;
            private Integer sharpnessMetric;
            private Integer verticalResolution;
            private Integer horizontalResolution;
        }
    }
}
