package factory;

import interfaces.IPool;
import interfaces.IPoolFactory;
import poolsImpl.ConnectAndAddHighPerfomancePoolImpl;

/**
 * Базовая имплементация {@code IPoolFactory}
 *
 * @inheritDoc
 */
public class PoolFactoryImpl implements IPoolFactory {
    /**
     * @inheritDoc
     */
    @Override
    public IPool create() {
        return new ConnectAndAddHighPerfomancePoolImpl();
    }
}
