package lithium.metrics.builders.kyc;

import lithium.metrics.builders.Metric;
import lombok.ToString;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ToString
public class VerificationAttemptMetric implements Metric {
    private final static String metricName = "verification-attempt";
    private String domainName;
    private EntryPoint entryPoint;
    private String providerName;
    private KycStatus status;

    public static VerificationAttemptMetric builder() {
        return new VerificationAttemptMetric();
    }

    public VerificationAttemptMetric domain(String domainName) {
        this.domainName = domainName;
        return this;
    }

    public VerificationAttemptMetric entryPoint(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;
        return this;
    }

    public VerificationAttemptMetric provider(String providerName) {
        this.providerName = providerName;
        return this;
    }

    public VerificationAttemptMetric status(KycStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public String build() throws Exception {
        final Object[] objects = {metricName, domainName, entryPoint, providerName, status};

        if (Stream.of(objects).noneMatch(Objects::isNull)){
            return Stream.of(objects)
                    .map(Object::toString)
                    .map(this::format)
                    .collect(Collectors.joining("."))
                    .toLowerCase();
        }

        throw new Exception("All fields should be set: " + this.toString());
    }

}
