package factory;

import interfaces.IPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import poolsImpl.MeasureHighPerformancePoolImpl;

/**
 * Тест для {@code PoolFactoryImpl}
 */
public class PoolFactoryImplTest {

    @Test
    public void verifyCreate() {
        PoolFactoryImpl factory = new PoolFactoryImpl();

        IPool actual = factory.create();

        Assertions.assertNotNull(actual);
        Assertions.assertEquals (IS_INSTANCE_OF,actual instanceof MeasureHighPerformancePoolImpl);
    }

    private static final boolean IS_INSTANCE_OF = true;
}
