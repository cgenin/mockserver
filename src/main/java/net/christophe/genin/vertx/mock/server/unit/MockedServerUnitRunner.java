package net.christophe.genin.vertx.mock.server.unit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

public class MockedServerUnitRunner extends BlockJUnit4ClassRunner {

    private final TestClass testClass;

    public MockedServerUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
        testClass = new TestClass(klass);
    }

    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        // TODO
        return super.methodBlock(method);
    }
}
