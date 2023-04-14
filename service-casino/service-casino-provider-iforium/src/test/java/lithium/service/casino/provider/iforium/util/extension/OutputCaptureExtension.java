package lithium.service.casino.provider.iforium.util.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/*
 using copy of SpringBoot implementation to prevent importing whole spring-boot-test dependency
 which can cause issue with our existing tests as this class exists on next major release only
 https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-test/src/main/java/org/springframework/boot/test/system/OutputCaptureExtension.java
 */
public class OutputCaptureExtension
        implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

    OutputCaptureExtension() {

    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        getOutputCapture(context).push();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        getOutputCapture(context).pop();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        getOutputCapture(context).push();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        getOutputCapture(context).pop();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return CapturedOutput.class.equals(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return getOutputCapture(extensionContext);
    }

    private OutputCapture getOutputCapture(ExtensionContext context) {
        return getStore(context).getOrComputeIfAbsent(OutputCapture.class);
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass()));
    }

}
