package org.technoready.meliecommerce.config;

import org.junit.platform.engine.discovery.ClassNameFilter;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("test")
@Order(1)
@ConditionalOnProperty(
        name = "auto-test.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class AutoTestRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AutoTestRunner.class);
    private static volatile boolean hasRun = false;

    @Value("${auto-test.exit-after-tests:false}")
    private boolean exitAfterTests;

    // Usar String con separador de comas en lugar de List
    @Value("${auto-test.packages:org.technoready.meliecommerce.integration,org.technoready.meliecommerce.service_unit}")
    private String testPackagesString;

    @Override
    public void run(String... args) {
        if (hasRun) {
            logger.debug("AutoTestRunner already executed, skipping...");
            return;
        }

        hasRun = true;
        System.setProperty("auto-test.running", "true");

        // Convertir String a List
        List<String> testPackages = Arrays.asList(testPackagesString.split(","));

        logger.info("========================================");
        logger.info("AUTO TEST RUNNER ACTIVATED");
        logger.info("========================================");
        logger.info("Test packages: {}", testPackages);
        logger.info("Exit after tests: {}", exitAfterTests);
        logger.info("");

        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        LauncherDiscoveryRequestBuilder requestBuilder = LauncherDiscoveryRequestBuilder.request();

        testPackages.forEach(pkg -> {
            String trimmedPkg = pkg.trim();
            logger.info("Adding package to scan: {}", trimmedPkg);
            requestBuilder.selectors(DiscoverySelectors.selectPackage(trimmedPkg));
        });

        LauncherDiscoveryRequest request = requestBuilder
                .filters(ClassNameFilter.includeClassNamePatterns(".*Test$", ".*Tests$"))
                .build();

        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);

        logger.info("Executing tests...");
        logger.info("");

        try {
            launcher.execute(request);
        } catch (Exception e) {
            logger.error("Error executing tests: {}", e.getMessage(), e);
        }

        TestExecutionSummary summary = listener.getSummary();

        logger.info("");
        logger.info("========================================");
        logger.info("TESTS SUMMARY");
        logger.info("========================================");
        logger.info("Tests found:     {}", summary.getTestsFoundCount());
        logger.info("Tests executed:  {}", summary.getTestsStartedCount());
        logger.info("Tests succeeded: {}", summary.getTestsSucceededCount());
        logger.info("Tests failed:    {}", summary.getTestsFailedCount());
        logger.info("Tests skipped:   {}", summary.getTestsSkippedCount());
        logger.info("Duration:        {} ms", summary.getTimeFinished() - summary.getTimeStarted());
        logger.info("========================================");

        if (summary.getTestsFailedCount() > 0) {
            logger.error("");
            logger.error("FAILED TESTS:");
            summary.getFailures().forEach(failure -> {
                logger.error("  - {}", failure.getTestIdentifier().getDisplayName());
                logger.error("    Reason: {}", failure.getException().getMessage());
            });
            logger.error("");
        }

        if (summary.getTestsFoundCount() == 0) {
            logger.warn("WARNING: No tests were found in packages: {}", testPackages);
            logger.warn("Make sure you're running with: mvn clean test-compile exec:java -Ptest");
        }

        System.clearProperty("auto-test.running");

        if (exitAfterTests) {
            logger.info("Exiting application after tests");
            int exitCode = summary.getTestsFailedCount() > 0 ? 1 : 0;
            System.exit(exitCode);
        } else {
            logger.info("Application will continue running");
            logger.info("");
        }
    }
}