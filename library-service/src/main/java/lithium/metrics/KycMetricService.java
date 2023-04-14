package lithium.metrics;

import lithium.metrics.builders.kyc.EntryPoint;
import lithium.metrics.builders.kyc.VerificationAttemptMetric;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static lithium.metrics.builders.kyc.KycStatus.FAILED;
import static lithium.metrics.builders.kyc.KycStatus.PASS;
import static lithium.metrics.builders.kyc.KycStatus.PENDING;
import static lithium.metrics.builders.kyc.KycStatus.TOTAL;

@Service
@Slf4j
@AllArgsConstructor
public class KycMetricService {
    private final LithiumMetricsService metricsService;

    public void passAttemptMetrics(String domainName, EntryPoint entryPoint, String providerName) {
        metricsService.increment(VerificationAttemptMetric.builder()
                .domain(domainName)
                .entryPoint(entryPoint)
                .status(PASS)
                .provider(providerName));
        metricsService.decrement(VerificationAttemptMetric.builder()
                .domain(domainName)
                .entryPoint(entryPoint)
                .status(PENDING)
                .provider(providerName));
    }

    public void failAttemptMetrics(String domainName, EntryPoint entryPoint, String providerName) {
        metricsService.increment(VerificationAttemptMetric.builder()
                .domain(domainName)
                .entryPoint(entryPoint)
                .status(FAILED)
                .provider(providerName));
        metricsService.decrement(VerificationAttemptMetric.builder()
                .domain(domainName)
                .entryPoint(entryPoint)
                .status(PENDING)
                .provider(providerName));
    }

    public void startAttemptMetrics(String domainName, EntryPoint entryPoint, String providerName) {
        metricsService.increment(VerificationAttemptMetric.builder()
                .domain(domainName)
                .entryPoint(entryPoint)
                .status(TOTAL)
                .provider(providerName));
        metricsService.increment(VerificationAttemptMetric.builder()
                .domain(domainName)
                .entryPoint(entryPoint)
                .status(PENDING)
                .provider(providerName));
    }
}
