package poolsImpl;

import interfaces.IPool;
import utils.PoolsOrchestrator;

/**
 * Имплементация {@code  IPool} эксплуатирующуая идею глобального агрегатора бассейнов
 * <p>
 * Идея в том что интерфейс достаточно скуден и для избавления от typecast все необходимые данные можно хранить
 * в глобальном оркестраторе {@code aggregator} который и будет оркестрировать все действия с есть бассейнов
 */
public class GlobalStatePoolImpl implements IPool {
    /**
     * В принципе он не обязан быть статическим, можно и в конструктор отдавть на уровне фабрики
     */
    private final static PoolsOrchestrator orchestrator = new PoolsOrchestrator();

    /**
     * Флаг оптимизации работы с орекстратором, используется для оптимизации создания новго бассейна
     */
    private boolean isPoolAddedToOrchestrator = false;

    /**
     * Переменная для обработки {@code add} пока бассейн не добавлен в оркестратор
     */
    private long derivedAmountOfWater = 0;

    /**
     * @inheritDoc
     */
    @Override
    public long measure() {
        if (isPoolAddedToOrchestrator) {
            return orchestrator.measurePool(this);
        } else {
            return derivedAmountOfWater;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void connect(IPool pool) {
        orchestrator.mergePools(this, pool);
        isPoolAddedToOrchestrator = true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void add(long water) {
        if (isPoolAddedToOrchestrator) {
            orchestrator.addWaterToPool(this, water);
        } else {
            try {
                derivedAmountOfWater = Math.addExact(derivedAmountOfWater, water);
            } catch (ArithmeticException exception) {
                derivedAmountOfWater = Long.MAX_VALUE;
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public Boolean isConnected(IPool pool) {
        if (isPoolAddedToOrchestrator) {
            return (pool != this) && orchestrator.arePoolConnected(this, pool);
        } else {
            return false;
        }
    }
}
